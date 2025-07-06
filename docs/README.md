# Documentación del Proyecto DejaTuHuella

## 📋 Índice de Documentación

Esta carpeta contiene toda la documentación técnica y de usuario del proyecto **DejaTuHuella**, una plataforma de comercio electrónico desarrollada con Spring Boot.

---

## 📁 Estructura de Documentación

### 📊 1. Gestión del Proyecto

#### [📋 Gestión del Proyecto](./GESTION_PROYECTO.md)
**Descripción**: Documento principal de gestión que incluye:
- Objetivos del proyecto
- Alcance y limitaciones
- Cronograma de desarrollo
- Requerimientos funcionales y no funcionales
- Casos de uso detallados
- Matriz de trazabilidad

**Contenido clave**:
- ✅ 15 Requerimientos funcionales
- ✅ 8 Requerimientos no funcionales
- ✅ 8 Casos de uso principales
- ✅ Matriz de trazabilidad completa

---

### 🧪 2. Documentación de Pruebas

#### [🔬 Casos de Prueba](./CASOS_PRUEBA.md)
**Descripción**: Documentación completa de testing que incluye:
- Casos de prueba funcionales
- Casos de prueba no funcionales
- Resultados de ejecución
- Estadísticas de cobertura

**Estadísticas**:
- ✅ **16 casos de prueba** ejecutados
- ✅ **100% de éxito** en todas las pruebas
- ✅ Cobertura completa de módulos
- ✅ Pruebas de rendimiento, seguridad y usabilidad

**Módulos probados**:
- Autenticación y usuarios (5 casos)
- Gestión de productos (2 casos)
- Carrito de compras (1 caso)
- Procesamiento de pedidos (2 casos)
- Sistema de reseñas (1 caso)
- Administración (2 casos)
- Pruebas no funcionales (3 casos)

---

### 📖 3. Manual de Usuario

#### [👤 Manual de Usuario](./MANUAL_USUARIO.md)
**Descripción**: Guía completa para usuarios finales que incluye:
- Introducción a la plataforma
- Instrucciones paso a paso
- Funciones principales
- Panel de control
- Administración
- Preguntas frecuentes
- Soporte técnico

**Secciones principales**:
- 🚀 **Cómo Empezar**: Registro, login, OAuth2
- 🛍️ **Funciones Principales**: Comprar, vender, gestionar
- 📊 **Panel de Control**: Gestión de productos y pedidos
- 👥 **Administración**: Funciones administrativas
- ❓ **FAQ**: Preguntas frecuentes y soluciones

---

### 📊 4. Diagramas Técnicos

#### [🗄️ Modelo de Datos](./diagrams/modelo_datos.svg)
**Descripción**: Diagrama entidad-relación (ER) que muestra:
- Estructura de la base de datos
- Relaciones entre entidades
- Claves primarias y foráneas
- Atributos de cada tabla

**Entidades incluidas**:
- 👤 Usuario
- 📦 Producto
- 🏷️ Categoria
- 🛒 Carrito
- 📋 CarritoItem
- 📄 Pedido
- 📝 DetallePedido

#### [🏗️ Diagrama de Componentes](./diagrams/diagrama_componentes.svg)
**Descripción**: Arquitectura del sistema mostrando:
- Capas de la aplicación
- Componentes principales
- Interacciones entre módulos
- Tecnologías utilizadas

**Capas arquitectónicas**:
- 🎨 **Presentación**: Thymeleaf, JavaScript, CSS
- 🎮 **Controladores**: Spring MVC Controllers
- 🔧 **Servicios**: Lógica de negocio
- 💾 **Repositorios**: Acceso a datos JPA
- 🗄️ **Datos**: MySQL Database

#### [🌐 Diagrama de Despliegue](./diagrams/diagrama_despliegue.svg)
**Descripción**: Infraestructura y despliegue del sistema:
- Arquitectura de servidores
- Dispositivos cliente
- Servicios externos
- Puertos y protocolos

**Componentes de infraestructura**:
- 💻 **Clientes**: Desktop, móvil, tablet
- ⚖️ **Load Balancer**: Nginx/Apache
- 🖥️ **Servidor de Aplicaciones**: Spring Boot + Tomcat
- 🗄️ **Base de Datos**: MySQL Server
- ☁️ **Servicios Externos**: Google OAuth2, Pagos, Email

