# Configuración de Google OAuth2 para DejaTuHuella

Este documento proporciona instrucciones detalladas para configurar la autenticación con Google OAuth2 en la aplicación DejaTuHuella.

## Requisitos previos

- Una cuenta de Google
- Acceso a Google Cloud Console
- La aplicación DejaTuHuella funcionando correctamente

## Pasos para configurar Google OAuth2

### 1. Crear un proyecto en Google Cloud Console

1. Accede a [Google Cloud Console](https://console.cloud.google.com/)
2. Crea un nuevo proyecto o selecciona uno existente
3. Anota el ID del proyecto, lo necesitarás más adelante

### 2. Configurar la pantalla de consentimiento de OAuth

1. En el menú lateral, ve a "APIs y servicios" > "Pantalla de consentimiento de OAuth"
2. Selecciona el tipo de usuario (externo o interno)
3. Completa la información requerida:
   - Nombre de la aplicación: DejaTuHuella
   - Correo electrónico de soporte al usuario
   - Dominio autorizado (si es necesario)
   - Información de contacto del desarrollador
4. Guarda la configuración

### 3. Crear credenciales de OAuth2

1. En el menú lateral, ve a "APIs y servicios" > "Credenciales"
2. Haz clic en "Crear credenciales" y selecciona "ID de cliente de OAuth"
3. Selecciona "Aplicación web" como tipo de aplicación
4. Asigna un nombre a tu cliente OAuth2 (por ejemplo, "DejaTuHuella Web Client")
5. Añade los URIs de redirección autorizados:
   - `http://localhost:8081/login/oauth2/code/google` (para desarrollo local)
   - `https://tudominio.com/login/oauth2/code/google` (para producción, si aplica)
6. Haz clic en "Crear"
7. Anota el **ID de cliente** y el **Secreto del cliente** que se generan

### 4. Configurar la aplicación DejaTuHuella

Ahora necesitas configurar la aplicación con las credenciales obtenidas. Hay dos formas de hacerlo:

#### Opción 1: Variables de entorno (recomendado)

Configura las siguientes variables de entorno en tu sistema:

```
GOOGLE_CLIENT_ID=tu-id-de-cliente
GOOGLE_CLIENT_SECRET=tu-secreto-de-cliente
```

#### Opción 2: Modificar application.properties directamente

Edita el archivo `src/main/resources/application.properties` y reemplaza las variables por los valores reales:

```properties
spring.security.oauth2.client.registration.google.client-id=tu-id-de-cliente
spring.security.oauth2.client.registration.google.client-secret=tu-secreto-de-cliente
spring.security.oauth2.client.registration.google.scope=email,profile
```

**Nota importante**: Si eliges esta opción, asegúrate de no subir estos datos sensibles a repositorios públicos.

## Verificación

Para verificar que la configuración funciona correctamente:

1. Inicia la aplicación DejaTuHuella
2. Navega a la página de inicio de sesión
3. Deberías ver un botón "Iniciar sesión con Google"
4. Haz clic en el botón y deberías ser redirigido a la página de inicio de sesión de Google
5. Después de iniciar sesión con Google, deberías ser redirigido de vuelta a la aplicación DejaTuHuella y estar autenticado

## Solución de problemas

### Error de redirección URI no coincidente

Si recibes un error que indica que el URI de redirección no coincide, asegúrate de que el URI configurado en Google Cloud Console coincida exactamente con el URI que está utilizando tu aplicación.

### Error de credenciales inválidas

Verifica que el ID de cliente y el secreto del cliente estén correctamente configurados en la aplicación.

### Error de consentimiento

Asegúrate de que la pantalla de consentimiento de OAuth esté correctamente configurada y que los ámbitos solicitados (email, profile) estén incluidos en la configuración.

## Consideraciones de seguridad

- Nunca compartas tu ID de cliente y secreto del cliente
- Utiliza HTTPS en producción
- Revisa regularmente las actividades de acceso en Google Cloud Console
- Considera implementar la revocación de tokens cuando sea necesario

## Recursos adicionales

- [Documentación oficial de Google OAuth2](https://developers.google.com/identity/protocols/oauth2)
- [Documentación de Spring Security OAuth2](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html)