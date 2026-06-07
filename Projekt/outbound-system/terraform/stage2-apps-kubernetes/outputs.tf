output "eks_cluster_endpoint" {
  description = "Endpoint EKS cluster, where the applications are deployed.\n Check the URLs of services in Load Balancers in the AWS CLI"
  value = data.aws_eks_cluster.cluster.endpoint
}