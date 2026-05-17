variable "aws_region" {
  description = "AWS region where the infrastructure will be created"
  type        = string
  default     = "us-east-1"
}

variable "aws_access_key" {
  description = "AWS Access Key ID"
  type        = string
  sensitive   = true
}

variable "aws_secret_key" {
  description = "AWS Secret Access Key"
  type        = string
  sensitive   = true
}

variable "aws_session_token" {
  description = "AWS Session Token (required in AWS Learner Lab)"
  type        = string
  sensitive   = true
}

variable "db_password" {
  description = "RDS PostgreSQL master password"
  type        = string
  sensitive   = true
}

variable "order_db_name" {
  type = string
  default = "order_db"
}

variable "reservation_db_name" {
  type = string
  default = "reservation_db"
}

variable "packing_db_name" {
  type = string
  default = "packing_db"
}