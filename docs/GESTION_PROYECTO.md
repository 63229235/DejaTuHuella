# Gestión del Proyecto DejaTuHuella

## 1. Información General del Proyecto

### Nombre del Proyecto
**DejaTuHuella - Plataforma de Comercio Electrónico**

### Objetivos
- **Objetivo Principal**: Desarrollar una plataforma de e-commerce completa que permita a los usuarios comprar y vender productos de manera segura y eficiente.
- **Objetivos Específicos**:
  - Implementar un sistema de gestión de usuarios con roles diferenciados
  - Crear un catálogo de productos organizado por categorías
  - Desarrollar un sistema de carrito de compras persistente
  - Integrar un sistema de procesamiento de pagos seguro
  - Implementar seguimiento de pedidos en tiempo real
  - Crear un panel de control administrativo

### Alcance del Proyecto

#### ¿Qué hará el sistema?
- Registro y autenticación de usuarios (incluyendo OAuth2 con Google)
- Gestión de perfiles de usuario
- Publicación y gestión de productos por parte de vendedores
- Navegación y búsqueda de productos por categorías
- Sistema de carrito de compras persistente
- Procesamiento de pagos con tarjeta de crédito
- Seguimiento de pedidos con diferentes estados
- Panel de control administrativo para gestión de usuarios y productos
- Notificaciones y alertas del sistema

#### ¿Qué NO hará el sistema?
- Gestión de inventario físico en almacenes
- Integración con sistemas de envío externos
- Procesamiento de devoluciones automáticas
- Chat en tiempo real entre usuarios
- Sistema de subastas
- Integración con redes sociales para marketing
- Análisis avanzado de datos de ventas
- Sistema de reseñas y calificaciones avanzado

### Cronograma del Proyecto

| Fase | Actividad | Duración | Fecha Inicio | Fecha Fin |
|------|-----------|----------|--------------|----------|
| 1 | Análisis y Diseño | 2 semanas | Semana 1 | Semana 2 |
| 2 | Configuración del Entorno | 1 semana | Semana 3 | Semana 3 |
| 3 | Desarrollo Backend (Modelos y Servicios) | 3 semanas | Semana 4 | Semana 6 |
| 4 | Desarrollo Frontend (Interfaces) | 2 semanas | Semana 7 | Semana 8 |
| 5 | Integración y Pruebas | 2 semanas | Semana 9 | Semana 10 |
| 6 | Documentación y Despliegue | 1 semana | Semana 11 | Semana 11 |
| 7 | Pruebas Finales y Entrega | 1 semana | Semana 12 | Semana 12 |

## 2. Requerimientos Funcionales

### Gestión de Usuarios
- **RF-01**: El sistema debe permitir el registro de nuevos usuarios con email y contraseña
- **RF-02**: El sistema debe permitir la autenticación mediante OAuth2 con Google
- **RF-03**: El sistema debe permitir el inicio de sesión con credenciales válidas
- **RF-04**: El sistema debe permitir la actualización del perfil de usuario
- **RF-05**: El sistema debe diferenciar entre roles de USUARIO y ADMINISTRADOR
- **RF-06**: El sistema debe permitir la activación/desactivación de cuentas de usuario

### Gestión de Productos
- **RF-07**: El sistema debe permitir a los usuarios publicar productos con imagen, descripción y precio
- **RF-08**: El sistema debe organizar los productos por categorías
- **RF-09**: El sistema debe permitir la búsqueda de productos por nombre
- **RF-10**: El sistema debe mostrar productos destacados en la página principal
- **RF-11**: El sistema debe permitir la edición y eliminación de productos por sus propietarios
- **RF-12**: El sistema debe controlar el stock de productos

### Carrito de Compras
- **RF-13**: El sistema debe permitir agregar productos al carrito de compras
- **RF-14**: El sistema debe mantener el carrito persistente entre sesiones
- **RF-15**: El sistema debe permitir modificar cantidades de productos en el carrito
- **RF-16**: El sistema debe permitir eliminar productos del carrito
- **RF-17**: El sistema debe calcular el total del carrito automáticamente

### Procesamiento de Pedidos
- **RF-18**: El sistema debe permitir crear pedidos a partir del carrito
- **RF-19**: El sistema debe procesar pagos con tarjeta de crédito
- **RF-20**: El sistema debe generar confirmaciones de pedido
- **RF-21**: El sistema debe permitir el seguimiento de estados de pedido
- **RF-22**: El sistema debe permitir la cancelación de pedidos



### Administración
- **RF-26**: El sistema debe proporcionar un panel administrativo
- **RF-27**: Los administradores deben poder gestionar usuarios
- **RF-28**: Los administradores deben poder gestionar productos
- **RF-29**: Los administradores deben poder gestionar categorías
- **RF-30**: El sistema debe generar reportes básicos de ventas

## 3. Requerimientos No Funcionales

### Rendimiento
- **RNF-01**: La página principal debe cargar en menos de 3 segundos
- **RNF-02**: Las búsquedas de productos deben responder en menos de 2 segundos
- **RNF-03**: El sistema debe soportar al menos 100 usuarios concurrentes
- **RNF-04**: Los productos destacados deben actualizarse cada 1 minuto

