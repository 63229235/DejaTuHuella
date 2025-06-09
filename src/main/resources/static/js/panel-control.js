// Funciones para productos
function editarProducto(id) {
    fetch(`/api/productos/${id}`)
        .then(response => response.json())
        .then(producto => {
            document.getElementById('productoId').value = producto.id;
            document.getElementById('nombre').value = producto.nombre;
            document.getElementById('descripcion').value = producto.descripcion;
            document.getElementById('precio').value = producto.precio;
            document.getElementById('stock').value = producto.stock;
            document.getElementById('categoria').value = producto.categoria ? producto.categoria.id : '';
            
            // Cambiar título del modal
            document.querySelector('#agregarProductoModal .modal-title').textContent = 'Editar Producto';
            
            // Mostrar modal
            new bootstrap.Modal(document.getElementById('agregarProductoModal')).show();
        })
        .catch(error => console.error('Error:', error));
}

function eliminarProducto(id) {
    if (confirm('¿Estás seguro de que deseas eliminar este producto?')) {
        fetch(`/api/productos/${id}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            },
        })
        .then(response => {
            if (response.ok) {
                window.location.reload();
            } else {
                return response.text().then(text => { throw new Error(text) });
            }
        })
        .catch(error => alert('Error al eliminar el producto: ' + error));
    }
}

function cambiarEstadoProducto(id, nuevoEstado) {
    fetch(`/api/productos/${id}/estado`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ activo: nuevoEstado })
    })
    .then(response => {
        if (response.ok) {
            window.location.reload();
        } else {
            return response.text().then(text => { throw new Error(text) });
        }
    })
    .catch(error => alert('Error al cambiar el estado del producto: ' + error));
}

function guardarProducto() {
    const productoId = document.getElementById('productoId').value;
    const formData = new FormData();
    
    formData.append('nombre', document.getElementById('nombre').value);
    formData.append('descripcion', document.getElementById('descripcion').value);
    formData.append('precio', document.getElementById('precio').value);
    formData.append('stock', document.getElementById('stock').value);
    formData.append('categoriaId', document.getElementById('categoria').value);
    
    const imagenInput = document.getElementById('imagen');
    if (imagenInput.files.length > 0) {
        formData.append('imagen', imagenInput.files[0]);
    }
    
    const url = productoId ? `/api/productos/${productoId}` : '/api/productos';
    const method = productoId ? 'PUT' : 'POST';
    
    fetch(url, {
        method: method,
        body: formData
    })
    .then(response => {
        if (response.ok) {
            // Cerrar el modal después de guardar
            const modal = bootstrap.Modal.getInstance(document.getElementById('agregarProductoModal'));
            if (modal) {
                modal.hide();
            }
            // Recargar la página para mostrar los cambios
            window.location.reload();
        } else {
            return response.text().then(text => { throw new Error(text) });
        }
    })
    .catch(error => {
        console.error('Error al guardar el producto:', error);
        alert('Error al guardar el producto: ' + error);
    });
}

// Funciones para pedidos
function verDetallePedido(id) {
    fetch(`/api/pedidos/${id}`)
        .then(response => response.json())
        .then(pedido => {
            document.getElementById('pedidoId').textContent = pedido.id;
            document.getElementById('pedidoFecha').textContent = new Date(pedido.fechaCreacion).toLocaleString();
            document.getElementById('pedidoTotal').textContent = `$${pedido.total}`;
            
            const estadoElement = document.getElementById('pedidoEstado');
            estadoElement.textContent = pedido.estado;
            estadoElement.className = 'badge';
            
            switch(pedido.estado) {
                case 'PENDIENTE':
                    estadoElement.classList.add('bg-warning');
                    break;
                case 'PAGADO':
                    estadoElement.classList.add('bg-info');
                    break;
                case 'ENVIADO':
                    estadoElement.classList.add('bg-primary');
                    break;
                case 'ENTREGADO':
                    estadoElement.classList.add('bg-success');
                    break;
                case 'CANCELADO':
                    estadoElement.classList.add('bg-danger');
                    break;
            }
            
            const detallesBody = document.getElementById('detallesPedidoBody');
            detallesBody.innerHTML = '';
            
            pedido.detalles.forEach(detalle => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td><img src="${detalle.producto.imagenUrl}" alt="${detalle.producto.nombre}" class="img-thumbnail" style="width: 50px;"></td>
                    <td>${detalle.producto.nombre}</td>
                    <td>$${detalle.precioUnitarioAlComprar}</td>
                    <td>${detalle.cantidad}</td>
                    <td>$${(detalle.precioUnitarioAlComprar * detalle.cantidad).toFixed(2)}</td>
                `;
                detallesBody.appendChild(row);
            });
            
            new bootstrap.Modal(document.getElementById('detallePedidoModal')).show();
        })
        .catch(error => console.error('Error:', error));
}

