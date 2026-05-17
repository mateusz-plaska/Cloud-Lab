terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
  }
}

provider "aws" {
  region     = var.aws_region
  access_key = var.aws_access_key
  secret_key = var.aws_secret_key
  token      = var.aws_session_token
}

resource "aws_default_vpc" "default" {}

resource "aws_security_group" "apps_sg" {
  name        = "outbound-microservices-sg"
  description = "Allows HTTP traffic to microservices and SSH access"
  vpc_id      = aws_default_vpc.default.id

  ingress {
    from_port   = 8081
    to_port     = 8085
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "app_server" {
  # Ubuntu Server 26.04 LTS
  ami = "ami-091138d0f0d41ff90"
  instance_type = "t3.medium"
  vpc_security_group_ids = [aws_security_group.apps_sg.id]

  root_block_device {
    volume_size = 20
  }

  user_data = <<-EOF
              #!/bin/bash

              apt-get update
              apt-get install -y docker.io docker-compose git

              systemctl start docker
              systemctl enable docker

              mkdir -p /home/ubuntu/app
              cd /home/ubuntu/app
              wget -O docker-compose.yml ${var.docker_compose_file_url}

              cat <<EOT >> .env
              RABBITMQ_URL=${var.rabbitmq_url}
              RABBITMQ_EXCHANGE=${var.rabbitmq_exchange}

              AWS_REGION=${var.aws_region}
              AWS_ACCESS_KEY_ID=${var.aws_access_key}
              AWS_SECRET_ACCESS_KEY=${var.aws_secret_key}
              AWS_SESSION_TOKEN=${var.aws_session_token}
              AWS_BUCKET_NAME=${var.aws_bucket_name}

              RDS_HOST=${var.rds_host}
              RDS_PORT=${var.rds_port}
              RDS_USERNAME=${var.rds_username}
              RDS_PASSWORD=${var.db_password}

              ORDER_DB_NAME=${var.order_db_name}
              RESERVATION_DB_NAME=${var.reservation_db_name}
              PACKING_DB_NAME=${var.packing_db_name}
              EOT

              docker compose pull
              docker compose up -d
              EOF

  tags = {
    Name = "Outbound-App-Server"
  }
}