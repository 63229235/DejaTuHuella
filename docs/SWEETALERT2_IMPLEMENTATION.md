# Guía de Implementación de SweetAlert2

## Descripción General

Este proyecto ha sido mejorado con SweetAlert2 para proporcionar diálogos de alerta hermosos, responsivos y personalizables. SweetAlert2 reemplaza las alertas predeterminadas del navegador con notificaciones modernas y amigables para el usuario.

## Archivos Modificados

### Archivos Principales de SweetAlert2

1. **`/js/sweetalert-utils.js`** - Archivo de utilidades central que contiene todas las funciones de SweetAlert2
2. **`/templates/fragments/sweetalert.html`** - Fragmento reutilizable de Thymeleaf para incluir scripts de SweetAlert2

### Plantillas Actualizadas

Las siguientes plantillas han sido actualizadas para incluir SweetAlert2:

- `home.html`
- `registro.html`
- `login.html`
- `carrito.html`
- `producto-detalle.html`
- `mis-compras.html`
- `mis-ventas.html`
- `categorias.html`
- `productos-categoria.html`
- `panel-control.html`
- `pago/formulario.html`
- `pago/confirmacion.html`

### Archivos JavaScript Actualizados

Los siguientes archivos JavaScript han sido actualizados para usar SweetAlert2:

- `home.js` - Notificaciones de adición de productos
- `registro.js` - Mensajes de éxito/error de registro
- `pago.js` - Mensajes de error de pago
- `panel-control.js` - Confirmaciones y notificaciones del panel de administración
- `resenas.js` - Validaciones y confirmaciones de reseñas

## Funciones Disponibles

El archivo `sweetalert-utils.js` proporciona las siguientes funciones:

### Alertas Básicas

- `showSuccess(title, text)` - Mensaje de éxito con marca de verificación verde
- `showError(title, text)` - Mensaje de error con X roja
- `showInfo(title, text)` - Mensaje de información con i azul
- `showWarning(title, text)` - Mensaje de advertencia con triángulo amarillo

### Diálogos de Confirmación

- `showConfirm(title, text, confirmCallback, cancelCallback)` - Diálogo de confirmación con callbacks personalizados

### Notificaciones Toast

- `showToast(type, message)` - Notificación pequeña que aparece brevemente
  - Tipos: 'success', 'error', 'warning', 'info'

```javascript
// Mensaje de éxito
showSuccess('¡Operación completada exitosamente!');

// Mensaje de error
showError('¡Algo salió mal!');

// Mensaje de información
showInfo('Aquí tienes información.');

// Mensaje de advertencia
showWarning('¡Ten cuidado!');

// Confirmación con callback
showConfirm('¿Estás seguro?', 'Esta acción no se puede deshacer', function() {
    // Código a ejecutar si se confirma
    console.log('Usuario confirmó');
});

// Toast de éxito
showToastSuccess('¡Elemento agregado al carrito!');

// Toast de error
showToastError('Error al guardar cambios');

// Toast de información
showToastInfo('Nueva actualización disponible');

// Toast de advertencia
showToastWarning('La sesión expirará pronto');
```

## Procesamiento Automático de Mensajes Flash

El archivo `sweetalert-utils.js` incluye procesamiento automático de mensajes flash de Thymeleaf. Cuando se carga una página, automáticamente:

1. Detecta divs de alerta de Bootstrap con clases: `alert-success`, `alert-danger`, `alert-info`, `alert-warning`
2. Los convierte en notificaciones toast de SweetAlert2
3. Oculta los divs de alerta originales

Esto significa que los mensajes flash existentes aparecerán automáticamente como notificaciones toast modernas sin requerir cambios de código.

## Uso en Plantillas

### Incluyendo SweetAlert2

Para incluir SweetAlert2 en una plantilla, agrega esta línea antes de tu JavaScript personalizado:

```html
<!-- SweetAlert2 -->
<div th:replace="~{fragments/sweetalert :: sweetalert-scripts}"></div>
```

### Ejemplo de Implementación

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <!-- Contenido de tu head -->
</head>
<body>
    <!-- Contenido de tu página -->
    
    <!-- Mensajes flash (convertidos automáticamente a toasts) -->
    <div class="alert alert-success" th:if="${mensaje}" th:text="${mensaje}"></div>
    <div class="alert alert-danger" th:if="${error}" th:text="${error}"></div>
    
    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <!-- SweetAlert2 -->
    <div th:replace="~{fragments/sweetalert :: sweetalert-scripts}"></div>
    <script th:src="@{/js/your-custom-script.js}"></script>
</body>
</html>
```

## Mejores Prácticas

1. **Usa tipos de alerta apropiados**: Elige la función correcta basada en el tipo de mensaje (éxito, error, información, advertencia)
2. **Mantén los mensajes concisos**: Los mensajes cortos y claros funcionan mejor con SweetAlert2
3. **Usa toasts para notificaciones no críticas**: Los toasts son menos intrusivos para actualizaciones de estado
4. **Usa confirmaciones para acciones destructivas**: Siempre confirma operaciones de eliminación y otras acciones irreversibles
5. **Estilo consistente**: Las funciones de utilidad aseguran una apariencia consistente en toda la aplicación

## Migración desde Alertas Nativas

### Antes (JavaScript Nativo)
```javascript
alert('¡Éxito!');
if (confirm('¿Estás seguro?')) {
    // hacer algo
}
```

### Después (SweetAlert2)
```javascript
showToastSuccess('¡Éxito!');
showConfirm('¿Estás seguro?', 'Esta acción no se puede deshacer', function() {
    // hacer algo
});
```

## Personalización

El archivo `sweetalert-utils.js` puede ser extendido con funciones adicionales según sea necesario. Todas las opciones de configuración de SweetAlert2 están disponibles para personalización.

## Dependencias

- SweetAlert2 v11 (cargado desde CDN)
- Bootstrap 5.3.0 (para compatibilidad de estilos)

## Soporte de Navegadores

SweetAlert2 soporta todos los navegadores modernos:
- Chrome 32+
- Firefox 27+
- Safari 9+
- Edge 79+
- Internet Explorer 11 (con polyfills)

---

*Esta implementación proporciona una experiencia de usuario moderna y consistente en toda la aplicación mientras mantiene la compatibilidad hacia atrás con la funcionalidad existente.*