# Documentación de la API - DejaTuHuella

Este documento describe los endpoints de la API REST disponibles en la plataforma DejaTuHuella, incluyendo los relacionados con el sistema de pagos.

## Convenciones

- Base URL: `http://localhost:8081`
- Formato de respuesta: JSON
- Autenticación: Basada en sesiones con Spring Security

## Autenticación

### Iniciar Sesión

```
POST /login
```

**Parámetros de formulario:**
- `username`: Nombre de usuario o correo electrónico
- `password`: Contraseña

**Respuesta exitosa:**
- Redirección a la página principal

**Respuesta de error:**
- Redirección a `/login?error`

### Cerrar Sesión

```
POST /logout
```

**Respuesta exitosa:**
- Redirección a la página de inicio de sesión

## Usuarios

### Registrar Usuario

```
POST /registro
```

**Parámetros de formulario:**
- `nombre`: Nombre completo
- `email`: Correo electrónico
- `password`: Contraseña

**Respuesta exitosa:**
- Redirección a la página de inicio de sesión

**Respuesta de error:**
- Redirección a `/registro?error`

## Productos

### Listar Productos

```
GET /api/productos
```

**Parámetros de consulta:**
- `categoria` (opcional): ID de la categoría
- `page` (opcional): Número de página (por defecto: 0)
- `size` (opcional): Tamaño de página (por defecto: 10)

