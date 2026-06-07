terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
    postgresql = {
      source  = "cyrilgdn/postgresql"
      version = "1.26.0"
    }
  }
}

provider "aws" {
  region     = var.aws_region
  access_key = var.aws_access_key
  secret_key = var.aws_secret_key
  token      = var.aws_session_token
}

resource "aws_default_vpc" "default" {
  tags = {
    Name = "Default VPC"
  }
}

resource "aws_default_subnet" "default_az1" {
  availability_zone = "${var.aws_region}a"
}

resource "aws_default_subnet" "default_az2" {
  availability_zone = "${var.aws_region}b"
}

resource "aws_db_subnet_group" "rds_subnet_group" {
  name        = "outbound-rds-subnet-group"
  subnet_ids  = [aws_default_subnet.default_az1.id, aws_default_subnet.default_az2.id]
  description = "Subnet group for RDS database"
}

resource "aws_db_instance" "postgres" {
  identifier             = "outbound-system-rds"
  engine                 = "postgres"
  engine_version         = "18.3"
  instance_class         = "db.t4g.micro"
  allocated_storage      = 20
  db_name                = "outbound"
  username               = "postgres"
  password               = var.db_password
  db_subnet_group_name   = aws_db_subnet_group.rds_subnet_group.name
  publicly_accessible    = true
  skip_final_snapshot    = true
}

resource "aws_dynamodb_table" "picking_tasks" {
  name         = "picking_tasks"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "PK"

  attribute {
    name = "PK"
    type = "S"
  }
}

resource "aws_dynamodb_table" "shipments" {
  name         = "shipments"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "PK"

  attribute {
    name = "PK"
    type = "S"
  }
}

provider "postgresql" {
  host            = aws_db_instance.postgres.address
  port            = aws_db_instance.postgres.port
  database        = aws_db_instance.postgres.db_name
  username        = aws_db_instance.postgres.username
  password        = aws_db_instance.postgres.password
  sslmode         = "require"
  superuser       = false
}

resource "postgresql_database" "order_db" {
  name = var.order_db_name
}

resource "postgresql_database" "reservation_db" {
  name = var.reservation_db_name
}

resource "postgresql_database" "packing_db" {
  name = var.packing_db_name
}

data "aws_iam_role" "lab_role" {
  name = "LabRole"
}

resource "aws_eks_cluster" "main_cluster" {
  name     = "outbound-system-cluster"
  role_arn = data.aws_iam_role.lab_role.arn

  vpc_config {
    subnet_ids  = [aws_default_subnet.default_az1.id, aws_default_subnet.default_az2.id]
  }
}

resource "aws_eks_node_group" "main_nodes" {
  cluster_name    = aws_eks_cluster.main_cluster.name
  node_group_name = "outbound-system-nodes"
  node_role_arn   = data.aws_iam_role.lab_role.arn
  subnet_ids      = aws_eks_cluster.main_cluster.vpc_config[0].subnet_ids

  scaling_config {
    desired_size = 2
    max_size     = 3
    min_size     = 1
  }

  instance_types = ["t3.medium"]
}
