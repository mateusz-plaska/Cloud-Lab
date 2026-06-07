package org.pwr.cloud.lab.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import tools.jackson.databind.json.JsonMapper;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class QrGeneratorHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String IMAGE_CONTENT_TYPE = "image/png";
    private static final String IMAGE_FORMAT_PNG = "PNG";
    private static final Integer IMAGE_SIZE = 300;
    private static final String IMAGE_FILE_EXTENSION = "png";

    private final String BUCKET_NAME = System.getenv("S3_BUCKET_NAME");
    private final String DYNAMO_TABLE_NAME = System.getenv("DYNAMO_TABLE_NAME");

    private final JsonMapper jsonMapper = new JsonMapper();

    private final S3Client s3Client = S3Client.create();

    private final DynamoDbClient dynamoDbClient = DynamoDbClient.create();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            var requestBody = request.getBody();
            var qrRequest = jsonMapper.readValue(requestBody, QrRequest.class);
            var url = qrRequest.url().trim();

            context.getLogger().log("Received URL: " + url);

            validateUrl(url);

            var qrImageBytes = generateQrCodeImage(url);
            var urlHash = generateSha256Hash(url);
            var qrFileName = urlHash + "." + IMAGE_FILE_EXTENSION;

            saveToDynamoDb(url, urlHash, qrFileName, qrImageBytes.length);

            var publicQrImageUrl = uploadToS3(qrFileName, qrImageBytes);

            return createSuccessResponse(200, publicQrImageUrl, "QR code generated successfully");
        } catch (IllegalArgumentException e) {
            context.getLogger().log("Validation Error: " + e.getMessage());
            return createErrorResponse(400, "Invalid Input: " + e.getMessage());
        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            return createErrorResponse(500, "Internal Function Error: " + e.getMessage());
        }
    }

    private void validateUrl(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        if (url.length() > 2048) {
            throw new IllegalArgumentException("URL exceeds maximum length of 2048 characters");
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalArgumentException("URL must start with http:// or https://");
        }
    }

    private byte[] generateQrCodeImage(String content) throws Exception {
        var qrCodeWriter = new QRCodeWriter();
        var bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, IMAGE_SIZE, IMAGE_SIZE);

        var pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, IMAGE_FORMAT_PNG, pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    private String generateSha256Hash(String content) throws Exception {
        var digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(content.getBytes(StandardCharsets.UTF_8));

        var hexString = new StringBuilder(2 * hashBytes.length);
        for (byte b : hashBytes) {
            var hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private void saveToDynamoDb(String url, String hash, String fileName, int fileSize) {
        var now = Instant.now();
        var expiration = now.plus(30, ChronoUnit.DAYS);

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("url_hash", AttributeValue.builder().s(hash).build());
        item.put("url", AttributeValue.builder().s(url).build());
        item.put("file_name", AttributeValue.builder().s(fileName).build());
        item.put(
                "file_extension",
                AttributeValue.builder().s(IMAGE_FILE_EXTENSION).build());
        item.put(
                "file_size",
                AttributeValue.builder().n(String.valueOf(fileSize)).build());
        item.put("creation_date", AttributeValue.builder().s(now.toString()).build());
        item.put(
                "expiration_date",
                AttributeValue.builder()
                        .n(String.valueOf(expiration.getEpochSecond()))
                        .build());

        var putItemRequest =
                PutItemRequest.builder().tableName(DYNAMO_TABLE_NAME).item(item).build();

        dynamoDbClient.putItem(putItemRequest);
    }

    private String uploadToS3(String fileName, byte[] fileContent) {
        var putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(fileName)
                .contentType(IMAGE_CONTENT_TYPE)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileContent));

        var utilities = s3Client.utilities();
        var request = GetUrlRequest.builder().bucket(BUCKET_NAME).key(fileName).build();

        return utilities.getUrl(request).toString();
    }

    private APIGatewayProxyResponseEvent createSuccessResponse(int statusCode, String qrUrl, String message) {
        return createResponse(statusCode, new QrResponse(qrUrl, message, null));
    }

    private APIGatewayProxyResponseEvent createErrorResponse(int statusCode, String errorMessage) {
        return createResponse(statusCode, new QrResponse(null, null, errorMessage));
    }

    private APIGatewayProxyResponseEvent createResponse(int statusCode, QrResponse responseBody) {
        try {
            var response = new APIGatewayProxyResponseEvent();
            response.setHeaders(Map.of(CONTENT_TYPE, APPLICATION_JSON));
            response.setStatusCode(statusCode);
            response.setBody(jsonMapper.writeValueAsString(responseBody));
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