function actualizarEstadoPedido(id, estado) {
    fetch(`/api/pedidos/${id}/estado?estado=${estado}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => {
        if (response.ok) {
            window.location.reload();
        } else {
            return response.text().then(text => { throw new Error(text) });
        }
    })
    .catch(error => alert('Error al actualizar el estado del pedido: ' + error));
}

// Funciones para categorías
function editarCategoria(id) {
    fetch(`/api/categorias/${id}`)
        .then(response => response.json())
        .then(categoria => {
            document.getElementById('categoriaId').value = categoria.id;
            document.getElementById('nombreCategoria').value = categoria.nombre;
            document.getElementById('descripcionCategoria').value = categoria.descripcion;
            
            // Cambiar título del modal
            document.querySelector('#agregarCategoriaModal .modal-title').textContent = 'Editar Categoría';
            
            // Mostrar modal
            new bootstrap.Modal(document.getElementById('agregarCategoriaModal')).show();
        })
        .catch(error => console.error('Error:', error));
}

function eliminarCategoria(id) {
    if (confirm('¿Estás seguro de que deseas eliminar esta categoría?')) {
        fetch(`/api/categorias/${id}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            },
        })
        .then(response => {
            if (response.ok) {
                window.location.reload();
            } else {
                return response.text().then(text => { throw new Error(text) });
            }
        })
        .catch(error => alert('Error al eliminar la categoría: ' + error));
    }
}

function guardarCategoria() {
    const categoriaId = document.getElementById('categoriaId').value;
    const categoria = {
        nombre: document.getElementById('nombreCategoria').value,
        descripcion: document.getElementById('descripcionCategoria').value
    };
    
    const url = categoriaId ? `/api/categorias/${categoriaId}` : '/api/categorias';
    const method = categoriaId ? 'PUT' : 'POST';
    
    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(categoria)
    })
    .then(response => {
        if (response.ok) {
            window.location.reload();
        } else {
            return response.text().then(text => { throw new Error(text) });
        }
    })
    .catch(error => alert('Error al guardar la categoría: ' + error));
}

// Función para actualizar perfil
function actualizarPerfil() {
    const usuario = {
        nombre: document.getElementById('nombrePerfil').value,
        apellido: document.getElementById('apellidoPerfil').value,
        telefono: document.getElementById('telefonoPerfil').value,
        direccion: document.getElementById('direccionPerfil').value
    };
    
    fetch('/api/usuarios/perfil', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(usuario)
    })
    .then(response => {
        if (response.ok) {
            alert('Perfil actualizado con éxito');
        } else {
            return response.text().then(text => { throw new Error(text) });
        }
    })
    .catch(error => alert('Error al actualizar el perfil: ' + error));
}

