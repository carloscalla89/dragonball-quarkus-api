#!/bin/bash

# 1. Variables de configuración
APP_NAME="dragonball-quarkus-api-native"
IMAGE_NAME="carlos89/dragonball-quarkus-api-native" # O el nombre que uses
TAG="latest"
DOCKERFILE="docker/Dockerfile.native-micro" # O Dockerfile.native-micro si usas nativo
DEPLOYMENT="k8s/deployment-native-minikube.yaml"
ELK_NS="elk" # Namespace donde vive tu ELK

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'


# 5. Construir la nueva imagen (entorno local)
echo -e "${GREEN}1. Construyendo imagen Docker...${NC}"
docker build -f $DOCKERFILE -t $IMAGE_NAME:$TAG .

# Verificamos si el build falló
if [ $? -ne 0 ]; then
    echo -e "${RED}Error: Docker build falló.${NC}"
    exit 1
fi


# --- 3. FLUJO PRINCIPAL ---
echo -e "${GREEN}--- Iniciando Redeploy en Minikube ---${NC}"

# A. Conectar a Minikube
echo -e "${GREEN}2. Conectando al entorno Docker de Minikube...${NC}"
eval $(minikube -p minikube docker-env)

# 3. Borrar el Deployment anterior
echo -e "${GREEN}3. Eliminando deployment anterior...${NC}"
# --ignore-not-found evita que el script falle si es la primera vez que despliegas
kubectl delete deployment $APP_NAME --ignore-not-found=true


# 4. Borrar la imagen vieja (Limpieza)
# Como ya estamos en el contexto de Minikube, esto borra la imagen DE MINIKUBE
echo -e "${GREEN}4. Borrando imagen antigua dentro de Minikube...${NC}"
docker rmi $IMAGE_NAME:$TAG || echo -e "${RED}Nota: La imagen no existía o no se pudo borrar (no es crítico).${NC}"

eval $(exit)

echo -e "${GREEN}--- Cargando imagen local a Minikube ---${NC}"
minikube image load $IMAGE_NAME:$TAG


# 6. Aplicar cambios en Kubernetes
echo -e "${GREEN}5. Desplegando en Kubernetes...${NC}"
kubectl apply -f $DEPLOYMENT

# 7. Forzar reinicio para que tome la nueva imagen (Por seguridad)
kubectl rollout restart deployment $APP_NAME

echo -e "${GREEN}--- ¡Despliegue completado! ---${NC}"
kubectl get pods