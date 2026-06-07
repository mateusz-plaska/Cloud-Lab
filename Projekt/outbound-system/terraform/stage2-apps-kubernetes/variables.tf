variable "aws_region" {
  type = string
  default = "us-east-1"
}

variable "aws_access_key" {
  type = string
  sensitive = true
}

variable "aws_secret_key" {
  type = string
  sensitive = true
}

variable "aws_session_token" {
  type = string
  sensitive = true
}

variable "cluster_name" {
  type = string
  default = "outbound-system-cluster"
}

variable "rds_username" {
  description = "RDS PostgreSQL master username"
  type        = string
  default     = "postgres"
}

variable "db_password" {
  description = "RDS PostgreSQL master password"
  type = string
  sensitive = true
}


