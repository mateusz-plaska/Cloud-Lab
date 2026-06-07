output "lambda_api_endpoint" {
  description = "URL of the Lambda function"
  value = aws_lambda_function_url.qr_lambda_url.function_url
}

output "s3_bucket_name" {
  description = "S3 bucket name"
  value = aws_s3_bucket.qr_codes_bucket.bucket
}

output "dynamodb_table_name" {
  description = "DynamoDB table name"
  value = aws_dynamodb_table.qr_metadata.name
}