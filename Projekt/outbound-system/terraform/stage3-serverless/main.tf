terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.9.0"
    }
  }
}

provider "aws" {
  region     = var.aws_region
  access_key = var.aws_access_key
  secret_key = var.aws_secret_key
  token      = var.aws_session_token
}

data "aws_iam_role" "lab_role" {
  name = "LabRole"
}

resource "aws_dynamodb_table" "qr_metadata" {
  name         = "qr_codes_metadata"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "url_hash"

  attribute {
    name = "url_hash"
    type = "S"
  }
}

resource "random_id" "bucket_suffix" {
  byte_length = 4
}

resource "aws_s3_bucket" "qr_codes_bucket" {
  bucket = "outbound-qrcodes-${random_id.bucket_suffix.hex}"
  force_destroy = true
}

resource "aws_s3_bucket_public_access_block" "public_access" {
  bucket                  = aws_s3_bucket.qr_codes_bucket.id
  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_policy" "public_read" {
  bucket = aws_s3_bucket.qr_codes_bucket.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action    = "s3:GetObject"
        Effect    = "Allow"
        Resource  = "${aws_s3_bucket.qr_codes_bucket.arn}/*"
        Principal = "*"
      }
    ]
  })
  depends_on = [aws_s3_bucket_public_access_block.public_access]
}

resource "aws_lambda_function" "qr_generator" {
  function_name = "GenerateQrCodeFunction"
  role          = data.aws_iam_role.lab_role.arn

  filename      = "../../qr-generator-lambda/build/libs/qr-generator-lambda-1.0-SNAPSHOT-all.jar"
  source_code_hash = filebase64sha256("../../qr-generator-lambda/build/libs/qr-generator-lambda-1.0-SNAPSHOT-all.jar")
  handler       = "org.pwr.cloud.lab.lambda.QrGeneratorHandler::handleRequest"
  runtime       = "java21"

  memory_size   = 1024
  timeout       = 30

  environment {
    variables = {
      S3_BUCKET_NAME = aws_s3_bucket.qr_codes_bucket.bucket
      DYNAMO_TABLE_NAME  = aws_dynamodb_table.qr_metadata.name
    }
  }
}

resource "aws_lambda_function_url" "qr_lambda_url" {
  function_name      = aws_lambda_function.qr_generator.function_name
  authorization_type = "NONE"
}