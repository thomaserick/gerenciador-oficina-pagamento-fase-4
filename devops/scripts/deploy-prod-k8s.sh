#!/bin/bash
set -e

# =============================
# Deploy da aplicação Spring Boot no Kubernetes EKS
# =============================

NAMESPACE="gerenciador-oficina-core"
K8S_PATH="./devops/k8s/prod"

echo "Iniciando deploy Kubernetes no namespace: $NAMESPACE"

# Verifica se o namespace existe
kubectl get namespace $NAMESPACE || kubectl apply -f $K8S_PATH/namespace.yaml

echo "Aplicando secrets..."
kubectl apply -f $K8S_PATH/newrelic-secret.yaml -n $NAMESPACE
kubectl apply -f $K8S_PATH/aws-secret.yaml -n $NAMESPACE

echo "Subindo aplicação Spring Boot..."
kubectl apply -f $K8S_PATH/deployment.yaml -n $NAMESPACE

echo "Criando service para expor aplicação..."
kubectl apply -f $K8S_PATH/services.yaml -n $NAMESPACE

echo "Aplicando Horizontal Pod Autoscaler..."
kubectl apply -f $K8S_PATH/hpa.yaml -n $NAMESPACE

echo "✅ Deploy realizado com sucesso no EKS!"
