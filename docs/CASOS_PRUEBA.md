# Casos de Prueba - DejaTuHuella

## Información General
- **Proyecto**: DejaTuHuella - Plataforma de Comercio Electrónico
- **Versión**: 1.0
- **Fecha**: Diciembre 2024

## Casos de Prueba Funcionales

### Módulo: Autenticación y Usuarios

| ID Prueba | CP-01 |
|-----------|-------|
| **Título** | Registro de usuario exitoso |
| **Objetivo** | Verificar que un usuario puede registrarse correctamente |
| **Precondiciones** | - Aplicación funcionando<br>- Email no registrado previamente |
| **Pasos a seguir** | 1. Navegar a /registro<br>2. Completar formulario con datos válidos<br>3. Hacer clic en "Registrarse" |
| **Datos de Prueba** | Nombre: Juan<br>Apellido: Pérez<br>Email: juan.perez@test.com<br>Contraseña: Test123! |
| **Resultado Esperado** | - Usuario creado exitosamente<br>- Redirección a página de login<br>- Mensaje de confirmación |
| **Estado** | ✅ Pasó |

| ID Prueba | CP-02 |
|-----------|-------|
| **Título** | Registro con email duplicado |
| **Objetivo** | Verificar que no se puede registrar con email existente |
| **Precondiciones** | - Email ya registrado en el sistema |
| **Pasos a seguir** | 1. Navegar a /registro<br>2. Usar email existente<br>3. Completar otros campos<br>4. Hacer clic en "Registrarse" |
| **Datos de Prueba** | Email: admin@dejatuhuella.com |
| **Resultado Esperado** | - Error: "El email ya está registrado"<br>- Usuario no creado |
| **Estado** | ✅ Pasó |

| ID Prueba | CP-03 |
|-----------|-------|
| **Título** | Autenticación OAuth2 con Google |
| **Objetivo** | Verificar login con cuenta de Google |
| **Precondiciones** | - Cuenta de Google válida |
| **Pasos a seguir** | 1. Ir a página de login<br>2. Hacer clic en "Iniciar con Google"<br>3. Autorizar en Google<br>4. Verificar redirección |
| **Resultado Esperado** | - Login exitoso<br>- Usuario creado/actualizado<br>- Redirección a página principal |
| **Estado** | ✅ Pasó |

| ID Prueba | CP-04 |
|-----------|-------|
| **Título** | Login exitoso |
| **Objetivo** | Verificar inicio de sesión con credenciales válidas |
| **Precondiciones** | - Usuario registrado en el sistema |
| **Pasos a seguir** | 1. Navegar a /login<br>2. Ingresar email y contraseña correctos<br>3. Hacer clic en "Iniciar Sesión" |
| **Datos de Prueba** | Email: usuario@test.com<br>Contraseña: password123 |
| **Resultado Esperado** | - Login exitoso<br>- Redirección a página principal<br>- Menú de usuario visible |
| **Estado** | ✅ Pasó |

| ID Prueba | CP-05 |
|-----------|-------|
| **Título** | Login con credenciales incorrectas |
| **Objetivo** | Verificar manejo de credenciales inválidas |
| **Precondiciones** | - Aplicación funcionando |
| **Pasos a seguir** | 1. Navegar a /login<br>2. Ingresar credenciales incorrectas<br>3. Hacer clic en "Iniciar Sesión" |
| **Datos de Prueba** | Email: wrong@test.com<br>Contraseña: wrongpass |
| **Resultado Esperado** | - Error: "Credenciales incorrectas"<br>- Permanecer en página de login |
| **Estado** | ✅ Pasó |

### Módulo: Gestión de Productos

| ID Prueba | CP-06 |
|-----------|-------|
| **Título** | Publicar producto exitosamente |
| **Objetivo** | Verificar que un usuario puede publicar un producto |
| **Precondiciones** | - Usuario autenticado<br>- Categorías disponibles |
| **Pasos a seguir** | 1. Ir a panel de control<br>2. Hacer clic en "Publicar Producto"<br>3. Completar formulario<br>4. Subir imagen<br>5. Hacer clic en "Publicar" |
| **Datos de Prueba** | Nombre: Laptop Gaming<br>Descripción: Laptop para gaming<br>Precio: 1500.00<br>Stock: 5<br>Categoría: Electrónicos |
| **Resultado Esperado** | - Producto creado exitosamente<br>- Visible en catálogo<br>- Mensaje de confirmación |
| **Estado** | ✅ Pasó |

| ID Prueba | CP-07 |
|-----------|-------|
| **Título** | Validación de campos obligatorios |
| **Objetivo** | Verificar validación de formulario de producto |
| **Precondiciones** | - Usuario autenticado |
| **Pasos a seguir** | 1. Ir a formulario de producto<br>2. Dejar campos obligatorios vacíos<br>3. Intentar publicar |
| **Resultado Esperado** | - Errores de validación mostrados<br>- Producto no creado |
| **Estado** | ✅ Pasó |

### Módulo: Carrito de Compras

| ID Prueba | CP-08 |
|-----------|-------|
| **Título** | Agregar producto al carrito |
| **Objetivo** | Verificar que se pueden agregar productos al carrito |
| **Precondiciones** | - Usuario autenticado<br>- Producto disponible |
| **Pasos a seguir** | 1. Navegar a detalle de producto<br>2. Hacer clic en "Agregar al Carrito"<br>3. Verificar contador de carrito |
| **Resultado Esperado** | - Producto agregado al carrito<br>- Contador actualizado<br>- Notificación de éxito |
| **Estado** | ✅ Pasó |

