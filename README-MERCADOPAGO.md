# Implementación de Mercado Pago en Deja Tu Huella

## Descripción

Este documento describe la implementación de Mercado Pago como pasarela de pagos en la aplicación Deja Tu Huella. La integración permite a los usuarios realizar pagos seguros utilizando la plataforma de Mercado Pago.

## Características implementadas

- Integración con el SDK de Mercado Pago v2.1.7
- Creación de preferencias de pago
- Procesamiento de notificaciones de pago (IPN)
- Manejo de estados de pago (éxito, pendiente, fallido)
- Interfaz de usuario adaptada para Mercado Pago

## Estructura de la implementación

### Configuración

- `MercadoPagoConfig.java`: Configuración del SDK de Mercado Pago
- `application.properties`: Configuración de tokens y URLs de callback

### Modelos de datos

- `MercadoPagoRequestDTO.java`: Modelo para solicitudes a Mercado Pago
- `MercadoPagoResponseDTO.java`: Modelo para respuestas de Mercado Pago

### Servicios

- `MercadoPagoService.java`: Servicio para interactuar con la API de Mercado Pago
- `PagoService.java`: Servicio actualizado para utilizar Mercado Pago

### Controladores

- `PagoController.java`: Controlador actualizado con endpoints para Mercado Pago

### Vistas

- `mercadopago.html`: Plantilla para la página de pago con Mercado Pago

### Recursos estáticos

- `css/pago.css`: Estilos actualizados para la interfaz de Mercado Pago
- `js/mercadopago.js`: JavaScript para la integración con Mercado Pago

## Flujo de pago

1. El usuario selecciona productos y procede al pago
2. El sistema crea una preferencia de pago en Mercado Pago
3. Se muestra al usuario el botón de pago de Mercado Pago
4. El usuario completa el pago en Mercado Pago
5. Mercado Pago notifica a la aplicación sobre el estado del pago
6. La aplicación actualiza el estado del pedido según la notificación

## Configuración necesaria

Para que la integración funcione correctamente, es necesario configurar las siguientes propiedades en `application.properties`:

```properties
# Configuración de Mercado Pago
mercadopago.access.token=TEST-0000000000000000000000000000000000-000000-00000000000000000000000000000000-000000
mercadopago.success.url=http://localhost:8080/pagos/success
mercadopago.failure.url=http://localhost:8080/pagos/failure
mercadopago.pending.url=http://localhost:8080/pagos/pending
mercadopago.notification.url=http://localhost:8080/pagos/webhook
```

Reemplaza el token de acceso con el proporcionado por Mercado Pago para tu cuenta.

## Documentación adicional

Para más detalles sobre la configuración y uso de Mercado Pago, consulta el archivo `docs/MERCADOPAGO_SETUP.md`.

## Pruebas

Para probar la integración, puedes utilizar las tarjetas de prueba proporcionadas por Mercado Pago:

- APRO (Pago aprobado): 5031 7557 3453 0604
- OTHE (Pendiente): 5031 7557 3453 0604
- CONT (Pendiente): 5031 7557 3453 0604
- CALL (Pendiente): 5031 7557 3453 0604
- FUND (Rechazado): 5031 7557 3453 0604
- SECU (Rechazado): 5031 7557 3453 0604
- EXPI (Rechazado): 5031 7557 3453 0604
- FORM (Rechazado): 5031 7557 3453 0604

Para todas las tarjetas:
- Fecha de vencimiento: Cualquier fecha futura
- CVV: 123
- Titular: APRO