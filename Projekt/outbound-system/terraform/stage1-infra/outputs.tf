output "rds_address" {
  description = "Connection endpoint for the generated RDS database"
  value = aws_db_instance.postgres.address
}

output "rds_port" {
  description = "Port for the generated RDS database"
  value = aws_db_instance.postgres.port
}