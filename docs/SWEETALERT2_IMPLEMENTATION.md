# SweetAlert2 Implementation Guide

## Overview

This project has been enhanced with SweetAlert2 to provide beautiful, responsive, and customizable alert dialogs. SweetAlert2 replaces the default browser alerts with modern, user-friendly notifications.

## Files Modified

### Core SweetAlert2 Files

1. **`/js/sweetalert-utils.js`** - Central utility file containing all SweetAlert2 functions
2. **`/templates/fragments/sweetalert.html`** - Reusable Thymeleaf fragment for including SweetAlert2 scripts

### Updated Templates

The following templates have been updated to include SweetAlert2:

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

### Updated JavaScript Files

The following JavaScript files have been updated to use SweetAlert2:

- `home.js` - Product addition notifications
- `registro.js` - Registration success/error messages
- `pago.js` - Payment error messages
- `panel-control.js` - Admin panel confirmations and notifications

## Available Functions

### Basic Alerts

```javascript
// Success message
showSuccess('Operation completed successfully!');

// Error message
showError('Something went wrong!');

// Info message
showInfo('Here is some information.');

// Warning message
showWarning('Please be careful!');
```

### Confirmation Dialogs

```javascript
// Confirmation with callback
showConfirm('Are you sure?', 'This action cannot be undone', function() {
    // Code to execute if confirmed
    console.log('User confirmed');
});
```

### Toast Notifications

```javascript
// Success toast
showToastSuccess('Item added to cart!');

// Error toast
showToastError('Failed to save changes');

// Info toast
showToastInfo('New update available');

// Warning toast
showToastWarning('Session will expire soon');
```

## Automatic Flash Message Processing

The `sweetalert-utils.js` file includes automatic processing of Thymeleaf flash messages. When a page loads, it automatically:

1. Detects Bootstrap alert divs with classes: `alert-success`, `alert-danger`, `alert-info`, `alert-warning`
2. Converts them to SweetAlert2 toast notifications
3. Hides the original alert divs

This means existing flash messages will automatically appear as modern toast notifications without requiring code changes.

## Usage in Templates

### Including SweetAlert2

To include SweetAlert2 in a template, add this line before your custom JavaScript:

```html
<!-- SweetAlert2 -->
<div th:replace="~{fragments/sweetalert :: sweetalert-scripts}"></div>
```

### Example Implementation

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <!-- Your head content -->
</head>
<body>
    <!-- Your page content -->
    
    <!-- Flash messages (automatically converted to toasts) -->
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

## Best Practices

1. **Use appropriate alert types**: Choose the right function based on the message type (success, error, info, warning)
2. **Keep messages concise**: Short, clear messages work best with SweetAlert2
3. **Use toasts for non-critical notifications**: Toasts are less intrusive for status updates
4. **Use confirmations for destructive actions**: Always confirm delete operations and other irreversible actions
5. **Consistent styling**: The utility functions ensure consistent appearance across the application

## Migration from Native Alerts

### Before (Native JavaScript)
```javascript
alert('Success!');
if (confirm('Are you sure?')) {
    // do something
}
```

### After (SweetAlert2)
```javascript
showToastSuccess('Success!');
showConfirm('Are you sure?', 'This action cannot be undone', function() {
    // do something
});
```

## Customization

The `sweetalert-utils.js` file can be extended with additional functions as needed. All SweetAlert2 configuration options are available for customization.

## Dependencies

- SweetAlert2 v11 (loaded from CDN)
- Bootstrap 5.3.0 (for styling compatibility)

## Browser Support

SweetAlert2 supports all modern browsers. For older browser support, consider using the legacy version of SweetAlert2.