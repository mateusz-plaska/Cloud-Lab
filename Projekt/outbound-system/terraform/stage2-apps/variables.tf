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

variable "aws_bucket_name" {
  type = string
}

variable "rds_host" {
  description = "RDS PostgreSQL host address (without port)"
  type = string
}

variable "rds_port" {
  description = "RDS PostgreSQL port"
  type        = string
  default     = "5432"
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

variable "order_db_name" {
  description = "Database name for the order service"
  type        = string
  default     = "order_db"
}

variable "reservation_db_name" {
  description = "Database name for the reservation service"
  type        = string
  default     = "reservation_db"
}

variable "packing_db_name" {
  description = "Database name for the packing service"
  type        = string
  default     = "packing_db"
}

variable "rabbitmq_url" {
  type = string
}

variable "rabbitmq_exchange" {
  type = string
}

variable "docker_compose_file_url" {
  description = "Direct link (raw) to the docker-compose.yml file on GitHub"
  type = string
}
