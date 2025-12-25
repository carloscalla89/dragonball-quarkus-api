#!/bin/bash

# 1. Variables de configuración
APP_NAME="dragonball-quarkus-api-jvm"
IMAGE_NAME="carlos89/dragonball-quarkus-api-jvm" # O el nombre que uses
TAG="latest"
DOCKERFILE="docker/Dockerfile.jvm"
DEPLOYMENT="k8s/deployment-jvm-minikube.yaml"

# Colores para los mensajes
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}--- Iniciando Redeploy en Minikube ---${NC}"

# 2. Conectar tu terminal al Docker Daemon de Minikube
# Esto es mágico: Hace que los comandos 'docker' se ejecuten DENTRO de Minikube
echo -e "${GREEN}1. Conectando al entorno Docker de Minikube...${NC}"
eval $(minikube -p minikube docker-env)

# 3. Borrar el Deployment anterior
echo -e "${GREEN}2. Eliminando deployment anterior...${NC}"
# --ignore-not-found evita que el script falle si es la primera vez que despliegas
kubectl delete deployment $APP_NAME --ignore-not-found=true

# 4. Borrar la imagen vieja (Limpieza)
# Como ya estamos en el contexto de Minikube, esto borra la imagen DE MINIKUBE
echo -e "${GREEN}3. Borrando imagen antigua dentro de Minikube...${NC}"
docker rmi $IMAGE_NAME:$TAG || echo -e "${RED}Nota: La imagen no existía o no se pudo borrar (no es crítico).${NC}"

# 5. Construir la nueva imagen (Directamente en Minikube)
echo -e "${GREEN}4. Construyendo imagen Docker...${NC}"
docker build -f $DOCKERFILE -t $IMAGE_NAME:$TAG .

# Verificamos si el build falló
if [ $? -ne 0 ]; then
    echo -e "${RED}Error: Docker build falló.${NC}"
    exit 1
fi

# NOTA: No necesitamos 'minikube image load' porque ya construimos dentro.

# 6. Aplicar cambios en Kubernetes
echo -e "${GREEN}5. Desplegando en Kubernetes...${NC}"
kubectl apply -f $DEPLOYMENT

# 7. Forzar reinicio para que tome la nueva imagen (Por seguridad)
kubectl rollout restart deployment $APP_NAME

echo -e "${GREEN}--- ¡Despliegue completado! ---${NC}"
kubectl get pods