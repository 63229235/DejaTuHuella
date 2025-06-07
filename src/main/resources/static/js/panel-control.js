function editarProducto(id) {
        // Cargar datos del producto y abrir modal
        fetch('/api/productos/' + id)
            .then(response => response.json())
            .then(producto => {
                document.getElementById('productoId').value = producto.id;
                document.getElementById('nombre').value = producto.nombre;
                document.getElementById('descripcion').value = producto.descripcion;
                document.getElementById('precio').value = producto.precio;
                document.getElementById('stock').value = producto.stock;
                document.getElementById('categoria').value = producto.categoria.id;

                document.querySelector('.modal-title').textContent = 'Editar Producto';
                var modal = new bootstrap.Modal(document.getElementById('agregarProductoModal'));
                modal.show();
            });
    }

    function eliminarProducto(id) {
        if (confirm('¿Estás seguro de que deseas eliminar este producto?')) {
            fetch('/api/productos/' + id, {
                method: 'DELETE'
            })
            .then(response => {
                if (response.ok) {
                    location.reload();
                } else {
                    alert('Error al eliminar el producto');
                }
            });
        }
    }

    function cambiarEstadoProducto(id, nuevoEstado) {
        fetch(`/api/productos/${id}/estado?activo=${nuevoEstado}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (response.ok) {
                location.reload();
            } else {
                alert('Error al cambiar el estado del producto');
            }
        });
    }

    function guardarProducto() {
        const formData = new FormData();
        const productoId = document.getElementById('productoId').value;

        formData.append('nombre', document.getElementById('nombre').value);
        formData.append('descripcion', document.getElementById('descripcion').value);
        formData.append('precio', document.getElementById('precio').value);
        formData.append('stock', document.getElementById('stock').value);
        formData.append('categoriaId', document.getElementById('categoria').value);
        // Añadir el vendedorId - Puedes obtener este valor del usuario actual
        formData.append('vendedorId', obtenerVendedorIdActual()); // Implementa esta función o usa un valor fijo

        const imagenInput = document.getElementById('imagen');
        if (imagenInput.files.length > 0) {
            formData.append('imagen', imagenInput.files[0]);
            // Ya no necesitamos establecer imagenUrl aquí, el servidor lo manejará
        }

        const url = productoId ? `/api/productos/${productoId}` : '/api/productos';
        const method = productoId ? 'PUT' : 'POST';

        fetch(url, {
            method: method,
            body: formData
        })
        .then(response => {
            if (response.ok) {
                location.reload();
            } else {
                alert('Error al guardar el producto');
            }
        });
    }

    // Función para obtener el ID del vendedor actual
    function obtenerVendedorIdActual() {
        // Puedes implementar esto de varias formas:
        // 1. Obtener el ID del vendedor de una variable global establecida en el servidor
        // 2. Hacer una petición al servidor para obtener el ID del usuario actual
        // 3. Almacenar el ID en un campo oculto en el HTML

        // Por ahora, retornamos un valor fijo (debes cambiarlo por el ID real)
        return 1; // Reemplaza con el ID real del vendedor
    }

    function verDetallePedido(id) {
        // Abrir modal con detalles del pedido
        fetch(`/api/pedidos/${id}`)
            .then(response => response.json())
            .then(pedido => {
                // Mostrar información del pedido
                document.getElementById('pedidoId').textContent = pedido.id;
                document.getElementById('pedidoFecha').textContent = new Date(pedido.fechaCreacion).toLocaleString();
                document.getElementById('pedidoTotal').textContent = 'S/ ' + pedido.total;

                // Mostrar estado del pedido
                let estadoTexto = '';
                switch(pedido.estado) {
                    case 'PENDIENTE': estadoTexto = '<span class="badge bg-warning">Pendiente</span>'; break;
                    case 'PAGADO': estadoTexto = '<span class="badge bg-info">Pagado</span>'; break;
                    case 'ENVIADO': estadoTexto = '<span class="badge bg-primary">Enviado</span>'; break;
                    case 'ENTREGADO': estadoTexto = '<span class="badge bg-success">Entregado</span>'; break;
                    case 'CANCELADO': estadoTexto = '<span class="badge bg-danger">Cancelado</span>'; break;
                }
                document.getElementById('pedidoEstado').innerHTML = estadoTexto;

                // Limpiar y llenar la tabla de detalles
                const detallesBody = document.getElementById('detallesPedidoBody');
                detallesBody.innerHTML = '';

                pedido.detalles.forEach(detalle => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td><img src="${detalle.producto.imagenUrl}" alt="Producto" class="img-thumbnail" style="width: 50px;"></td>
                        <td>${detalle.producto.nombre}</td>
                        <td>S/ ${detalle.precioUnitario}</td>
                        <td>${detalle.cantidad}</td>
                        <td>S/ ${detalle.subtotal}</td>
                    `;
                    detallesBody.appendChild(row);
                });

                // Mostrar el modal
                var modal = new bootstrap.Modal(document.getElementById('detallePedidoModal'));
                modal.show();
            });
    }

    function actualizarEstadoPedido(id, nuevoEstado) {
        fetch(`/api/pedidos/${id}/estado?estado=${nuevoEstado}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (response.ok) {
                location.reload();
            } else {
                alert('Error al actualizar el estado del pedido');
            }
        });
    }