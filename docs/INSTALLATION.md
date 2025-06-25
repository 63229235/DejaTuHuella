# Guía de Instalación y Configuración - DejaTuHuella

Esta guía proporciona instrucciones detalladas para instalar, configurar y ejecutar el proyecto DejaTuHuella en un entorno de desarrollo local.

## Requisitos Previos

Antes de comenzar, asegúrate de tener instalado lo siguiente:

1. **Java Development Kit (JDK) 17 o superior**
   - [Descargar JDK](https://www.oracle.com/java/technologies/javase-downloads.html)
   - Verifica la instalación con: `java -version`

2. **Maven 3.6 o superior**
   - [Descargar Maven](https://maven.apache.org/download.cgi)
   - Verifica la instalación con: `mvn -version`

3. **MySQL 8.0 o superior**
   - [Descargar MySQL](https://dev.mysql.com/downloads/mysql/)
   - Asegúrate de que el servidor MySQL esté en ejecución

4. **Git**
   - [Descargar Git](https://git-scm.com/downloads)
   - Verifica la instalación con: `git --version`

## Clonar el Repositorio

```bash
git clone https://github.com/tu-usuario/DejaTuHuella.git
cd DejaTuHuella
```

## Configuración de la Base de Datos

1. Crea una base de datos MySQL para el proyecto:

```sql
CREATE DATABASE galeria_virtual;
```

2. Configura las credenciales de la base de datos en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/galeria_virtual
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

## Configuración del Proyecto

1. **Configuración de Carga de Archivos**

Asegúrate de que el directorio para la carga de archivos exista y tenga los permisos adecuados:

```bash
mkdir -p src/main/resources/static/uploads
```

Puedes modificar la ubicación de este directorio en `application.properties`:

```properties
file.upload-dir=src/main/resources/static/uploads
```

2. **Configuración del Puerto del Servidor**

Por defecto, la aplicación se ejecuta en el puerto 8081. Puedes cambiar esto en `application.properties`:

```properties
server.port=8081
```

## Compilación del Proyecto

Compila el proyecto utilizando Maven:

```bash
mvn clean install
```

Esto descargará todas las dependencias necesarias, compilará el código y ejecutará las pruebas.

## Ejecución de la Aplicación

1. **Usando Maven**

```bash
mvn spring-boot:run
```

2. **Usando el JAR generado**

```bash
java -jar target/dejatuhuella-0.0.1-SNAPSHOT.jar
```

La aplicación estará disponible en: `http://localhost:8081`

## Configuración para Desarrollo

### Perfiles de Spring

Puedes crear diferentes perfiles de configuración para diferentes entornos:

1. Crea archivos de propiedades específicos para cada entorno:
   - `application-dev.properties` para desarrollo
   - `application-prod.properties` para producción

2. Activa un perfil específico al ejecutar la aplicación:

```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

### Hot Reload

Para habilitar la recarga automática durante el desarrollo, puedes usar Spring DevTools, que ya está incluido en el proyecto. Asegúrate de que tu IDE esté configurado correctamente para aprovechar esta funcionalidad.

## Ejecución de Pruebas

Ejecuta las pruebas unitarias e integración con:

```bash
mvn test
```

Para ejecutar un conjunto específico de pruebas:

```bash
mvn test -Dtest=PagoServiceTest
```

## Solución de Problemas Comunes

### Error de Conexión a la Base de Datos

- Verifica que el servidor MySQL esté en ejecución
- Comprueba las credenciales en `application.properties`
- Asegúrate de que la base de datos `galeria_virtual` exista

### Error al Cargar Archivos

- Verifica que el directorio de carga exista y tenga permisos de escritura
- Comprueba la configuración `file.upload-dir` en `application.properties`

### Error de Puerto en Uso

Si el puerto 8081 ya está en uso, puedes cambiarlo en `application.properties`:

```properties
server.port=8082
```

## Despliegue

### Generación del JAR para Producción

```bash
mvn clean package -Pprod
```

Esto generará un archivo JAR en el directorio `target/` que puede ser desplegado en un servidor.

### Configuración para Producción

Para un entorno de producción, considera las siguientes configuraciones en `application-prod.properties`:

```properties
# Deshabilitar SQL en logs
spring.jpa.show-sql=false

# Nivel de logging
logging.level.root=WARN
logging.level.com.proyecto.dejatuhuella=INFO

# Configuración de seguridad
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.http-only=true
```

## Recursos Adicionales

- [Documentación de Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Documentación de Thymeleaf](https://www.thymeleaf.org/documentation.html)
- [Documentación de MySQL](https://dev.mysql.com/doc/)

## Soporte

Si encuentras problemas o tienes preguntas, por favor crea un issue en el repositorio de GitHub o contacta al equipo de desarrollo.