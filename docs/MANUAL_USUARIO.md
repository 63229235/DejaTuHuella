# Manual de Usuario - DejaTuHuella

## Tabla de Contenidos
1. [Introducción](#introducción)
2. [Cómo Empezar](#cómo-empezar)
3. [Funciones Principales](#funciones-principales)
4. [Panel de Control](#panel-de-control)
5. [Administración](#administración)
6. [Preguntas Frecuentes](#preguntas-frecuentes)
7. [Soporte Técnico](#soporte-técnico)

---

## Introducción

### ¿Qué es DejaTuHuella?

**DejaTuHuella** es una plataforma de comercio electrónico moderna y segura que permite a los usuarios:
- **Comprar productos** de diversos vendedores
- **Vender sus propios productos** de manera sencilla
- **Gestionar pedidos** y seguimiento de compras
- **Administrar su perfil** y preferencias

### Características Principales
- ✅ **Registro seguro** con email o Google
- 🛒 **Carrito persistente** que guarda tus productos
- 💳 **Pagos seguros** con validación de tarjetas
- 📱 **Diseño responsivo** para móviles y computadoras
- 🔒 **Seguridad avanzada** con encriptación

---

## Cómo Empezar

### Acceso a la Plataforma

**URL de Acceso**: `https://dejatuhuella-production.up.railway.app/` (producción)

**URL de Desarrollo**: `http://localhost:8081` (solo para desarrollo local)

### Registro de Usuario

#### Opción 1: Registro con Email

1. **Navegar a la página de registro**
   - Hacer clic en "Registrarse" en la página principal
   - O ir directamente a `/registro`

2. **Completar el formulario**
   ```
   📝 Campos requeridos:
   • Nombre
   • Apellido  
   • Email (debe ser único)
   • Contraseña (mínimo 6 caracteres)
   • Teléfono
   • Dirección
   ```

3. **Confirmar registro**
   - Hacer clic en "Registrarse"
   - Serás redirigido a la página de login

#### Opción 2: Registro con Google OAuth2

1. **En la página de login**
   - Hacer clic en "Iniciar con Google"
   - Autorizar el acceso en Google
   - Tu cuenta se creará automáticamente

### Iniciar Sesión

1. **Ir a la página de login** (`/login`)
2. **Ingresar credenciales**:
   - Email registrado
   - Contraseña
3. **Hacer clic en "Iniciar Sesión"**
4. **Serás redirigido** a la página principal

---

## Funciones Principales

### 🛍️ Explorar y Comprar Productos

#### Navegación por el Catálogo

1. **Página Principal**
   - Ver productos destacados (se actualizan cada minuto)
   - Navegar por categorías
   - Usar la barra de búsqueda

2. **Filtros y Búsqueda**
   ```
   🔍 Opciones de búsqueda:
   • Por nombre de producto
   • Por categoría
   • Por rango de precio
   ```

#### Ver Detalles de Producto

1. **Hacer clic en cualquier producto**
2. **Información disponible**:
   - Descripción completa
   - Precio y stock disponible
   - Imágenes del producto
   - Información del vendedor

#### Agregar al Carrito

1. **En la página de detalle del producto**
2. **Hacer clic en "Agregar al Carrito"**
3. **Verificar confirmación**:
   - Notificación de éxito
   - Contador del carrito actualizado

### 🛒 Gestión del Carrito

#### Ver Carrito

1. **Hacer clic en el ícono del carrito** (esquina superior derecha)
2. **Revisar productos agregados**:
   - Nombre y precio de cada producto
   - Cantidad seleccionada
   - Subtotal por producto
   - Total general

#### Modificar Carrito

- **Cambiar cantidad**: Usar los botones +/- junto a cada producto
- **Eliminar producto**: Hacer clic en el botón "Eliminar"
- **Vaciar carrito**: Usar el botón "Vaciar Carrito"

### 💳 Proceso de Compra

#### Proceder al Pago

1. **Desde el carrito, hacer clic en "Proceder al Pago"**
2. **Revisar resumen del pedido**:
   - Lista de productos
   - Cantidades y precios
   - Total a pagar

3. **Completar datos de pago**:
   ```
   💳 Información requerida:
   • Número de tarjeta (16 dígitos)
   • Fecha de vencimiento (MM/AA)
   • Código CVV (3 dígitos)
   • Nombre del titular
   ```

4. **Confirmar pedido**
   - Revisar todos los datos
   - Hacer clic en "Confirmar Pedido"
   - Recibir confirmación por email

#### Estados del Pedido

```
📦 Estados posibles:
• PENDIENTE - Pedido recibido, procesando pago
• CONFIRMADO - Pago confirmado, preparando envío
• ENVIADO - Producto en camino
• ENTREGADO - Pedido completado
• CANCELADO - Pedido cancelado
```



---

## Panel de Control

### Acceso al Panel

1. **Iniciar sesión** en tu cuenta
2. **Hacer clic en tu nombre** (esquina superior derecha)
3. **Seleccionar "Panel de Control"**

### 📊 Dashboard Principal

**Información disponible**:
- Resumen de productos publicados
- Estadísticas de ventas
- Pedidos recientes
- Accesos rápidos a funciones principales

### 📦 Gestión de Productos

#### Publicar Nuevo Producto

1. **En el panel, hacer clic en "Publicar Producto"**
2. **Completar formulario**:
   ```
   📝 Campos obligatorios:
   • Nombre del producto
   • Descripción detallada
   • Precio (formato: 0.00)
   • Stock disponible
   • Categoría
   • Imagen del producto
   ```

3. **Subir imagen**:
   - Formatos aceptados: JPG, PNG, GIF
   - Tamaño máximo: 5MB
   - Resolución recomendada: 800x600px

4. **Publicar**:
   - Hacer clic en "Publicar Producto"
   - El producto aparecerá inmediatamente en el catálogo

#### Editar Productos Existentes

1. **En "Mis Productos"**
2. **Hacer clic en "Editar"** junto al producto deseado
3. **Modificar campos necesarios**
4. **Guardar cambios**

#### Eliminar Productos

1. **En "Mis Productos"**
2. **Hacer clic en "Eliminar"**
3. **Confirmar eliminación**
   - ⚠️ **Advertencia**: Esta acción no se puede deshacer

### 📋 Gestión de Pedidos

#### Ver Pedidos Recibidos

1. **Ir a "Pedidos Recibidos"**
2. **Información disponible**:
   - Número de pedido
   - Cliente que compró
   - Productos vendidos
   - Estado actual
   - Fecha del pedido
   - Total de la venta

#### Actualizar Estado de Pedidos

1. **Hacer clic en "Ver Detalles"** del pedido
2. **Cambiar estado**:
   - PENDIENTE → CONFIRMADO (confirmar pago)
   - CONFIRMADO → ENVIADO (producto despachado)
   - ENVIADO → ENTREGADO (entrega confirmada)

### 📈 Historial de Compras

**Ver tus compras**:
- Ir a "Mis Compras"
- Ver historial completo
- Seguimiento de pedidos
- Descargar facturas

---

## Administración

### Acceso Administrativo

**Solo para usuarios con rol ADMINISTRADOR**

1. **Iniciar sesión** con cuenta de administrador
2. **Acceder a `/admin`**

### 👥 Gestión de Usuarios

#### Listar Usuarios

- Ver todos los usuarios registrados
- Información: nombre, email, rol, estado
- Filtros por rol y estado

#### Activar/Desactivar Usuarios

1. **En la lista de usuarios**
2. **Hacer clic en "Activar" o "Desactivar"**
3. **Confirmar acción**

**Efectos**:
- Usuario desactivado no puede iniciar sesión
- Sus productos se ocultan del catálogo
- Pedidos existentes no se afectan

#### Cambiar Roles

1. **Hacer clic en "Editar"** junto al usuario
2. **Seleccionar nuevo rol**:
   - USUARIO (comprador/vendedor)
   - ADMINISTRADOR (acceso completo)
3. **Guardar cambios**

### 📊 Estadísticas del Sistema

**Métricas disponibles**:
- Total de usuarios registrados
- Productos publicados
- Pedidos procesados
- Ingresos totales
- Usuarios activos

---

## Preguntas Frecuentes

### 🔐 Seguridad y Cuenta

**P: ¿Es seguro registrarse con Google?**
R: Sí, utilizamos OAuth2 de Google, que es un estándar de seguridad. No almacenamos tu contraseña de Google.

**P: ¿Cómo cambio mi contraseña?**
R: Ve a tu perfil → Configuración → Cambiar Contraseña. Necesitarás tu contraseña actual.

**P: ¿Qué hago si olvido mi contraseña?**
R: Usa la opción "¿Olvidaste tu contraseña?" en la página de login. Te enviaremos un enlace de recuperación.

### 🛒 Compras y Pagos

**P: ¿El carrito guarda mis productos si cierro el navegador?**
R: Sí, tu carrito se guarda automáticamente y estará disponible cuando regreses.

**P: ¿Qué métodos de pago aceptan?**
R: Actualmente aceptamos tarjetas de crédito y débito (Visa, MasterCard, American Express).

**P: ¿Puedo cancelar un pedido?**
R: Puedes cancelar pedidos en estado "PENDIENTE". Una vez confirmados, contacta al vendedor.

**P: ¿Cómo sigo mi pedido?**
R: Ve a "Mis Compras" en tu panel de control para ver el estado actualizado.

### 📦 Venta de Productos

**P: ¿Hay límite en la cantidad de productos que puedo publicar?**
R: No hay límite. Puedes publicar tantos productos como desees.

**P: ¿Puedo editar un producto después de publicarlo?**
R: Sí, puedes editar precio, descripción, stock e imagen en cualquier momento.

**P: ¿Cómo recibo el pago de mis ventas?**
R: Los pagos se procesan automáticamente. Contacta al administrador para configurar tu método de pago.



### 🔧 Problemas Técnicos

**P: La página no carga correctamente**
R: 
1. Actualiza la página (F5)
2. Limpia la caché del navegador
3. Verifica tu conexión a internet
4. Prueba con otro navegador

**P: No puedo subir imágenes**
R: Verifica que:
- El archivo sea JPG, PNG o GIF
- El tamaño sea menor a 5MB
- Tu conexión sea estable

**P: El sitio se ve mal en mi móvil**
R: Asegúrate de usar un navegador actualizado. El sitio es compatible con Chrome, Firefox, Safari y Edge.

### 📱 Compatibilidad

**Navegadores soportados**:
- ✅ Chrome 80+
- ✅ Firefox 75+
- ✅ Safari 13+
- ✅ Edge 80+

**Dispositivos**:
- ✅ Computadoras (Windows, Mac, Linux)
- ✅ Tablets (iPad, Android)
- ✅ Smartphones (iOS, Android)

---

## Soporte Técnico

### 📞 Contacto

**Email de Soporte**: soporte@dejatuhuella.com
**Horario de Atención**: Lunes a Viernes, 9:00 AM - 6:00 PM
**Tiempo de Respuesta**: 24-48 horas

### 🐛 Reportar Problemas

**Al reportar un problema, incluye**:
1. Descripción detallada del problema
2. Pasos para reproducir el error
3. Navegador y versión que usas
4. Capturas de pantalla (si aplica)
5. Tu email de usuario

### 💡 Sugerencias

¿Tienes ideas para mejorar la plataforma?
**Envíanos tus sugerencias a**: sugerencias@dejatuhuella.com

### 📚 Recursos Adicionales

- **Documentación Técnica**: `/docs`
- **Estado del Sistema**: Verifica si hay mantenimientos programados
- **Actualizaciones**: Síguenos para conocer nuevas funcionalidades

---

## Términos y Condiciones

### 📋 Uso de la Plataforma

- Los usuarios son responsables de la veracidad de la información que publican
- Está prohibido publicar contenido ofensivo o ilegal
- Los precios y disponibilidad son responsabilidad de cada vendedor
- La plataforma actúa como intermediario entre compradores y vendedores

### 🔒 Privacidad

- Protegemos tu información personal según las leyes de privacidad
- No compartimos datos con terceros sin tu consentimiento
- Puedes solicitar la eliminación de tu cuenta en cualquier momento

---

**Versión del Manual**: 1.0  
**Última Actualización**: Diciembre 2024  
**Plataforma**: DejaTuHuella v1.0

---

*¿Necesitas ayuda adicional? No dudes en contactarnos. ¡Estamos aquí para ayudarte a tener la mejor experiencia en DejaTuHuella!* 🚀