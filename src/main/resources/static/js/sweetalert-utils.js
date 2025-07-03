/**
 * sweetalert-utils.js - Utilidades para mostrar notificaciones con SweetAlert2
 * Funciones reutilizables para alertas, confirmaciones y notificaciones
 */

// Función para mostrar alertas de éxito
function showSuccess(title, text = '') {
    Swal.fire({
        icon: 'success',
        title: title,
        text: text,
        confirmButtonColor: '#28a745',
        timer: 3000,
        timerProgressBar: true
    });
}

// Función para mostrar alertas de error
function showError(title, text = '') {
    Swal.fire({
        icon: 'error',
        title: title,
        text: text,
        confirmButtonColor: '#dc3545'
    });
}

// Función para mostrar alertas de información
function showInfo(title, text = '') {
    Swal.fire({
        icon: 'info',
        title: title,
        text: text,
        confirmButtonColor: '#17a2b8'
    });
}

// Función para mostrar alertas de advertencia
function showWarning(title, text = '') {
    Swal.fire({
        icon: 'warning',
        title: title,
        text: text,
        confirmButtonColor: '#ffc107'
    });
}

// Función para mostrar confirmaciones
function showConfirm(title, text, confirmText = 'Sí', cancelText = 'Cancelar') {
    return Swal.fire({
        title: title,
        text: text,
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#28a745',
        cancelButtonColor: '#dc3545',
        confirmButtonText: confirmText,
        cancelButtonText: cancelText
    });
}

// Toast notifications (notificaciones pequeñas)
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

// Función para mostrar toast de éxito
function showToastSuccess(message) {
    Toast.fire({
        icon: 'success',
        title: message
    });
}

// Función para mostrar toast de error
function showToastError(message) {
    Toast.fire({
        icon: 'error',
        title: message
    });
}

// Función para mostrar toast de información
function showToastInfo(message) {
    Toast.fire({
        icon: 'info',
        title: message
    });
}

// Función para mostrar toast de advertencia
function showToastWarning(message) {
    Toast.fire({
        icon: 'warning',
        title: message
    });
}

// Función para procesar mensajes flash de Thymeleaf
function processFlashMessages() {
    // Procesar mensajes de éxito
    const successMessages = document.querySelectorAll('.alert-success');
    successMessages.forEach(alert => {
        const message = alert.textContent.trim();
        if (message) {
            showToastSuccess(message);
            alert.style.display = 'none';
        }
    });

    // Procesar mensajes de error
    const errorMessages = document.querySelectorAll('.alert-danger');
    errorMessages.forEach(alert => {
        const message = alert.textContent.trim();
        if (message) {
            showToastError(message);
            alert.style.display = 'none';
        }
    });

    // Procesar mensajes de información
    const infoMessages = document.querySelectorAll('.alert-info');
    infoMessages.forEach(alert => {
        const message = alert.textContent.trim();
        if (message && !alert.classList.contains('keep-visible')) {
            showToastInfo(message);
            alert.style.display = 'none';
        }
    });

    // Procesar mensajes de advertencia
    const warningMessages = document.querySelectorAll('.alert-warning');
    warningMessages.forEach(alert => {
        const message = alert.textContent.trim();
        if (message) {
            showToastWarning(message);
            alert.style.display = 'none';
        }
    });
}

// Auto-ejecutar el procesamiento de mensajes flash cuando se carga la página
document.addEventListener('DOMContentLoaded', function() {
    // Solo procesar si SweetAlert2 está disponible
    if (typeof Swal !== 'undefined') {
        processFlashMessages();
    }
});