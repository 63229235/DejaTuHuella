/**
 * sweetalert-utils.js - Utilidades para mostrar notificaciones con SweetAlert2
 */

// Función para mostrar una notificación de éxito
function mostrarExito(mensaje, titulo = 'Éxito') {
    Swal.fire({
        title: titulo,
        text: mensaje,
        icon: 'success',
        confirmButtonText: 'Aceptar',
        confirmButtonColor: '#28a745',
        timer: 3000,
        timerProgressBar: true
    });
}

// Función para mostrar una notificación de error
function mostrarError(mensaje, titulo = 'Error') {
    Swal.fire({
        title: titulo,
        text: mensaje,
        icon: 'error',
        confirmButtonText: 'Aceptar',
        confirmButtonColor: '#dc3545'
    });
}

// Función para mostrar una notificación de información
function mostrarInfo(mensaje, titulo = 'Información') {
    Swal.fire({
        title: titulo,
        text: mensaje,
        icon: 'info',
        confirmButtonText: 'Aceptar',
        confirmButtonColor: '#17a2b8'
    });
}

// Función para mostrar una notificación de advertencia
function mostrarAdvertencia(mensaje, titulo = 'Advertencia') {
    Swal.fire({
        title: titulo,
        text: mensaje,
        icon: 'warning',
        confirmButtonText: 'Aceptar',
        confirmButtonColor: '#ffc107'
    });
}

// Función para mostrar una confirmación
function mostrarConfirmacion(mensaje, titulo = '¿Estás seguro?', callback) {
    Swal.fire({
        title: titulo,
        text: mensaje,
        icon: 'question',
        showCancelButton: true,
        confirmButtonText: 'Sí',
        cancelButtonText: 'No',
        confirmButtonColor: '#28a745',
        cancelButtonColor: '#dc3545'
    }).then((result) => {
        if (result.isConfirmed && typeof callback === 'function') {
            callback();
        }
    });
}

// Función para mostrar una notificación tipo toast (pequeña y temporal)
function mostrarToast(mensaje, tipo = 'success') {
    const Toast = Swal.mixin({
        toast: true,
        position: 'top-end',
        showConfirmButton: false,
        timer: 3000,
        timerProgressBar: true,
        didOpen: (toast) => {
            toast.addEventListener('mouseenter', Swal.stopTimer);
            toast.addEventListener('mouseleave', Swal.resumeTimer);
        }
    });

    Toast.fire({
        icon: tipo,
        title: mensaje
    });
}