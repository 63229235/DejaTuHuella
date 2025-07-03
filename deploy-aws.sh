#!/bin/bash

# Script para automatizar el despliegue en AWS Elastic Beanstalk
# Uso: ./deploy-aws.sh <region> <account-id>

set -e

# Verificar argumentos
if [ $# -ne 2 ]; then
    echo "Uso: ./deploy-aws.sh <region> <account-id>"
    exit 1
fi

AWS_REGION=$1
AWS_ACCOUNT_ID=$2
APP_NAME="dejatuhuella"
ECR_REPO="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${APP_NAME}"

echo "=== Iniciando despliegue de ${APP_NAME} en AWS Elastic Beanstalk ==="
echo "Región: ${AWS_REGION}"
echo "Cuenta AWS: ${AWS_ACCOUNT_ID}"
echo "Repositorio ECR: ${ECR_REPO}"

# Actualizar Dockerrun.aws.json con los valores correctos
echo "=== Actualizando Dockerrun.aws.json ==="
sed -i "s/\${AWS_ACCOUNT_ID}/${AWS_ACCOUNT_ID}/g" Dockerrun.aws.json
sed -i "s/\${AWS_REGION}/${AWS_REGION}/g" Dockerrun.aws.json

# Construir la aplicación con Maven
echo "=== Construyendo la aplicación con Maven ==="
./mvnw clean package -DskipTests

# Construir la imagen Docker
echo "=== Construyendo la imagen Docker ==="
docker build -t ${APP_NAME}:latest .

# Autenticarse en ECR
echo "=== Autenticándose en ECR ==="
aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPO}

# Crear el repositorio ECR si no existe
echo "=== Verificando repositorio ECR ==="
aws ecr describe-repositories --repository-names ${APP_NAME} --region ${AWS_REGION} || \
aws ecr create-repository --repository-name ${APP_NAME} --region ${AWS_REGION}

# Etiquetar y subir la imagen a ECR
echo "=== Etiquetando y subiendo la imagen a ECR ==="
docker tag ${APP_NAME}:latest ${ECR_REPO}:latest
docker push ${ECR_REPO}:latest

# Inicializar EB si es necesario
if [ ! -d ".elasticbeanstalk" ]; then
    echo "=== Inicializando Elastic Beanstalk ==="
    eb init ${APP_NAME} --region ${AWS_REGION} --platform docker
fi

# Verificar si el entorno ya existe
ENV_NAME="${APP_NAME}-env"
eb list | grep -q "${ENV_NAME}" || {
    echo "=== Creando nuevo entorno de Elastic Beanstalk ==="
    eb create ${ENV_NAME} --single --instance-type t2.micro
}

# Desplegar la aplicación
echo "=== Desplegando la aplicación en Elastic Beanstalk ==="
eb deploy ${ENV_NAME}

echo "=== Despliegue completado ==="
echo "URL de la aplicación: $(eb status ${ENV_NAME} | grep CNAME | awk '{print $2}')"