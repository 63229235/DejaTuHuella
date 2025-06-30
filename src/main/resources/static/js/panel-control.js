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
                // Eliminar la fila de la tabla dinámicamente
                const filaProducto = document.querySelector(`tr[data-producto-id="${id}"]`);
                if (filaProducto) {
                    filaProducto.remove();
                }
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
            return response.json();
        } else {
            return response.text().then(text => { throw new Error(text) });
        }
    })
    .then(productoActualizado => {
        // Actualizar el estado del producto en la interfaz
        const filaProducto = document.querySelector(`tr[data-producto-id="${id}"]`);
        if (filaProducto) {
            // Actualizar el badge de estado
            const estadoCell = filaProducto.querySelector('td:nth-child(5)');
            if (estadoCell) {
                estadoCell.innerHTML = productoActualizado.activo ? 
                    '<span class="badge bg-success">Activo</span>' : 
                    '<span class="badge bg-danger">Inactivo</span>';
            }
            
            // Actualizar el botón de cambio de estado
            const toggleButton = filaProducto.querySelector('button.btn-outline-warning');
            if (toggleButton) {
                toggleButton.setAttribute('onclick', `cambiarEstadoProducto(${id}, ${!productoActualizado.activo})`);
            }
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
            
            // En lugar de recargar la página, actualizar la tabla dinámicamente
            return response.json();
        } else {
            return response.text().then(text => { throw new Error(text) });
        }
    })
    .then(nuevoProducto => {
        console.log('Producto guardado:', nuevoProducto);
        // Actualizar la tabla de productos dinámicamente
        actualizarTablaProductos(nuevoProducto);
    })
    .catch(error => {
        console.error('Error al guardar el producto:', error);
        alert('Error al guardar el producto: ' + error);
    });
}

// Función para actualizar la tabla de productos dinámicamente
function actualizarTablaProductos(nuevoProducto) {
    // Si es una edición, actualizar la fila existente
    const filaExistente = document.querySelector(`tr[data-producto-id="${nuevoProducto.id}"]`);
    
    if (filaExistente) {
        // Actualizar la fila existente
        filaExistente.innerHTML = crearFilaProducto(nuevoProducto);
    } else {
        // Agregar una nueva fila a la tabla
        // Seleccionar específicamente la tabla en la pestaña de productos
        const tablaProductos = document.querySelector('#productos table tbody');
        if (tablaProductos) {
            const nuevaFila = document.createElement('tr');
            nuevaFila.setAttribute('data-producto-id', nuevoProducto.id);
            nuevaFila.innerHTML = crearFilaProducto(nuevoProducto);
            tablaProductos.prepend(nuevaFila); // Agregar al principio de la tabla
            console.log('Fila agregada a la tabla de productos');
        } else {
            console.error('No se encontró la tabla de productos');
        }
    }
}

// Función para crear el HTML de una fila de producto
function crearFilaProducto(producto) {
    const estadoBadge = producto.activo ? 
        '<span class="badge bg-success">Activo</span>' : 
        '<span class="badge bg-danger">Inactivo</span>';
    
    return `
        <td>
            <img src="${producto.imagenUrl}" alt="Producto" class="img-thumbnail" style="width: 50px;">
        </td>
        <td>${producto.nombre}</td>
        <td>S/ ${producto.precio}</td>
        <td>${producto.stock}</td>
        <td>
            ${estadoBadge}
        </td>
        <td>
            <button class="btn btn-sm btn-outline-primary" onclick="editarProducto(${producto.id})">
                <i class="fas fa-edit"></i>
            </button>
            <button class="btn btn-sm btn-outline-danger" onclick="eliminarProducto(${producto.id})">
                <i class="fas fa-trash"></i>
            </button>
            <button class="btn btn-sm btn-outline-warning" onclick="cambiarEstadoProducto(${producto.id}, ${!producto.activo})">
                <i class="fas fa-toggle-on"></i>
            </button>
        </td>
    `;
}