### Módulo: Procesamiento de Pedidos

| ID Prueba | CP-09 |
|-----------|-------|
| **Título** | Crear pedido desde carrito |
| **Objetivo** | Verificar creación de pedido exitosa |
| **Precondiciones** | - Usuario autenticado<br>- Productos en carrito |
| **Pasos a seguir** | 1. Ir al carrito<br>2. Hacer clic en "Proceder al Pago"<br>3. Completar datos de pago<br>4. Confirmar pedido |
| **Datos de Prueba** | Tarjeta: 4111111111111111<br>CVV: 123<br>Fecha: 12/25 |
| **Resultado Esperado** | - Pedido creado exitosamente<br>- Carrito vaciado<br>- Email de confirmación |
| **Estado** | ✅ Pasó |

| ID Prueba | CP-10 |
|-----------|-------|
| **Título** | Validación de datos de pago |
| **Objetivo** | Verificar validación de tarjeta de crédito |
| **Precondiciones** | - Productos en carrito |
| **Pasos a seguir** | 1. Proceder al pago<br>2. Ingresar datos de tarjeta inválidos<br>3. Intentar confirmar |
| **Datos de Prueba** | Tarjeta: 1234567890123456<br>CVV: 999 |
| **Resultado Esperado** | - Error de validación<br>- Pedido no creado |
| **Estado** | ✅ Pasó |



### Módulo: Administración

| ID Prueba | CP-11 |
|-----------|-------|
| **Título** | Gestión de usuarios por administrador |
| **Objetivo** | Verificar funciones administrativas |
| **Precondiciones** | - Usuario con rol ADMINISTRADOR |
| **Pasos a seguir** | 1. Acceder a panel admin<br>2. Ir a "Gestión de Usuarios"<br>3. Activar/desactivar usuario<br>4. Verificar cambios |
| **Resultado Esperado** | - Estado de usuario cambiado<br>- Cambios reflejados en sistema |
| **Estado** | ✅ Pasó |

| ID Prueba | CP-12 |
|-----------|-------|
| **Título** | Acceso restringido a funciones admin |
| **Objetivo** | Verificar que usuarios normales no accedan a admin |
| **Precondiciones** | - Usuario con rol USUARIO |
| **Pasos a seguir** | 1. Intentar acceder a /admin<br>2. Verificar respuesta |
| **Resultado Esperado** | - Acceso denegado (403)<br>- Redirección a página de error |
| **Estado** | ✅ Pasó |

## Casos de Prueba No Funcionales

### Rendimiento

| ID Prueba | CP-13 |
|-----------|-------|
| **Título** | Tiempo de carga de página principal |
| **Objetivo** | Verificar que la página principal carga en menos de 3 segundos |
| **Herramientas** | Google PageSpeed Insights, DevTools |
| **Pasos a seguir** | 1. Abrir DevTools<br>2. Navegar a página principal<br>3. Medir tiempo de carga |
| **Resultado Esperado** | - Tiempo de carga < 3 segundos<br>- Todos los recursos cargados |
| **Estado** | ✅ Pasó (2.1 segundos) |

### Seguridad

| ID Prueba | CP-14 |
|-----------|-------|
| **Título** | Encriptación de contraseñas |
| **Objetivo** | Verificar que las contraseñas se almacenan encriptadas |
| **Pasos a seguir** | 1. Registrar usuario<br>2. Verificar en base de datos<br>3. Comprobar que no es texto plano |
| **Resultado Esperado** | - Contraseña encriptada con BCrypt<br>- No visible en texto plano |
| **Estado** | ✅ Pasó |

### Usabilidad

| ID Prueba | CP-15 |
|-----------|-------|
| **Título** | Diseño responsivo |
| **Objetivo** | Verificar adaptación a dispositivos móviles |
| **Pasos a seguir** | 1. Abrir en dispositivo móvil<br>2. Navegar por diferentes páginas<br>3. Verificar usabilidad |
| **Resultado Esperado** | - Diseño adaptado correctamente<br>- Elementos accesibles<br>- Navegación fluida |
| **Estado** | ✅ Pasó |

## Resumen de Resultados

### Estadísticas Generales
- **Total de Casos de Prueba**: 15
- **Casos Exitosos**: 15
- **Casos Fallidos**: 0
- **Porcentaje de Éxito**: 100%

### Cobertura por Módulos
| Módulo | Casos Ejecutados | Casos Exitosos | % Éxito |
|--------|------------------|----------------|---------|
| Autenticación | 5 | 5 | 100% |
| Productos | 2 | 2 | 100% |
| Carrito | 1 | 1 | 100% |
| Pedidos | 2 | 2 | 100% |
| Administración | 2 | 2 | 100% |
| No Funcionales | 3 | 3 | 100% |

### Observaciones
- Todos los casos de prueba han pasado exitosamente
- El sistema cumple con los requerimientos funcionales y no funcionales
- La aplicación está lista para producción
- Se recomienda ejecutar pruebas de carga adicionales

### Recomendaciones
1. Implementar pruebas automatizadas para regresión
2. Agregar monitoreo de rendimiento en producción
3. Realizar pruebas de penetración de seguridad
4. Documentar casos de prueba adicionales para nuevas funcionalidades

---

**Ejecutado por**: Equipo de QA
**Fecha de Ejecución**: Diciembre 2024
**Entorno de Prueba**: Desarrollo Local
**Versión Probada**: 1.0