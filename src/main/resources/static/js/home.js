// Función para agregar productos al carrito
        function agregarAlCarrito(productoId) {
            fetch('/api/carrito/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    productoId: productoId,
                    cantidad: 1
                })
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Actualizar el contador del carrito
                    document.querySelector('.cart-badge').textContent = data.cartCount;
                    showToastSuccess('Producto agregado al carrito');
                } else {
                    showToastError('Error al agregar al carrito: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showToastError('Error al agregar al carrito');
            });
        }

        // Función para actualizar los productos destacados
        function actualizarProductosDestacados() {
            console.log('Iniciando actualización de productos destacados...');
            fetch(window.location.pathname + '?ajax=true')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Error en la respuesta: ' + response.status);
                    }
                    return response.text();
                })
                .then(html => {
                    const parser = new DOMParser();
                    const doc = parser.parseFromString(html, 'text/html');
                    
                    // Buscar específicamente la sección de productos destacados
                    const nuevosProductos = doc.querySelector('.container.mb-5:last-of-type .row.g-4');
                    const productosActuales = document.querySelector('.container.mb-5:last-of-type .row.g-4');

                    if (nuevosProductos && productosActuales) {
                        productosActuales.innerHTML = nuevosProductos.innerHTML;
                        console.log('✅ Productos destacados actualizados exitosamente');
                        
                        // Mostrar notificación sutil
                        showToastInfo('Productos actualizados');
                    } else {
                        console.warn('⚠️ No se encontraron elementos para actualizar');
                    }
                })
                .catch(error => {
                    console.error('❌ Error al actualizar productos destacados:', error);
                });
        }

        // Inicializar cuando el DOM esté listo
        document.addEventListener('DOMContentLoaded', function() {
            console.log('🚀 Iniciando sistema de actualización automática de productos');
            
            // Actualizar productos destacados cada 2 minuto (120000 ms)
            const intervalo = setInterval(actualizarProductosDestacados, 120000);
            
            console.log('⏰ Actualización automática configurada cada 1 minuto');
        });