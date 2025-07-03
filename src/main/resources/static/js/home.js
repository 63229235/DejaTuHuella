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
                    // Usar SweetAlert2 para mostrar mensaje de éxito
                    mostrarToast('Producto agregado al carrito', 'success');
                } else {
                    // Usar SweetAlert2 para mostrar mensaje de error
                    mostrarError(data.message || 'No se pudo agregar el producto', 'Error al agregar al carrito');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                // Usar SweetAlert2 para mostrar mensaje de error
                mostrarError('No se pudo procesar la solicitud', 'Error al agregar al carrito');
            });
        }

        // Función para actualizar los productos destacados
        function actualizarProductosDestacados() {
            fetch(window.location.pathname)
                .then(response => response.text())
                .then(html => {
                    const parser = new DOMParser();
                    const doc = parser.parseFromString(html, 'text/html');
                    const nuevosProductos = doc.querySelector('.row.g-4');

                    if (nuevosProductos) {
                        // Actualizar solo la sección de productos destacados
                        const productosActuales = document.querySelector('.container.mb-5:nth-of-type(2) .row.g-4');
                        if (productosActuales) {
                            productosActuales.innerHTML = nuevosProductos.innerHTML;
                            console.log('Productos destacados actualizados');
                        }
                    }
                })
                .catch(error => {
                    console.error('Error al actualizar productos destacados:', error);
                });
        }

        // Actualizar productos destacados cada 2.5 minutos (900000 ms)
        setInterval(actualizarProductosDestacados, 150000);