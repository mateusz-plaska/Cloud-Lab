terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 3.2.0"
    }
  }
}

provider "aws" {
  region     = var.aws_region
  access_key = var.aws_access_key
  secret_key = var.aws_secret_key
  token      = var.aws_session_token
}

data "aws_eks_cluster" "cluster" {
  name = var.cluster_name
}

data "aws_eks_cluster_auth" "cluster_auth" {
  name = var.cluster_name
}

provider "kubernetes" {
  host                   = data.aws_eks_cluster.cluster.endpoint
  cluster_ca_certificate = base64decode(data.aws_eks_cluster.cluster.certificate_authority[0].data)
  token                  = data.aws_eks_cluster_auth.cluster_auth.token
}


resource "kubernetes_manifest" "common_config_map" {
  manifest = yamldecode(file("../../k8s/common-configmap.yaml"))
}

resource "kubernetes_secret_v1" "common_secret" {
  metadata {
    name      = "outbound-secret"
    namespace = "default"
  }

  type = "Opaque"

  data = {
    AWS_ACCESS_KEY_ID     = var.aws_access_key
    AWS_SECRET_ACCESS_KEY = var.aws_secret_key
    AWS_SESSION_TOKEN     = var.aws_session_token
    RDS_USERNAME          = var.rds_username
    RDS_PASSWORD          = var.db_password
  }
}


resource "kubernetes_manifest" "order_deployment" {
  manifest   = yamldecode(file("../../k8s/order-deployment.yaml"))
  depends_on = [kubernetes_manifest.common_config_map, kubernetes_secret_v1.common_secret]
}

resource "kubernetes_manifest" "order_service" {
  manifest = yamldecode(file("../../k8s/order-service.yaml"))
}


resource "kubernetes_manifest" "packing_deployment" {
  manifest   = yamldecode(file("../../k8s/packing-deployment.yaml"))
  depends_on = [kubernetes_manifest.common_config_map, kubernetes_secret_v1.common_secret]
}

resource "kubernetes_manifest" "packing_service" {
  manifest = yamldecode(file("../../k8s/packing-service.yaml"))
}


resource "kubernetes_manifest" "picking_deployment" {
  manifest   = yamldecode(file("../../k8s/picking-deployment.yaml"))
  depends_on = [kubernetes_manifest.common_config_map, kubernetes_secret_v1.common_secret]
}

resource "kubernetes_manifest" "picking_service" {
  manifest = yamldecode(file("../../k8s/picking-service.yaml"))
}


resource "kubernetes_manifest" "reservation_deployment" {
  manifest   = yamldecode(file("../../k8s/reservation-deployment.yaml"))
  depends_on = [kubernetes_manifest.common_config_map, kubernetes_secret_v1.common_secret]
}

resource "kubernetes_manifest" "reservation_service" {
  manifest = yamldecode(file("../../k8s/reservation-service.yaml"))
}


resource "kubernetes_manifest" "shipping_deployment" {
  manifest   = yamldecode(file("../../k8s/shipping-deployment.yaml"))
  depends_on = [kubernetes_manifest.common_config_map, kubernetes_secret_v1.common_secret]
}

resource "kubernetes_manifest" "shipping_service" {
  manifest = yamldecode(file("../../k8s/shipping-service.yaml"))
}