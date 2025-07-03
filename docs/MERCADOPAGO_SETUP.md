# Configuración de Mercado Pago en Deja Tu Huella

Este documento describe cómo configurar y utilizar Mercado Pago como pasarela de pagos en la aplicación Deja Tu Huella.

## Requisitos Previos

1. Tener una cuenta en [Mercado Pago](https://www.mercadopago.com.pe/)
2. Obtener las credenciales de API (Access Token) desde el [Panel de Desarrolladores de Mercado Pago](https://www.mercadopago.com.pe/developers/panel)

## Configuración

### 1. Configurar las credenciales

Las credenciales de Mercado Pago deben configurarse como variables de entorno o directamente en el archivo `application.properties`.

#### Opción 1: Variables de entorno (recomendado para producción)

```bash
export MP_ACCESS_TOKEN=YOUR_MERCADO_PAGO_ACCESS_TOKEN
```

#### Opción 2: Configuración en application.properties (solo para desarrollo)

```properties
mercadopago.access.token=YOUR_MERCADO_PAGO_ACCESS_TOKEN
```

### 2. Configurar las URLs de callback

En el archivo `application.properties` se encuentran las siguientes configuraciones:

```properties
mercadopago.success.url=http://localhost:8081/pagos/success
mercadopago.failure.url=http://localhost:8081/pagos/failure
mercadopago.pending.url=http://localhost:8081/pagos/pending
mercadopago.notification.url=http://localhost:8081/pagos/webhook
```

Para entornos de producción, debes actualizar estas URLs con tu dominio real.

### 3. Configurar la Public Key en la vista

En el archivo `mercadopago.html`, actualiza la Public Key con la tuya:

```javascript
const mp = new MercadoPago('YOUR_PUBLIC_KEY', {
    locale: 'es-PE'
});
```

## Flujo de Pago

1. El usuario selecciona productos y los agrega al carrito
2. El usuario procede al checkout y se crea un pedido en estado "PENDIENTE"
3. El sistema redirige al usuario a la página de pago con Mercado Pago
4. Se crea una preferencia de pago en Mercado Pago y se muestra el botón de pago
5. El usuario selecciona su método de pago y completa la transacción
6. Mercado Pago redirige al usuario a una de las URLs de callback (success, failure, pending)
7. El sistema actualiza el estado del pedido según la respuesta

## Webhooks (IPN - Instant Payment Notification)

Mercado Pago enviará notificaciones a la URL configurada en `mercadopago.notification.url` cuando el estado de un pago cambie. Estas notificaciones son procesadas por el endpoint `/pagos/webhook`.

## Pruebas

Para realizar pruebas en el entorno de desarrollo, puedes usar las [tarjetas de prueba proporcionadas por Mercado Pago](https://www.mercadopago.com.pe/developers/es/docs/checkout-pro/additional-content/test-cards).

Ejemplo de tarjetas de prueba:

| País | Tipo | Número | CVV | Fecha Venc. |
|------|------|--------|-----|-------------|
| Perú | Visa | 4009 1753 3280 6176 | 123 | 11/25 |
| Perú | Mastercard | 5031 7557 3453 0604 | 123 | 11/25 |

## Solución de Problemas

### El pago no se procesa

- Verifica que las credenciales (Access Token y Public Key) sean correctas
- Asegúrate de que las URLs de callback sean accesibles desde Internet
- Revisa los logs de la aplicación para ver si hay errores en la comunicación con Mercado Pago

### No se reciben notificaciones (webhooks)

- Verifica que la URL de notificación sea accesible desde Internet
- Configura correctamente los permisos en el panel de desarrolladores de Mercado Pago
- Revisa los logs de la aplicación para ver si hay errores al procesar las notificaciones

## Referencias

- [Documentación oficial de Mercado Pago](https://www.mercadopago.com.pe/developers/es/docs)
- [Checkout Pro](https://www.mercadopago.com.pe/developers/es/docs/checkout-pro/landing)
- [SDK de Java para Mercado Pago](https://github.com/mercadopago/sdk-java)