---

## 🛠️ Tecnologías Documentadas

### Backend
- ☕ **Java 17+**
- 🍃 **Spring Boot 3.x**
- 🔒 **Spring Security**
- 🗄️ **Spring Data JPA**
- 🐬 **MySQL 8.0+**
- 🔐 **OAuth2 (Google)**

### Frontend
- 🎨 **Thymeleaf**
- 📱 **Bootstrap 5**
- ⚡ **JavaScript/jQuery**
- 🎯 **AJAX**
- 🎨 **CSS3**

### Herramientas
- 🔨 **Maven**
- 🐙 **Git**
- 🧪 **JUnit**
- 📊 **Spring Boot Actuator**

---

## 📈 Métricas del Proyecto

### Líneas de Código
- **Backend Java**: ~3,000 líneas
- **Frontend (HTML/JS/CSS)**: ~2,000 líneas
- **Configuración**: ~500 líneas
- **Total**: ~5,500 líneas

### Funcionalidades
- ✅ **8 entidades** de base de datos
- ✅ **6 controladores** principales
- ✅ **6 servicios** de negocio
- ✅ **15 páginas web** funcionales
- ✅ **20+ endpoints** REST

### Cobertura de Pruebas
- ✅ **100% módulos** probados
- ✅ **16 casos de prueba** documentados
- ✅ **Pruebas funcionales** y no funcionales
- ✅ **Validación completa** de requerimientos

---

## 🎯 Casos de Uso Principales

1. **👤 Gestión de Usuarios**
   - Registro con email o Google OAuth2
   - Autenticación segura
   - Gestión de perfiles

2. **📦 Gestión de Productos**
   - Publicación de productos
   - Catálogo con búsqueda y filtros
   - Gestión de inventario

3. **🛒 Carrito de Compras**
   - Carrito persistente
   - Gestión de cantidades
   - Cálculo automático de totales

4. **💳 Procesamiento de Pedidos**
   - Checkout seguro
   - Validación de pagos
   - Seguimiento de estados

5. **👨‍💼 Panel de Administración**
   - Gestión de usuarios
   - Moderación de contenido
   - Estadísticas del sistema

---

## 📋 Requerimientos del Sistema

### Desarrollo
- **Java**: 17 o superior
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **IDE**: IntelliJ IDEA / Eclipse
- **Git**: Para control de versiones

### Producción
- **Servidor**: Linux/Windows Server
- **RAM**: 4GB mínimo, 8GB recomendado
- **Almacenamiento**: 20GB mínimo
- **Base de datos**: MySQL 8.0+ o compatible
- **Java Runtime**: JRE 17+

### Cliente
- **Navegadores**: Chrome 80+, Firefox 75+, Safari 13+, Edge 80+
- **JavaScript**: Habilitado
- **Resolución**: 320px+ (móvil) a 1920px+ (desktop)
- **Conexión**: Internet estable

---

## 🔗 Enlaces Útiles

- **[Código Fuente](../README.md)**: Documentación técnica principal
- **[Configuración](../src/main/resources/application.properties)**: Configuración de la aplicación
- **[Base de Datos](../database/)**: Scripts SQL y esquemas
- **[Tests](../src/test/)**: Código de pruebas unitarias

---

## 🌐 Acceso al Sistema

- **URL de producción**: `https://dejatuhuella-production.up.railway.app/`
- **URL de desarrollo**: `http://localhost:8080`

---

## 📞 Contacto y Soporte

**Equipo de Desarrollo**: DejaTuHuella Team  
**Email**: soporte@dejatuhuella.com  
**Documentación**: Actualizada en Diciembre 2024  
**Versión**: 1.0

---

## 📄 Licencia

Este proyecto y su documentación están bajo la licencia MIT. Ver el archivo [LICENSE](../LICENSE) para más detalles.

---

*Esta documentación ha sido generada automáticamente y se mantiene actualizada con cada versión del proyecto. Para sugerencias o correcciones, contacta al equipo de desarrollo.*

**Última actualización**: Diciembre 2024  
**Versión de la documentación**: 1.0  
**Estado**: ✅ Completa y actualizada