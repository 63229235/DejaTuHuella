/**
 * pedidos.js - Funcionalidades para las páginas de mis-compras y mis-ventas
 */

document.addEventListener('DOMContentLoaded', function() {
    // Inicializar tooltips de Bootstrap
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Añadir animación a los badges de estado
    const estadoBadges = document.querySelectorAll('.estado-badge');
    estadoBadges.forEach(badge => {
        badge.addEventListener('mouseenter', function() {
            this.style.transform = 'scale(1.1)';
        });
        badge.addEventListener('mouseleave', function() {
            this.style.transform = 'scale(1)';
        });
    });

    // Filtrado de pedidos/ventas por estado
    setupFiltroEstados();

    // Animación para mensajes de alerta
    animateAlerts();
});

/**
 * Configura el filtrado de pedidos/ventas por estado
 */
function setupFiltroEstados() {
    // Verificar si estamos en la página de pedidos o ventas
    const isPedidosPage = window.location.pathname.includes('mis-compras');
    const isVentasPage = window.location.pathname.includes('mis-ventas');
    
    if (!isPedidosPage && !isVentasPage) return;
    
    // Añadir botones de filtro si no existen
    const infoCard = document.querySelector('.info-card .card-body');
    if (!infoCard) return;
    
    if (!document.getElementById('filtro-estados')) {
        const filtroHTML = `
            <div id="filtro-estados" class="mt-3">
                <p><i class="fas fa-filter me-2"></i>Filtrar por estado:</p>
                <div class="btn-group" role="group">
                    <button type="button" class="btn btn-sm btn-outline-secondary active" data-estado="todos">Todos</button>
                    <button type="button" class="btn btn-sm btn-outline-warning" data-estado="PENDIENTE">Pendientes</button>
                    <button type="button" class="btn btn-sm btn-outline-info" data-estado="PAGADO">Pagados</button>
                    <button type="button" class="btn btn-sm btn-outline-primary" data-estado="ENVIADO">Enviados</button>
                    <button type="button" class="btn btn-sm btn-outline-success" data-estado="ENTREGADO">Entregados</button>
                    <button type="button" class="btn btn-sm btn-outline-danger" data-estado="CANCELADO">Cancelados</button>
                </div>
            </div>
        `;
        infoCard.insertAdjacentHTML('beforeend', filtroHTML);
        
        // Añadir event listeners a los botones de filtro
        const filtroButtons = document.querySelectorAll('#filtro-estados button');
        filtroButtons.forEach(button => {
            button.addEventListener('click', function() {
                // Quitar clase active de todos los botones
                filtroButtons.forEach(btn => btn.classList.remove('active'));
                // Añadir clase active al botón clickeado
                this.classList.add('active');
                
                // Filtrar los pedidos/ventas
                const estado = this.getAttribute('data-estado');
                filtrarPorEstado(estado, isPedidosPage);
            });
        });
    }
}

/**
 * Filtra los pedidos o ventas por estado
 * @param {string} estado - El estado por el que filtrar
 * @param {boolean} isPedidosPage - Indica si estamos en la página de pedidos
 */
function filtrarPorEstado(estado, isPedidosPage) {
    const items = isPedidosPage 
        ? document.querySelectorAll('#accordionPedidos .accordion-item')
        : document.querySelectorAll('.table-responsive tbody tr:not(.collapse)');
    
    items.forEach(item => {
        if (estado === 'todos') {
            item.style.display = '';
            return;
        }
        
        const estadoBadge = item.querySelector('.estado-badge');
        if (!estadoBadge) return;
        
        const itemEstado = estadoBadge.textContent.trim();
        if (itemEstado === estado) {
            item.style.display = '';
        } else {
            item.style.display = 'none';
        }
    });
}

/**
 * Añade animaciones a los mensajes de alerta usando SweetAlert2
 */
function animateAlerts() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        // Solo procesar si el alert es visible
        if (alert.style.display !== 'none' && !alert.classList.contains('processed-by-sweetalert')) {
            alert.classList.add('processed-by-sweetalert');
            
            // Obtener el texto del mensaje
            const mensaje = alert.textContent.trim();
            if (!mensaje) return;
            
            // Determinar el tipo de alerta
            let tipo = 'info';
            if (alert.classList.contains('alert-success')) {
                tipo = 'success';
            } else if (alert.classList.contains('alert-danger')) {
                tipo = 'error';
            } else if (alert.classList.contains('alert-warning')) {
                tipo = 'warning';
            }
            
            // Ocultar el alert original
            alert.style.display = 'none';
            
            // Mostrar con SweetAlert2
            if (tipo === 'success') {
                mostrarToast(mensaje, tipo);
            } else if (tipo === 'error') {
                mostrarError(mensaje);
            } else {
                mostrarInfo(mensaje);
            }
        }
    });
}