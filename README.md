# DejaTuHuella - Plataforma de Comercio Electrónico

## Descripción

DejaTuHuella es una plataforma de comercio electrónico que permite a los usuarios comprar y vender productos de manera sencilla y segura. La plataforma incluye funcionalidades como gestión de productos, carrito de compras, procesamiento de pagos, seguimiento de pedidos y más.

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
│   │       ├── model/           # Entidades de la base de datos
│   │       ├── repository/      # Repositorios JPA
│   │       ├── service/         # Servicios de negocio
│   │       └── DejatuHuellaApplication.java  # Punto de entrada
│   └── resources/
│       ├── static/              # Recursos estáticos (CSS, JS, imágenes)
│       ├── templates/           # Plantillas Thymeleaf
│       └── application.properties  # Configuración de la aplicación
└── test/                        # Pruebas unitarias e integración
```

## Flujo de Compra

1. **Exploración**: El usuario navega por las categorías y productos.
2. **Selección**: Añade productos al carrito de compras.
3. **Checkout**: Revisa el carrito y procede al pago.
4. **Pago**: Ingresa los datos de la tarjeta y confirma el pago.
5. **Confirmación**: Recibe confirmación del pedido y puede seguir su estado.

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