// Cargar usuarios para administradores
function cargarUsuarios() {
    const usuariosTableBody = document.getElementById('usuariosTableBody');
    if (usuariosTableBody) {
        console.log('Cargando usuarios...');
        fetch('/api/usuarios')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al cargar usuarios: ' + response.status);
                }
                return response.json();
            })
            .then(usuarios => {
                usuariosTableBody.innerHTML = '';
                usuarios.forEach(usuario => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${usuario.id}</td>
                        <td>${usuario.nombre} ${usuario.apellido}</td>
                        <td>${usuario.email}</td>
                        <td>${usuario.rol}</td>
                        <td>
                            <button class="btn btn-sm ${usuario.activo ? 'btn-danger' : 'btn-success'}"
                                    onclick="cambiarEstadoUsuario(${usuario.id}, ${!usuario.activo})">
                                ${usuario.activo ? 'Desactivar' : 'Activar'}
                            </button>
                            <button class="btn btn-sm btn-danger ms-2"
                                    onclick="eliminarUsuario(${usuario.id})">
                                <i class="fas fa-trash"></i> Eliminar
                            </button>
                        </td>
                    `;
                    usuariosTableBody.appendChild(row);
                });
            })
            .catch(error => {
                console.error('Error:', error);
                usuariosTableBody.innerHTML = `<tr><td colspan="5" class="text-center text-danger">Error al cargar usuarios: ${error.message}</td></tr>`;
            });
    }
}function cargarUsuarios() {
     const usuariosTableBody = document.getElementById('usuariosTableBody');
     if (usuariosTableBody) {
         console.log('Cargando usuarios...');
         fetch('/api/usuarios')
             .then(response => {
                 if (!response.ok) {
                     throw new Error('Error al cargar usuarios: ' + response.status);
                 }
                 return response.json();
             })
             .then(usuarios => {
                 usuariosTableBody.innerHTML = '';
                 usuarios.forEach(usuario => {
                     const row = document.createElement('tr');
                     row.innerHTML = `
                         <td>${usuario.id}</td>
                         <td>${usuario.nombre} ${usuario.apellido}</td>
                         <td>${usuario.email}</td>
                         <td>${usuario.rol}</td>
                         <td>
                             <button class="btn btn-sm ${usuario.activo ? 'btn-danger' : 'btn-success'}"
                                     onclick="cambiarEstadoUsuario(${usuario.id}, ${!usuario.activo})">
                                 ${usuario.activo ? 'Desactivar' : 'Activar'}
                             </button>
                             <button class="btn btn-sm btn-danger ms-2"
                                     onclick="eliminarUsuario(${usuario.id})">
                                 <i class="fas fa-trash"></i> Eliminar
                             </button>
                         </td>
                     `;
                     usuariosTableBody.appendChild(row);
                 });
             })
             .catch(error => {
                 console.error('Error:', error);
                 usuariosTableBody.innerHTML = `<tr><td colspan="5" class="text-center text-danger">Error al cargar usuarios: ${error.message}</td></tr>`;
             });
     }
 }

 function eliminarUsuario(id) {
     if (confirm('¿Estás seguro de que deseas eliminar este usuario? Esta acción no se puede deshacer.')) {
         fetch(`/api/usuarios/${id}`, {
             method: 'DELETE',
             headers: {
                 'Content-Type': 'application/json',
             },
         })
         .then(response => {
             if (response.ok) {
                 cargarUsuarios(); // Recargar la lista de usuarios
             } else {
                 return response.text().then(text => { throw new Error(text) });
             }
         })
         .catch(error => alert('Error al eliminar el usuario: ' + error));
     }
 }

function cambiarEstadoUsuario(id, nuevoEstado) {
    fetch(`/api/usuarios/${id}/estado`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ activo: nuevoEstado })
    })
    .then(response => {
        if (response.ok) {
            cargarUsuarios();
        } else {
            return response.text().then(text => { throw new Error(text) });
        }
    })
    .catch(error => alert('Error al cambiar el estado del usuario: ' + error));
}

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    // Resetear formulario al abrir modal de producto
    document.getElementById('agregarProductoModal')?.addEventListener('show.bs.modal', function (event) {
        if (!event.relatedTarget.classList.contains('btn-outline-primary')) {
            document.getElementById('productoForm').reset();
            document.getElementById('productoId').value = '';
            document.querySelector('#agregarProductoModal .modal-title').textContent = 'Agregar Nuevo Producto';
        }
    });
    
    // Resetear formulario al abrir modal de categoría
    document.getElementById('agregarCategoriaModal')?.addEventListener('show.bs.modal', function (event) {
        if (!event.relatedTarget.classList.contains('btn-outline-primary')) {
            document.getElementById('categoriaForm').reset();
            document.getElementById('categoriaId').value = '';
            document.querySelector('#agregarCategoriaModal .modal-title').textContent = 'Agregar Nueva Categoría';
        }
    });
    
    // Cargar usuarios si es administrador
        const usuariosTab = document.getElementById('usuarios');
        if (usuariosTab) {
            console.log('Tab de usuarios encontrado, cargando usuarios...');
            cargarUsuarios();

            // También cargar usuarios cuando se haga clic en la pestaña
            document.querySelector('a[data-bs-target="#usuarios"]')?.addEventListener('click', function() {
                console.log('Clic en pestaña de usuarios, recargando...');
                cargarUsuarios();
            });
        }
    });

    // Evento para cargar usuarios cuando se activa la pestaña
    document.addEventListener('DOMContentLoaded', function() {
        // Detectar cambio de pestaña para cargar datos específicos
        const tabLinks = document.querySelectorAll('[data-bs-toggle="list"]');
        tabLinks.forEach(tabLink => {
            tabLink.addEventListener('shown.bs.tab', function(event) {
                const targetId = event.target.getAttribute('data-bs-target');
                if (targetId === '#usuarios') {
                    cargarUsuarios();
                }
            });
        });

        // Cargar usuarios si la pestaña está activa al cargar la página
        if (document.querySelector('[data-bs-target="#usuarios"].active')) {
            cargarUsuarios();
        }
    });