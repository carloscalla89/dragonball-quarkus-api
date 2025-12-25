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


# --- 2. FUNCIONES DE ELK ---
function stop_elk() {
    echo -e "${YELLOW}[ELK] Deteniendo stack para liberar recursos...${NC}"
    # Reducimos réplicas a 0
    kubectl scale statefulset elasticsearch --replicas=0 -n $ELK_NS > /dev/null 2>&1
    kubectl scale deployment logstash --replicas=0 -n $ELK_NS > /dev/null 2>&1
    kubectl scale deployment kibana --replicas=0 -n $ELK_NS > /dev/null 2>&1

    # Esperamos unos segundos para que Kubernetes libere la RAM
    echo -e "${YELLOW}[ELK] Stack detenido. Esperando 5s para liberar RAM...${NC}"
    sleep 5
}

function start_elk() {
    echo -e "${BLUE}[ELK] Reactivando stack de Observabilidad...${NC}"
    # Restauramos réplicas a 1
    kubectl scale statefulset elasticsearch --replicas=1 -n $ELK_NS > /dev/null 2>&1
    kubectl scale deployment logstash --replicas=1 -n $ELK_NS > /dev/null 2>&1
    kubectl scale deployment kibana --replicas=1 -n $ELK_NS > /dev/null 2>&1
    echo -e "${BLUE}[ELK] Señal de inicio enviada (iniciarán en segundo plano).${NC}"
}


# --- 3. FLUJO PRINCIPAL ---
echo -e "${GREEN}--- Iniciando Redeploy en Minikube ---${NC}"

# A. Conectar a Minikube
echo -e "${GREEN}1. Conectando al entorno Docker de Minikube...${NC}"
eval $(minikube -p minikube docker-env)

# B. APAGAR ELK (Estrategia 3)
stop_elk

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

# Verificamos si el build falló
if [ $? -ne 0 ]; then
    echo -e "${RED}Error: Docker build falló. El stack ELK permanecerá apagado para que corrijas el código.${NC}"
    # Opcional: Si quieres que se prenda aunque falle, descomenta la línea de abajo:
    # start_elk
    exit 1
fi

# D. ENCENDER ELK (Inmediatamente después del build exitoso)
# Lo encendemos ahora para que vaya cargando mientras se despliega la app
start_elk

# 6. Aplicar cambios en Kubernetes
echo -e "${GREEN}5. Desplegando en Kubernetes...${NC}"
kubectl apply -f $DEPLOYMENT

# 7. Forzar reinicio para que tome la nueva imagen (Por seguridad)
kubectl rollout restart deployment $APP_NAME

echo -e "${GREEN}--- ¡Despliegue completado! ---${NC}"
kubectl get pods