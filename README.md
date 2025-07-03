# DejaTuHuella - Plataforma de Comercio Electrónico

## Descripción

DejaTuHuella es una plataforma de ecommerce que permite a los usuarios comprar y vender productos de manera sencilla y segura. La plataforma incluye funcionalidades como gestión de productos, carrito de compras, procesamiento de pagos, seguimiento de pedidos y más.

## Características Principales

- **Gestión de Usuarios**: Registro, inicio de sesión y perfiles de usuario.
- **Catálogo de Productos**: Exploración de productos por categorías.
- **Carrito de Compras**: Añadir, actualizar y eliminar productos del carrito.
- **Procesamiento de Pagos**: Sistema seguro para procesar pagos con tarjeta de crédito.
- **Seguimiento de Pedidos**: Visualización del estado de los pedidos (pendiente, pagado, enviado, entregado, cancelado).
- **Panel de Control**: Gestión de productos para vendedores y seguimiento de compras para compradores.

## Tecnologías Utilizadas

- **Backend**: Java con Spring Boot
- **Frontend**: Thymeleaf, HTML, CSS, JavaScript
- **Base de Datos**: MySQL
- **Seguridad**: Spring Security

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/proyecto/dejatuhuella/
│   │       ├── config/          # Configuraciones de la aplicación
│   │       ├── controller/      # Controladores REST y MVC
│   │       ├── dto/             # Objetos de transferencia de datos
│   │       ├── exception/       # Manejo de excepciones
│   │       ├── model/           # Entidades de la base de datos
│   │       ├── repository/      # Repositorios JPA
│   │       ├── security/        # Configuración de seguridad
│   │       ├── service/         # Servicios de negocio
│   │       └── DejatuHuellaApplication.java  # Punto de entrada
│   └── resources/
│       ├── static/              # Recursos estáticos (CSS, JS, imágenes)
│       ├── templates/           # Plantillas Thymeleaf
│       └── application.properties  # Configuración de la aplicación
└── test/
    └── java/
        └── com/proyecto/dejatuhuella/
            ├── controller/      # Pruebas de controladores
            ├── repository/      # Pruebas de repositorios
            └── service/         # Pruebas de servicios
