# Despliegue en AWS Elastic Beanstalk con Docker

Este documento proporciona instrucciones para desplegar la aplicación DejaTuHuella en AWS Elastic Beanstalk utilizando Docker.

## Requisitos previos

1. Cuenta de AWS
2. AWS CLI instalado y configurado
3. EB CLI instalado
4. Docker instalado
5. Git instalado

## Pasos para el despliegue

### 1. Crear un repositorio ECR (Elastic Container Registry)

```bash
# Crear un repositorio ECR
aws ecr create-repository --repository-name dejatuhuella --region <tu-region>

# Autenticarse en ECR
aws ecr get-login-password --region <tu-region> | docker login --username AWS --password-stdin <tu-account-id>.dkr.ecr.<tu-region>.amazonaws.com
```

### 2. Construir y subir la imagen Docker

```bash
# Construir la imagen Docker
docker build -t dejatuhuella .

# Etiquetar la imagen para ECR
docker tag dejatuhuella:latest <tu-account-id>.dkr.ecr.<tu-region>.amazonaws.com/dejatuhuella:latest

# Subir la imagen a ECR
docker push <tu-account-id>.dkr.ecr.<tu-region>.amazonaws.com/dejatuhuella:latest
```

### 3. Actualizar Dockerrun.aws.json

Edita el archivo `Dockerrun.aws.json` y reemplaza `${AWS_ACCOUNT_ID}` y `${AWS_REGION}` con tu ID de cuenta de AWS y la región que estás utilizando.

### 4. Inicializar y desplegar con Elastic Beanstalk

```bash
# Inicializar la aplicación EB
eb init dejatuhuella --region <tu-region> --platform docker

# Crear un entorno y desplegar la aplicación
eb create dejatuhuella-env --single --instance-type t2.micro
```

### 5. Configurar la base de datos RDS

Se recomienda crear una instancia de RDS MySQL para producción:

1. Ve a la consola de AWS RDS
2. Crea una nueva instancia de MySQL
3. Configura la instancia para que esté en la misma VPC que tu entorno de Elastic Beanstalk
4. Actualiza las variables de entorno en la consola de Elastic Beanstalk:
   - `SPRING_DATASOURCE_URL`: jdbc:mysql://<rds-endpoint>:3306/dejatuhuella
   - `SPRING_DATASOURCE_USERNAME`: <usuario-db>
   - `SPRING_DATASOURCE_PASSWORD`: <contraseña-db>

## Actualización de la aplicación

Para actualizar la aplicación después de realizar cambios:

```bash
# Construir y subir la nueva imagen
docker build -t dejatuhuella .
docker tag dejatuhuella:latest <tu-account-id>.dkr.ecr.<tu-region>.amazonaws.com/dejatuhuella:latest
docker push <tu-account-id>.dkr.ecr.<tu-region>.amazonaws.com/dejatuhuella:latest

# Desplegar la actualización
eb deploy
```

## Monitoreo y solución de problemas

- Puedes ver los logs de la aplicación con `eb logs`
- Para conectarte a la instancia EC2: `eb ssh`
- Monitorea la aplicación desde la consola de Elastic Beanstalk

## Limpieza de recursos

Cuando ya no necesites el entorno, puedes eliminarlo para evitar cargos adicionales:

```bash
eb terminate dejatuhuella-env
```

Recuerda también eliminar otros recursos como la base de datos RDS y el repositorio ECR si ya no los necesitas.