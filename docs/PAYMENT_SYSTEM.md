# Sistema de Pagos - Documentación Técnica

## Descripción General

El sistema de pagos de DejaTuHuella permite a los usuarios realizar pagos seguros para sus pedidos utilizando tarjetas de crédito. Este documento describe la arquitectura, componentes y flujo de trabajo del sistema de pagos.

## Arquitectura

El sistema de pagos está compuesto por los siguientes componentes principales:

1. **Controladores**:
   - `PagoController`: Maneja las solicitudes HTTP relacionadas con pagos.

2. **Servicios**:
   - `PagoService`: Contiene la lógica de negocio para procesar pagos.
   - `PedidoService`: Gestiona los pedidos y sus estados.

3. **Modelos**:
   - `Pedido`: Entidad que representa un pedido en el sistema.
   - `EstadoPedido`: Enumeración que define los posibles estados de un pedido.

4. **DTOs**:
   - `PagoDTO`: Objeto de transferencia de datos para la información de pago.

5. **Vistas**:
   - `pago.html`: Formulario para ingresar datos de pago.
   - `confirmacion.html`: Página de confirmación de pago exitoso.
   - `mis-compras.html`: Vista para que los compradores vean sus pedidos.
   - `mis-ventas.html`: Vista para que los vendedores gestionen sus ventas.

## Flujo de Trabajo

### 1. Creación del Pedido

Cuando un usuario realiza un pedido, se crea con estado inicial `PENDIENTE`.

```java
pedido.setEstado(EstadoPedido.PENDIENTE);
pedidoRepository.save(pedido);
```

### 2. Pago del Pedido

1. El usuario accede a la página de pago desde `mis-compras.html` haciendo clic en "Realizar Pago".
2. El sistema verifica que el pedido exista, pertenezca al usuario y esté en estado `PENDIENTE`.
3. Se muestra el formulario de pago (`pago.html`) con los detalles del pedido.
4. El usuario ingresa los datos de la tarjeta de crédito.
5. Al enviar el formulario, `PagoController` procesa la solicitud:

```java
@PostMapping("/procesar/{id}")
public String procesarPago(@PathVariable Long id, @ModelAttribute PagoDTO pagoDTO, Model model) {
    try {
        // Validar datos de la tarjeta
        if (!pagoService.validarTarjeta(pagoDTO)) {
            return "redirect:/pago/" + id + "?error=tarjeta";
        }
        
        // Procesar el pago
        pagoService.procesarPago(id, pagoDTO);
        
        return "redirect:/pago/confirmacion/" + id;
    } catch (Exception e) {
        return "redirect:/pago/" + id + "?error=general";
    }
}
```

6. `PagoService` valida los datos de la tarjeta y procesa el pago:

```java
public void procesarPago(Long pedidoId, PagoDTO pagoDTO) {
    // Obtener el pedido
    Pedido pedido = pedidoRepository.findById(pedidoId)
        .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado"));
    
    // Verificar que el pedido pertenezca al usuario autenticado
    Usuario usuarioActual = usuarioService.obtenerUsuarioAutenticado();
    if (!pedido.getComprador().getId().equals(usuarioActual.getId())) {
        throw new UnauthorizedException("No tienes permiso para pagar este pedido");
    }
    
    // Verificar que el pedido esté en estado PENDIENTE
    if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
        throw new IllegalStateException("El pedido no está en estado PENDIENTE");
    }
    
    // Actualizar el estado del pedido a PAGADO
    pedido.setEstado(EstadoPedido.PAGADO);
    pedidoRepository.save(pedido);
}
```

7. Si el pago es exitoso, se actualiza el estado del pedido a `PAGADO` y se redirige a la página de confirmación.

### 3. Confirmación del Pago

1. Se muestra la página de confirmación con los detalles del pedido y el pago.
2. El usuario puede volver a `mis-compras.html` para ver el estado actualizado de su pedido.

### 4. Gestión del Pedido por el Vendedor

1. El vendedor ve el pedido con estado `PAGADO` en `mis-ventas.html`.
2. El vendedor puede actualizar el estado del pedido a `ENVIADO` cuando envía los productos.
3. Una vez entregado, el vendedor puede actualizar el estado a `ENTREGADO`.

## Validación de Tarjetas de Crédito

El sistema implementa validaciones básicas para las tarjetas de crédito:

```java
public boolean validarTarjeta(PagoDTO pagoDTO) {
    // Validar que el número de tarjeta tenga 16 dígitos
    if (pagoDTO.getNumeroTarjeta().replaceAll("\\s", "").length() != 16) {
        return false;
    }
    
    // Validar que el CVV tenga 3 dígitos
    if (pagoDTO.getCvv().length() != 3) {
        return false;
    }
    
    // Validar que la fecha de expiración sea futura
    LocalDate fechaActual = LocalDate.now();
    int mes = Integer.parseInt(pagoDTO.getMesExpiracion());
    int anio = Integer.parseInt(pagoDTO.getAnioExpiracion());
    
    if (anio < fechaActual.getYear() || 
        (anio == fechaActual.getYear() && mes < fechaActual.getMonthValue())) {
        return false;
    }
    
    return true;
}
```

## Estados de Pedido

El sistema utiliza la siguiente enumeración para gestionar los estados de los pedidos:

```java
public enum EstadoPedido {
    PENDIENTE,   // Pedido creado pero pendiente de pago
    PAGADO,      // Pago confirmado, listo para ser procesado
    ENVIADO,     // Pedido en camino al comprador
    ENTREGADO,   // Pedido recibido por el comprador
    CANCELADO    // Pedido cancelado (por el comprador o vendedor)
}
```

## Seguridad

El sistema implementa las siguientes medidas de seguridad:

1. **Autenticación de usuarios**: Solo usuarios autenticados pueden realizar pagos.
2. **Verificación de propiedad**: Se verifica que el usuario autenticado sea el propietario del pedido.
3. **Validación de estado**: Se verifica que el pedido esté en el estado correcto antes de procesar el pago.
4. **Protección contra CSRF**: Se utilizan tokens CSRF en los formularios.
5. **Enmascaramiento de datos**: Los datos sensibles de la tarjeta no se almacenan en la base de datos.

## Pruebas

El sistema incluye pruebas unitarias para validar el funcionamiento correcto del sistema de pagos:

1. **PagoServiceTest**: Pruebas para la lógica de negocio del servicio de pagos.
2. **PagoControllerTest**: Pruebas para los endpoints del controlador de pagos.

## Mejoras Futuras

1. Integración con pasarelas de pago reales (Stripe, PayPal, etc.).
2. Implementación de autenticación 3D Secure.
3. Soporte para múltiples métodos de pago.
4. Generación de facturas y recibos.
5. Sistema de reembolsos.

## Diagrama de Flujo

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Pedido    │     │    Pago     │     │Confirmación │
│  PENDIENTE  │────▶│  Procesar   │────▶│   PAGADO    │
└─────────────┘     └─────────────┘     └─────────────┘
                           │
                           │
                           ▼
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Pedido    │     │   Pedido    │     │   Pedido    │
│   ENVIADO   │◀────│   PAGADO    │────▶│  CANCELADO  │
└─────────────┘     └─────────────┘     └─────────────┘
       │
       │
       ▼
┌─────────────┐
│   Pedido    │
│  ENTREGADO  │
└─────────────┘
```

## Referencias

- [Documentación de Spring Security](https://docs.spring.io/spring-security/reference/index.html)
- [Mejores prácticas para procesamiento de pagos](https://www.pcisecuritystandards.org/)
- [Validación de tarjetas de crédito](https://www.regular-expressions.info/creditcard.html)