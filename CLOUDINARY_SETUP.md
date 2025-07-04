# Configuración de Cloudinary para DejaTuHuella

## Variables de Entorno Requeridas en Railway

Para que el almacenamiento de imágenes en la nube funcione correctamente, debes configurar las siguientes variables de entorno en Railway:

### 1. CLOUDINARY_CLOUD_NAME
- **Valor**: `ddvlkbhe0`
- **Descripción**: Nombre de tu instancia de Cloudinary

### 2. CLOUDINARY_API_KEY
- **Valor**: Tu API Key de Cloudinary (obtenida del dashboard)
- **Descripción**: Clave pública para autenticación

### 3. CLOUDINARY_API_SECRET
- **Valor**: Tu API Secret de Cloudinary (obtenida del dashboard)
- **Descripción**: Clave secreta para autenticación

## Pasos para Configurar en Railway

1. Ve a tu proyecto en Railway
2. Selecciona tu servicio
3. Ve a la pestaña "Variables"
4. Añade las tres variables mencionadas arriba
5. Haz deploy de los cambios

## Beneficios de Cloudinary

✅ **Persistencia**: Las imágenes no se borran al reiniciar la aplicación
✅ **Optimización automática**: Cloudinary optimiza las imágenes automáticamente
✅ **CDN global**: Entrega rápida de imágenes desde cualquier parte del mundo
✅ **Transformaciones**: Redimensionado y optimización automática
✅ **Escalabilidad**: Maneja grandes volúmenes de imágenes sin problemas

## Estructura de Carpetas en Cloudinary

Las imágenes se organizarán en:
- `dejatuhuella/productos/` - Imágenes de productos

## Verificación

Una vez configurado, las nuevas imágenes subidas se almacenarán en Cloudinary y tendrán URLs como:
`https://res.cloudinary.com/ddvlkbhe0/image/upload/v1234567890/dejatuhuella/productos/uuid.jpg`