```

## Flujo de Compra

1. **Exploración**: El usuario navega por las categorías y productos.
2. **Selección**: Añade productos al carrito de compras.
3. **Checkout**: Revisa el carrito y procede al pago.
4. **Pago**: Ingresa los datos de la tarjeta y confirma el pago.
5. **Confirmación**: Recibe confirmación del pedido y puede seguir su estado.

## Modelos de Datos

### Usuario

Representa a los usuarios de la plataforma, que pueden ser compradores, vendedores o administradores.

**Atributos principales**:
- `id`: Identificador único
- `nombre`: Nombre del usuario
- `apellido`: Apellido del usuario
- `email`: Correo electrónico (usado para autenticación)
- `password`: Contraseña (almacenada encriptada)
- `telefono`: Número de teléfono
- `direccion`: Dirección de envío
- `rol`: Rol del usuario (USUARIO, ADMINISTRADOR)
- `activo`: Estado de la cuenta

**Relaciones**:
- `productosPublicados`: Productos que el usuario ha publicado para vender
- `pedidosRealizados`: Pedidos realizados por el usuario
- `carrito`: Carrito de compras del usuario

### Producto

Representa los artículos disponibles para la venta en la plataforma.

**Atributos principales**:
- `id`: Identificador único
- `nombre`: Nombre del producto
- `descripcion`: Descripción detallada
- `precio`: Precio del producto
- `stock`: Cantidad disponible
- `activo`: Indica si el producto está disponible
- `imagenUrl`: URL de la imagen del producto

**Relaciones**:
- `usuario`: Usuario que vende el producto
- `categoria`: Categoría a la que pertenece el producto
- `detallesPedido`: Detalles de pedidos que incluyen este producto
- `resenas`: Reseñas del producto

### Pedido

Representa una compra realizada por un usuario.

**Atributos principales**:
- `id`: Identificador único
- `fechaCreacion`: Fecha en que se realizó el pedido
- `estado`: Estado del pedido (PENDIENTE, ENVIADO, ENTREGADO, CANCELADO)
- `total`: Monto total del pedido

**Relaciones**:
- `usuario`: Usuario que realizó el pedido
- `detalles`: Detalles de los productos incluidos en el pedido

### Carrito

Representa el carrito de compras de un usuario.

**Atributos principales**:
- `id`: Identificador único

**Relaciones**:
- `usuario`: Usuario propietario del carrito
- `items`: Ítems incluidos en el carrito

## Servicios Principales

### UsuarioService

Gestiona las operaciones relacionadas con los usuarios.

**Métodos principales**:
- `crearUsuario`: Registra un nuevo usuario
- `obtenerTodosLosUsuarios`: Recupera todos los usuarios
- `obtenerUsuarioPorId`: Busca un usuario por su ID
- `actualizarPerfilUsuario`: Actualiza el perfil de un usuario
- `actualizarUsuario`: Actualiza los datos de un usuario (admin)
- `eliminarUsuario`: Elimina un usuario
- `cambiarEstadoUsuario`: Activa o desactiva un usuario

### ProductoService

Gestiona las operaciones relacionadas con los productos.

**Métodos principales**:
- `obtenerTodosLosProductos`: Recupera todos los productos
- `obtenerProductoPorId`: Busca un producto por su ID
- `obtenerProductosPorUsuario`: Recupera productos de un usuario específico
- `crearProducto`: Crea un nuevo producto
- `actualizarProducto`: Actualiza un producto existente
- `eliminarProducto`: Elimina un producto
- `obtenerProductosPorCategoria`: Recupera productos por categoría
- `buscarProductos`: Busca productos por nombre
- `cambiarEstadoProducto`: Activa o desactiva un producto

### CarritoPersistentService

Gestiona las operaciones relacionadas con el carrito de compras persistente.

**Métodos principales**:
- `obtenerCarritoUsuarioActual`: Recupera el carrito del usuario actual
- `agregarProducto`: Añade un producto al carrito
- `actualizarCantidad`: Actualiza la cantidad de un producto en el carrito
- `eliminarProducto`: Elimina un producto del carrito
- `vaciarCarrito`: Elimina todos los productos del carrito
- `getItemsDetallados`: Obtiene los items del carrito con detalles
- `getTotal`: Calcula el total del carrito
- `verificarStock`: Verifica si hay suficiente stock para todos los productos del carrito

### PedidoService

Gestiona las operaciones relacionadas con los pedidos.

**Métodos principales**:
- `crearPedido`: Crea un nuevo pedido a partir del carrito
- `obtenerPedidosPorUsuario`: Recupera pedidos de un usuario
- `obtenerPedidoPorId`: Busca un pedido por su ID
- `actualizarEstadoPedido`: Actualiza el estado de un pedido
- `cancelarPedido`: Cancela un pedido

## Seguridad

La aplicación utiliza Spring Security con JWT para la autenticación y autorización.

### Roles y Permisos

- **USUARIO**: Puede gestionar su perfil, publicar productos, realizar compras y gestionar sus pedidos.
- **ADMINISTRADOR**: Tiene acceso completo a la plataforma, incluyendo la gestión de usuarios, productos y pedidos.

### SeguridadService

Proporciona métodos para verificar permisos específicos.

**Métodos principales**:
- `getUsuarioAutenticado`: Obtiene el usuario actualmente autenticado
- `esUsuarioActual`: Verifica si el usuario autenticado es el mismo que se está consultando
- `esPropietarioDelProducto`: Verifica si el usuario es propietario de un producto
- `esPropietarioDelPedido`: Verifica si el usuario es propietario de un pedido
- `esVendedorEnPedido`: Verifica si el usuario es vendedor de algún producto en un pedido

## Pruebas

El proyecto incluye pruebas unitarias y de integración para garantizar la calidad del código.

### Pruebas de Servicios

- **UsuarioServiceTest**: Prueba las operaciones de gestión de usuarios
- **ProductoServiceTest**: Prueba las operaciones de gestión de productos
- **CarritoPersistentServiceTest**: Prueba las operaciones del carrito de compras persistente
- **PedidoServiceTest**: Prueba las operaciones de gestión de pedidos
- **SeguridadServiceTest**: Prueba las verificaciones de seguridad
- **FileStorageServiceTest**: Prueba el almacenamiento de archivos

### Pruebas de Controladores

- **UsuarioControllerTest**: Prueba los endpoints de usuarios
- **ProductoControllerTest**: Prueba los endpoints de productos
- **CarritoPersistenteControllerTest**: Prueba los endpoints del carrito persistente
- **PedidoControllerTest**: Prueba los endpoints de pedidos

## Sistema de Pagos

El sistema de pagos implementa un flujo seguro para procesar transacciones:

1. **Validación de Pedido**: Verifica que el pedido exista y pertenezca al usuario autenticado.
2. **Formulario de Pago**: Interfaz para ingresar datos de la tarjeta con validación en tiempo real.
3. **Procesamiento**: Validación de los datos de la tarjeta y simulación de procesamiento con pasarela de pago.
4. **Actualización de Estado**: Cambio automático del estado del pedido a "PAGADO" tras un pago exitoso.
5. **Confirmación**: Página de confirmación con detalles del pedido y resumen del pago.

## Estados de Pedido

- **PENDIENTE**: Pedido creado pero pendiente de pago.
- **PAGADO**: Pago confirmado, listo para ser procesado.
- **ENVIADO**: Pedido en camino al comprador.
- **ENTREGADO**: Pedido recibido por el comprador.
- **CANCELADO**: Pedido cancelado (por el comprador o vendedor).

## Seguridad

- Autenticación de usuarios mediante Spring Security.
- Validación de permisos para acciones específicas.
- Protección contra CSRF en formularios.
- Enmascaramiento de datos sensibles de tarjetas de crédito.

## Instalación y Ejecución

1. Clonar el repositorio:
   ```
   git clone https://github.com/tu-usuario/DejaTuHuella.git
   ```

2. Configurar la base de datos en `application.properties`.

3. Ejecutar la aplicación:
   ```
   ./mvnw spring-boot:run
   ```

4. Acceder a la aplicación en `http://localhost:8080`.

## Pruebas

Ejecutar las pruebas unitarias e integración:
```
./mvnw test
```

## Contribución

1. Hacer fork del repositorio.
2. Crear una rama para tu funcionalidad (`git checkout -b feature/nueva-funcionalidad`).
3. Hacer commit de tus cambios (`git commit -m 'Añadir nueva funcionalidad'`).
4. Hacer push a la rama (`git push origin feature/nueva-funcionalidad`).
5. Crear un Pull Request.

## Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.