### Seguridad
- **RNF-05**: Las contraseñas deben estar encriptadas con BCrypt
- **RNF-06**: El sistema debe implementar protección CSRF
- **RNF-07**: Las sesiones deben expirar después de 30 minutos de inactividad
- **RNF-08**: Los datos de tarjetas de crédito deben procesarse de forma segura
- **RNF-09**: El acceso a funciones administrativas debe estar restringido por roles

### Usabilidad
- **RNF-10**: El diseño debe ser responsivo y adaptarse a dispositivos móviles
- **RNF-11**: La interfaz debe ser intuitiva y fácil de navegar
- **RNF-12**: El sistema debe proporcionar mensajes de error claros
- **RNF-13**: Las acciones importantes deben tener confirmaciones

### Compatibilidad
- **RNF-14**: El sistema debe funcionar en navegadores Chrome, Firefox, Safari y Edge
- **RNF-15**: El sistema debe ser compatible con dispositivos móviles
- **RNF-16**: La base de datos debe ser MySQL 8.0 o superior

### Mantenibilidad
- **RNF-17**: El código debe seguir las convenciones de Spring Boot
- **RNF-18**: El sistema debe tener cobertura de pruebas del 80%
- **RNF-19**: La documentación técnica debe estar actualizada
- **RNF-20**: El sistema debe usar logging para auditoría

## 4. Casos de Uso

### CU-01: Registro de Usuario
- **Actor**: Usuario no registrado
- **Resumen**: Un visitante se registra en la plataforma para poder comprar y vender productos
- **Precondiciones**: El usuario no debe estar registrado previamente
- **Pasos**:
  1. El usuario accede a la página de registro
  2. Completa el formulario con nombre, apellido, email y contraseña
  3. Acepta los términos y condiciones
  4. Hace clic en "Registrarse"
  5. El sistema valida los datos y crea la cuenta
  6. El usuario recibe confirmación de registro
- **Postcondiciones**: El usuario queda registrado y puede iniciar sesión

### CU-02: Publicar Producto
- **Actor**: Usuario vendedor
- **Resumen**: Un usuario registrado publica un producto para vender
- **Precondiciones**: El usuario debe estar autenticado
- **Pasos**:
  1. El usuario accede al panel de control
  2. Selecciona "Publicar Producto"
  3. Completa el formulario con datos del producto
  4. Sube una imagen del producto
  5. Selecciona la categoría apropiada
  6. Establece precio y stock
  7. Hace clic en "Publicar"
  8. El sistema valida y guarda el producto
- **Postcondiciones**: El producto queda disponible en el catálogo

### CU-03: Realizar Compra
- **Actor**: Usuario comprador
- **Resumen**: Un usuario realiza una compra de productos en su carrito
- **Precondiciones**: El usuario debe estar autenticado y tener productos en el carrito
- **Pasos**:
  1. El usuario revisa su carrito de compras
  2. Verifica cantidades y productos
  3. Hace clic en "Proceder al pago"
  4. Ingresa datos de la tarjeta de crédito
  5. Confirma la dirección de envío
  6. Hace clic en "Confirmar Pago"
  7. El sistema procesa el pago
  8. Se genera el pedido y se envía confirmación
- **Postcondiciones**: El pedido queda registrado y el carrito se vacía

### CU-04: Gestionar Usuarios (Administrador)
- **Actor**: Administrador
- **Resumen**: Un administrador gestiona las cuentas de usuario
- **Precondiciones**: El usuario debe tener rol de ADMINISTRADOR
- **Pasos**:
  1. El administrador accede al panel administrativo
  2. Selecciona "Gestión de Usuarios"
  3. Ve la lista de todos los usuarios
  4. Puede activar/desactivar cuentas
  5. Puede editar información de usuarios
  6. Puede eliminar usuarios si es necesario
- **Postcondiciones**: Los cambios se reflejan en el sistema



## 5. Matriz de Trazabilidad

| ID Requerimiento | Descripción | Casos de Uso | Casos de Prueba |
|------------------|-------------|--------------|----------------|
| RF-01 | Registro de usuarios | CU-01 | CP-01, CP-02 |
| RF-02 | Autenticación OAuth2 | CU-01 | CP-03 |
| RF-03 | Inicio de sesión | - | CP-04, CP-05 |
| RF-07 | Publicar productos | CU-02 | CP-06, CP-07 |
| RF-13 | Agregar al carrito | CU-03 | CP-08 |
| RF-18 | Crear pedidos | CU-03 | CP-09, CP-10 |

| RF-27 | Gestión de usuarios | CU-04 | CP-12, CP-13 |
| RNF-01 | Rendimiento página principal | - | CP-14 |
| RNF-05 | Encriptación contraseñas | CU-01 | CP-15 |
| RNF-10 | Diseño responsivo | - | CP-16 |

---

**Fecha de Creación**: $(date)
**Versión**: 1.0
**Estado**: En Desarrollo