**Respuesta exitosa:**
```json
{
  "content": [
    {
      "id": 1,
      "nombre": "Producto 1",
      "descripcion": "Descripción del producto",
      "precio": 100.0,
      "stock": 10,
      "imagenUrl": "/uploads/imagen1.jpg",
      "categoria": {
        "id": 1,
        "nombre": "Categoría 1"
      },
      "vendedor": {
        "id": 1,
        "nombre": "Vendedor 1"
      }
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### Obtener Producto por ID

```
GET /api/productos/{id}
```

**Parámetros de ruta:**
- `id`: ID del producto

**Respuesta exitosa:**
```json
{
  "id": 1,
  "nombre": "Producto 1",
  "descripcion": "Descripción del producto",
  "precio": 100.0,
  "stock": 10,
  "imagenUrl": "/uploads/imagen1.jpg",
  "categoria": {
    "id": 1,
    "nombre": "Categoría 1"
  },
  "vendedor": {
    "id": 1,
    "nombre": "Vendedor 1"
  }
}
```

**Respuesta de error:**
```json
{
  "error": "Producto no encontrado",
  "status": 404
}
```

### Crear Producto

```
POST /api/productos
```

**Cuerpo de la solicitud:**
```json
{
  "nombre": "Nuevo Producto",
  "descripcion": "Descripción del nuevo producto",
  "precio": 150.0,
  "stock": 20,
  "categoriaId": 1
}
```

**Respuesta exitosa:**
```json
{
  "id": 2,
  "nombre": "Nuevo Producto",
  "descripcion": "Descripción del nuevo producto",
  "precio": 150.0,
  "stock": 20,
  "imagenUrl": null,
  "categoria": {
    "id": 1,
    "nombre": "Categoría 1"
  },
  "vendedor": {
    "id": 1,
    "nombre": "Vendedor 1"
  }
}
```

### Actualizar Producto

```
PUT /api/productos/{id}
```

**Parámetros de ruta:**
- `id`: ID del producto

**Cuerpo de la solicitud:**
```json
{
  "nombre": "Producto Actualizado",
  "descripcion": "Descripción actualizada",
  "precio": 200.0,
  "stock": 15,
  "categoriaId": 2
}
```

**Respuesta exitosa:**
```json
{
  "id": 1,
  "nombre": "Producto Actualizado",
  "descripcion": "Descripción actualizada",
  "precio": 200.0,
  "stock": 15,
  "imagenUrl": "/uploads/imagen1.jpg",
  "categoria": {
    "id": 2,
    "nombre": "Categoría 2"
  },
  "vendedor": {
    "id": 1,
    "nombre": "Vendedor 1"
  }
}
```

### Eliminar Producto

```
DELETE /api/productos/{id}
```

**Parámetros de ruta:**
- `id`: ID del producto

**Respuesta exitosa:**
```json
{
  "mensaje": "Producto eliminado correctamente"
}
```

## Pedidos

### Listar Pedidos del Usuario

```
GET /api/pedidos
```

**Respuesta exitosa:**
```json
[
  {
    "id": 1,
    "fecha": "2023-06-15T10:30:00",
    "estado": "PENDIENTE",
    "total": 300.0,
    "items": [
      {
        "id": 1,
        "producto": {
          "id": 1,
          "nombre": "Producto 1",
          "precio": 100.0
        },
        "cantidad": 3,
        "precioUnitario": 100.0,
        "subtotal": 300.0
      }
    ]
  }
]
```

### Obtener Pedido por ID

```
GET /api/pedidos/{id}
```

**Parámetros de ruta:**
- `id`: ID del pedido

**Respuesta exitosa:**
```json
{
  "id": 1,
  "fecha": "2023-06-15T10:30:00",
  "estado": "PENDIENTE",
  "total": 300.0,
  "items": [
    {
      "id": 1,
      "producto": {
        "id": 1,
        "nombre": "Producto 1",
        "precio": 100.0
      },
      "cantidad": 3,
      "precioUnitario": 100.0,
      "subtotal": 300.0
    }
  ]
}
```

### Crear Pedido

```
POST /api/pedidos
```

**Cuerpo de la solicitud:**
```json
{
  "items": [
    {
      "productoId": 1,
      "cantidad": 2
    },
    {
      "productoId": 2,
      "cantidad": 1
    }
  ]
}
```

**Respuesta exitosa:**
```json
{
  "id": 2,
  "fecha": "2023-06-16T14:20:00",
  "estado": "PENDIENTE",
  "total": 500.0,
  "items": [
    {
      "id": 2,
      "producto": {
        "id": 1,
        "nombre": "Producto 1",
        "precio": 100.0
      },
      "cantidad": 2,
      "precioUnitario": 100.0,
      "subtotal": 200.0
    },
    {
      "id": 3,
      "producto": {
        "id": 2,
        "nombre": "Producto 2",
        "precio": 300.0
      },
      "cantidad": 1,
      "precioUnitario": 300.0,
      "subtotal": 300.0
    }
  ]
}
```

### Actualizar Estado del Pedido

```
PUT /api/pedidos/{id}/estado
```

**Parámetros de ruta:**
- `id`: ID del pedido

**Cuerpo de la solicitud:**
```json
{
  "estado": "ENVIADO"
}
```

**Respuesta exitosa:**
```json
{
  "id": 1,
  "fecha": "2023-06-15T10:30:00",
  "estado": "ENVIADO",
  "total": 300.0,
  "items": [
    {
      "id": 1,
      "producto": {
        "id": 1,
        "nombre": "Producto 1",
        "precio": 100.0
      },
      "cantidad": 3,
      "precioUnitario": 100.0,
      "subtotal": 300.0
    }
  ]
}
```

### Cancelar Pedido

```
POST /api/pedidos/{id}/cancelar
```

**Parámetros de ruta:**
- `id`: ID del pedido

**Respuesta exitosa:**
```json
{
  "id": 1,
  "fecha": "2023-06-15T10:30:00",
  "estado": "CANCELADO",
  "total": 300.0,
  "items": [
    {
      "id": 1,
      "producto": {
        "id": 1,
        "nombre": "Producto 1",
        "precio": 100.0
      },
      "cantidad": 3,
      "precioUnitario": 100.0,
      "subtotal": 300.0
    }
  ]
}
```

## Pagos

### Obtener Formulario de Pago

```
GET /pago/{id}
```

**Parámetros de ruta:**
- `id`: ID del pedido

**Respuesta exitosa:**
- Renderiza la vista `pago.html` con los detalles del pedido

**Respuesta de error:**
- Redirección a `/mis-compras?error=pedido-no-encontrado`
- Redirección a `/mis-compras?error=pedido-no-autorizado`
- Redirección a `/mis-compras?error=pedido-no-pendiente`

### Procesar Pago

```
POST /pago/procesar/{id}
```

**Parámetros de ruta:**
- `id`: ID del pedido

**Parámetros de formulario:**
- `numeroTarjeta`: Número de la tarjeta de crédito
- `nombreTitular`: Nombre del titular de la tarjeta
- `mesExpiracion`: Mes de expiración (MM)
- `anioExpiracion`: Año de expiración (YY)
- `cvv`: Código de seguridad

**Respuesta exitosa:**
- Redirección a `/pago/confirmacion/{id}`

**Respuesta de error:**
- Redirección a `/pago/{id}?error=tarjeta`
- Redirección a `/pago/{id}?error=general`

### Confirmación de Pago

```
GET /pago/confirmacion/{id}
```

**Parámetros de ruta:**
- `id`: ID del pedido

**Respuesta exitosa:**
- Renderiza la vista `confirmacion.html` con los detalles del pedido y pago

**Respuesta de error:**
- Redirección a `/mis-compras?error=pedido-no-encontrado`
- Redirección a `/mis-compras?error=pedido-no-autorizado`

## Categorías

### Listar Categorías

```
GET /api/categorias
```

**Respuesta exitosa:**
```json
[
  {
    "id": 1,
    "nombre": "Categoría 1"
  },
  {
    "id": 2,
    "nombre": "Categoría 2"
  }
]
```

### Obtener Categoría por ID

```
GET /api/categorias/{id}
```

**Parámetros de ruta:**
- `id`: ID de la categoría

**Respuesta exitosa:**
```json
{
  "id": 1,
  "nombre": "Categoría 1"
}
```

## Carrito de Compras

### Obtener Carrito

```
GET /api/carrito
```

**Respuesta exitosa:**
```json
{
  "items": [
    {
      "productoId": 1,
      "nombre": "Producto 1",
      "precio": 100.0,
      "cantidad": 2,
      "subtotal": 200.0
    }
  ],
  "total": 200.0
}
```

### Agregar Producto al Carrito

```
POST /api/carrito/agregar
```

**Cuerpo de la solicitud:**
```json
{
  "productoId": 2,
  "cantidad": 1
}
```

**Respuesta exitosa:**
```json
{
  "items": [
    {
      "productoId": 1,
      "nombre": "Producto 1",
      "precio": 100.0,
      "cantidad": 2,
      "subtotal": 200.0
    },
    {
      "productoId": 2,
      "nombre": "Producto 2",
      "precio": 300.0,
      "cantidad": 1,
      "subtotal": 300.0
    }
  ],
  "total": 500.0
}
```

### Actualizar Cantidad en el Carrito

```
PUT /api/carrito/actualizar
```

**Cuerpo de la solicitud:**
```json
{
  "productoId": 1,
  "cantidad": 3
}
```

**Respuesta exitosa:**
```json
{
  "items": [
    {
      "productoId": 1,
      "nombre": "Producto 1",
      "precio": 100.0,
      "cantidad": 3,
      "subtotal": 300.0
    },
    {
      "productoId": 2,
      "nombre": "Producto 2",
      "precio": 300.0,
      "cantidad": 1,
      "subtotal": 300.0
    }
  ],
  "total": 600.0
}
```

### Eliminar Producto del Carrito

```
DELETE /api/carrito/eliminar/{productoId}
```

**Parámetros de ruta:**
- `productoId`: ID del producto

**Respuesta exitosa:**
```json
{
  "items": [
    {
      "productoId": 2,
      "nombre": "Producto 2",
      "precio": 300.0,
      "cantidad": 1,
      "subtotal": 300.0
    }
  ],
  "total": 300.0
}
```

### Vaciar Carrito

```
POST /api/carrito/vaciar
```

**Respuesta exitosa:**
```json
{
  "items": [],
  "total": 0.0
}
```

## Códigos de Estado HTTP

- `200 OK`: La solicitud se ha completado correctamente
- `201 Created`: El recurso se ha creado correctamente
- `400 Bad Request`: La solicitud contiene datos inválidos
- `401 Unauthorized`: No se ha proporcionado autenticación o es inválida
- `403 Forbidden`: No se tienen permisos para acceder al recurso
- `404 Not Found`: El recurso no existe
- `500 Internal Server Error`: Error interno del servidor

## Errores

Los errores se devuelven en el siguiente formato:

```json
{
  "error": "Descripción del error",
  "status": 400,
  "timestamp": "2023-06-15T10:30:00",
  "path": "/api/productos/999"
}
```

## Notas Adicionales

- Todas las solicitudes que modifican datos (POST, PUT, DELETE) requieren autenticación
- Las fechas se devuelven en formato ISO 8601 (YYYY-MM-DDTHH:MM:SS)
- Los precios y totales se devuelven como números de punto flotante
- Los estados de pedido válidos son: PENDIENTE, PAGADO, ENVIADO, ENTREGADO, CANCELADO