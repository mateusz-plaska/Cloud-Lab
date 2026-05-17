output "public_ip" {
  description = "Public IP address of your application server"
  value = aws_instance.app_server.public_ip
}

output "how_to_test" {
  description = "How to test the API"
  value = "Query to order service: http://${aws_instance.app_server.public_ip}:8081"
}