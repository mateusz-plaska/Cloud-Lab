package org.pwr.cloud.lab.ordergateway.infrastructure.storage;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pwr.cloud.lab.common.domain.model.id.OrderId;
import org.pwr.cloud.lab.ordergateway.domain.storage.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3StorageService implements StorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @PostConstruct
    public void ensureBucketExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            log.info("S3 Bucket '{}' exists and is accessible.", bucketName);
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                log.info("S3 Bucket '{}' not found. Creating a new one...", bucketName);
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            } else {
                log.error("Error accessing S3 bucket: {}", e.getMessage());
            }
        }
    }

    @Override
    public String uploadFile(MultipartFile file, OrderId orderId) {
        try {
            var objectKey = "orders/" + orderId.value() + "/" + file.getOriginalFilename();
            var putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            log.info("File uploaded successfully to S3: {}", objectKey);
            return objectKey;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file content for S3 upload", e);
        }
    }

    @Override
    public byte[] loadFile(String objectKey) {
        try {
            var getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            return s3Client.getObject(getObjectRequest).readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load file from S3: " + objectKey, e);
        }
    }
}