// Funciones para pedidos
function verDetallePedido(id) {
    fetch(`/api/pedidos/${id}`)
        .then(response => response.json())
        .then(pedido => {
            document.getElementById('pedidoId').textContent = pedido.id;
            document.getElementById('pedidoFecha').textContent = new Date(pedido.fechaPedido).toLocaleString();
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

// Funciones para gestión de productos (administrador)
function cargarTodosLosProductos() {
    const todosProductosTableBody = document.getElementById('todosProductosTableBody');
    if (todosProductosTableBody) {
        console.log('Cargando todos los productos...');
        fetch('/api/productos')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al cargar productos: ' + response.status);
                }
                return response.json();
            })
            .then(productos => {
                console.log('Productos cargados:', productos);
                todosProductosTableBody.innerHTML = '';
                productos.forEach(producto => {
                    console.log('Producto:', producto);
                    console.log('Usuario del producto:', producto.usuario);
                    
                    const row = document.createElement('tr');
                    row.setAttribute('data-producto-id', producto.id);
                    
                    // Formatear el correo del vendedor
                    const nombreVendedor = producto.usuario ? 
                        `${producto.usuario.email}` : 'Desconocido';
                    console.log('Email del vendedor:', nombreVendedor);
                    
                    // Formatear el nombre de la categoría
                    const nombreCategoria = producto.categoria ? producto.categoria.nombre : 'Sin categoría';
                    
                    // Crear el badge de estado
                    const estadoBadge = producto.activo ? 
                        '<span class="badge bg-success">Activo</span>' : 
                        '<span class="badge bg-danger">Inactivo</span>';
                    
                    row.innerHTML = `
                        <td>
                            <img src="${producto.imagenUrl}" alt="${producto.nombre}" class="img-thumbnail" style="width: 50px;">
                        </td>
                        <td>${producto.nombre}</td>
                        <td>${nombreVendedor}</td>
                        <td>${nombreCategoria}</td>
                        <td>S/ ${producto.precio}</td>
                        <td>${producto.stock}</td>
                        <td>${estadoBadge}</td>
                        <td>
                            <button class="btn btn-sm btn-outline-primary" onclick="editarProducto(${producto.id})">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="btn btn-sm btn-outline-danger" onclick="eliminarProductoAdmin(${producto.id})">
                                <i class="fas fa-trash"></i>
                            </button>
                            <button class="btn btn-sm btn-outline-warning" onclick="cambiarEstadoProducto(${producto.id}, ${!producto.activo})">
                                <i class="fas fa-toggle-on"></i>
                            </button>
                        </td>
                    `;
                    todosProductosTableBody.appendChild(row);
                });
            })
            .catch(error => {
                console.error('Error:', error);
                todosProductosTableBody.innerHTML = `<tr><td colspan="8" class="text-center text-danger">Error al cargar productos: ${error.message}</td></tr>`;
            });
    }
}

function eliminarProductoAdmin(id) {
    if (confirm('¿Estás seguro de que deseas eliminar este producto? Esta acción no se puede deshacer.')) {
        fetch(`/api/productos/${id}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            },
        })
        .then(response => {
            if (response.ok) {
                // Eliminar la fila de la tabla dinámicamente
                const filaProducto = document.querySelector(`tr[data-producto-id="${id}"]`);
                if (filaProducto) {
                    filaProducto.remove();
                }
                alert('Producto eliminado con éxito');
            } else {
                return response.text().then(text => { throw new Error(text) });
            }
        })
        .catch(error => alert('Error al eliminar el producto: ' + error));
    }
}

function filtrarProductosAdmin() {
    const filtro = document.getElementById('filtroProductos').value.toLowerCase();
    const todosProductosTableBody = document.getElementById('todosProductosTableBody');
    
    if (todosProductosTableBody) {
        const filas = todosProductosTableBody.querySelectorAll('tr');
        
        filas.forEach(fila => {
            const nombre = fila.querySelector('td:nth-child(2)').textContent.toLowerCase();
            const correoVendedor = fila.querySelector('td:nth-child(3)').textContent.toLowerCase();
            const categoria = fila.querySelector('td:nth-child(4)').textContent.toLowerCase();
            
            // Mostrar u ocultar la fila según si coincide con el filtro
            if (nombre.includes(filtro) || correoVendedor.includes(filtro) || categoria.includes(filtro)) {
                fila.style.display = '';
            } else {
                fila.style.display = 'none';
            }
        });
    }
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
    
    // Cargar todos los productos si es administrador
    const gestionProductosTab = document.getElementById('gestion-productos');
    if (gestionProductosTab) {
        console.log('Tab de gestión de productos encontrado, cargando productos...');
        cargarTodosLosProductos();

        // También cargar productos cuando se haga clic en la pestaña
        document.querySelector('a[data-bs-target="#gestion-productos"]')?.addEventListener('click', function() {
            console.log('Clic en pestaña de gestión de productos, recargando...');
            cargarTodosLosProductos();
        });
    }
});

// Evento para cargar datos específicos cuando se activa una pestaña
document.addEventListener('DOMContentLoaded', function() {
    // Detectar cambio de pestaña para cargar datos específicos
    const tabLinks = document.querySelectorAll('[data-bs-toggle="list"]');
    tabLinks.forEach(tabLink => {
        tabLink.addEventListener('shown.bs.tab', function(event) {
            const targetId = event.target.getAttribute('data-bs-target');
            if (targetId === '#usuarios') {
                cargarUsuarios();
            } else if (targetId === '#gestion-productos') {
                cargarTodosLosProductos();
            }
        });
    });

    // Cargar datos si la pestaña correspondiente está activa al cargar la página
    if (document.querySelector('[data-bs-target="#usuarios"].active')) {
        cargarUsuarios();
    }
    if (document.querySelector('[data-bs-target="#gestion-productos"].active')) {
        cargarTodosLosProductos();
    }
    
    // Configurar el evento para el filtro de productos
    const filtroProductos = document.getElementById('filtroProductos');
    if (filtroProductos) {
        filtroProductos.addEventListener('input', function() {
            filtrarProductosAdmin();
        });
    }
});
