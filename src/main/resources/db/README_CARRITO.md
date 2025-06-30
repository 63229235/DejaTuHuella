# Implementación del Carrito Persistente

Este documento describe la implementación de un carrito de compras persistente en la base de datos para la aplicación DejaTuHuella.

## Archivos Creados

### Modelos
- `Carrito.java`: Entidad que representa un carrito de compras asociado a un usuario.
- `CarritoItem.java`: Entidad que representa un ítem dentro del carrito (producto, cantidad, precio).

### Repositorios
- `CarritoRepository.java`: Repositorio JPA para la entidad Carrito.
- `CarritoItemRepository.java`: Repositorio JPA para la entidad CarritoItem.

### Servicios
- `CarritoPersistentService.java`: Servicio que gestiona las operaciones del carrito persistente.

### Controladores
- `CarritoPersistenteController.java`: Controlador MVC para las operaciones del carrito.
- `CarritoPersistenteRestController.java`: Controlador REST para las operaciones del carrito vía API.

### Scripts SQL
- `carrito_tables.sql`: Script para crear las tablas necesarias en la base de datos.
- `carrito_data.sql`: Script con datos de ejemplo para las tablas del carrito.

## Pasos para la Implementación

1. **Crear las tablas en la base de datos**:
   - Ejecutar el script `carrito_tables.sql` en la base de datos.

2. **Configurar la aplicación**:
   - Asegurarse de que las entidades `Carrito` y `CarritoItem` estén escaneadas por JPA.
   - Verificar que los repositorios estén correctamente configurados como beans de Spring.

3. **Probar la funcionalidad**:
   - Iniciar la aplicación.
   - Registrar un usuario o iniciar sesión con uno existente.
   - Agregar productos al carrito y verificar que se persistan en la base de datos.
   - Cerrar sesión y volver a iniciar para comprobar que el carrito se mantiene.

## Verificación

Para verificar que el carrito persistente funciona correctamente:

1. Consultar la base de datos para ver si se han creado registros en las tablas `carritos` y `carrito_items`.
2. Comprobar que al agregar productos al carrito, estos se guardan en la base de datos.
3. Verificar que al cerrar sesión y volver a iniciar, el carrito mantiene los productos agregados anteriormente.

## Solución de Problemas

Si encuentras problemas con la implementación del carrito persistente:

1. **Error al crear las tablas**:
   - Verificar que el script SQL se ejecuta correctamente.
   - Comprobar que las tablas referenciadas (`usuarios`, `productos`) existen.

2. **Error al guardar en el carrito**:
   - Verificar que el usuario está autenticado.
   - Comprobar que el producto existe en la base de datos.
   - Revisar los logs para ver si hay errores específicos.

3. **El carrito no persiste entre sesiones**:
   - Verificar que el servicio `CarritoPersistentService` está siendo utilizado correctamente.
   - Comprobar que la autenticación funciona correctamente y que se obtiene el usuario correcto.

## Diferencias con el Carrito Anterior

El carrito anterior utilizaba la sesión HTTP para almacenar los productos, lo que significa que los datos se perdían cuando el usuario cerraba sesión o cuando la sesión expiraba. El nuevo carrito persistente almacena los datos en la base de datos, lo que permite mantener el carrito entre sesiones y dispositivos.

Principales diferencias:

- **Persistencia**: Los datos se guardan en la base de datos en lugar de en la sesión.
- **Asociación con usuario**: El carrito está directamente asociado a un usuario mediante una relación en la base de datos.
- **Consistencia**: Los precios y disponibilidad de productos se mantienen actualizados con la base de datos.
- **Experiencia de usuario**: El usuario puede continuar su compra desde donde la dejó, incluso después de cerrar sesión.