/**
 * Funcionalidad para la pasarela de pago
 */
document.addEventListener('DOMContentLoaded', function() {
    // Formatear número de tarjeta mientras se escribe
    const numeroTarjeta = document.getElementById('numeroTarjeta');
    if (numeroTarjeta) {
        numeroTarjeta.addEventListener('input', function(e) {
            // Eliminar cualquier carácter que no sea número
            let value = this.value.replace(/\D/g, '');
            // Limitar a 16 dígitos
            if (value.length > 16) {
                value = value.slice(0, 16);
            }
            this.value = value;
            
            // Detectar tipo de tarjeta
            const cardIcon = document.getElementById('cardIcon');
            if (cardIcon) {
                if (value.startsWith('4')) {
                    cardIcon.className = 'fab fa-cc-visa';
                } else if (value.startsWith('5')) {
                    cardIcon.className = 'fab fa-cc-mastercard';
                } else if (value.startsWith('3')) {
                    cardIcon.className = 'fab fa-cc-amex';
                } else if (value.startsWith('6')) {
                    cardIcon.className = 'fab fa-cc-discover';
                } else {
                    cardIcon.className = 'fas fa-credit-card';
                }
            }
        });
    }
    
    // Formatear fecha de vencimiento
    const fechaVencimiento = document.getElementById('fechaVencimiento');
    if (fechaVencimiento) {
        fechaVencimiento.addEventListener('input', function(e) {
            let value = this.value.replace(/\D/g, '');
            if (value.length > 0) {
                // Formatear como MM/YY
                if (value.length > 2) {
                    value = value.substring(0, 2) + '/' + value.substring(2);
                }
                // Limitar a 5 caracteres (MM/YY)
                if (value.length > 5) {
                    value = value.slice(0, 5);
                }
            }
            this.value = value;
            
            // Validar que el mes esté entre 01 y 12
            if (value.length >= 2) {
                const mes = parseInt(value.substring(0, 2));
                if (mes < 1) {
                    this.value = '01' + value.substring(2);
                } else if (mes > 12) {
                    this.value = '12' + value.substring(2);
                }
            }
        });
    }
    
    // Validar código de seguridad (solo números)
    const codigoSeguridad = document.getElementById('codigoSeguridad');
    if (codigoSeguridad) {
        codigoSeguridad.addEventListener('input', function(e) {
            // Eliminar cualquier carácter que no sea número
            let value = this.value.replace(/\D/g, '');
            // Limitar a 4 dígitos
            if (value.length > 4) {
                value = value.slice(0, 4);
            }
            this.value = value;
        });
    }
    
    // Selección de método de pago
    const paymentOptions = document.querySelectorAll('.payment-method-option');
    const metodoPagoInput = document.getElementById('metodoPago');
    
    if (paymentOptions.length > 0 && metodoPagoInput) {
        paymentOptions.forEach(option => {
            option.addEventListener('click', function() {
                // Quitar la clase 'selected' de todas las opciones
                paymentOptions.forEach(opt => opt.classList.remove('selected'));
                // Agregar la clase 'selected' a la opción clickeada
                this.classList.add('selected');
                // Actualizar el valor del input hidden
                metodoPagoInput.value = this.getAttribute('data-method');
            });
        });
    }
    
    // Validación del formulario antes de enviar
    const formPago = document.querySelector('form[action*="/pagos/procesar"]');
    if (formPago) {
        // Asegurarse de que el método de pago esté seleccionado al cargar la página
        const metodoPagoInput = document.getElementById('metodoPago');
        if (metodoPagoInput && metodoPagoInput.value === '') {
            metodoPagoInput.value = 'TARJETA_CREDITO'; // Valor por defecto
            document.querySelector('.payment-method-option[data-method="TARJETA_CREDITO"]')?.classList.add('selected');
        }
        
        formPago.addEventListener('submit', function(e) {
            e.preventDefault(); // Prevenir el envío por defecto para validar primero
            
            let isValid = true;
            let errorMessage = '';
            
            // Validar número de tarjeta
            const numeroTarjeta = document.getElementById('numeroTarjeta');
            if (!numeroTarjeta || numeroTarjeta.value.length !== 16) {
                isValid = false;
                errorMessage = 'El número de tarjeta debe tener 16 dígitos';
            }
            
            // Validar nombre del titular
            const nombreTitular = document.getElementById('nombreTitular');
            if (!nombreTitular || nombreTitular.value.trim() === '') {
                isValid = false;
                errorMessage = 'El nombre del titular es obligatorio';
            }
            
            // Validar fecha de vencimiento
            const fechaVencimiento = document.getElementById('fechaVencimiento');
            if (!fechaVencimiento || !/^(0[1-9]|1[0-2])\/([0-9]{2})$/.test(fechaVencimiento.value)) {
                isValid = false;
                errorMessage = 'La fecha de vencimiento debe tener el formato MM/YY';
            } else {
                // Validar que la fecha no esté expirada
                const [mes, anio] = fechaVencimiento.value.split('/');
                const fechaExp = new Date(2000 + parseInt(anio), parseInt(mes) - 1, 1);
                const hoy = new Date();
                if (fechaExp < hoy) {
                    isValid = false;
                    errorMessage = 'La tarjeta ha expirado';
                }
            }
            
            // Validar código de seguridad
            const codigoSeguridad = document.getElementById('codigoSeguridad');
            if (!codigoSeguridad || (codigoSeguridad.value.length < 3 || codigoSeguridad.value.length > 4)) {
                isValid = false;
                errorMessage = 'El código de seguridad debe tener 3 o 4 dígitos';
            }
            
            // Validar método de pago
            const metodoPago = document.getElementById('metodoPago');
            if (!metodoPago || metodoPago.value.trim() === '') {
                isValid = false;
                errorMessage = 'Debe seleccionar un método de pago';
            }
            
            // Si hay errores, mostrarlos y prevenir el envío del formulario
            if (!isValid) {
                showError('Error en el pago', errorMessage);
            } else {
                // Si todo está bien, enviar el formulario
                formPago.submit();
            }
        });
    }
    
    // Animación para la confirmación de pago
    const confirmationIcon = document.querySelector('.confirmation-icon');
    if (confirmationIcon) {
        confirmationIcon.style.transition = 'transform 0.5s ease';
        
        setTimeout(() => {
            confirmationIcon.style.transform = 'scale(1.2)';
            setTimeout(() => {
                confirmationIcon.style.transform = 'scale(1)';
            }, 500);
        }, 300);
    }
    
    // Animación para el mensaje de agradecimiento
    const thankYouMessage = document.querySelector('.thank-you-message');
    if (thankYouMessage) {
        // Inicialmente oculto
        thankYouMessage.style.opacity = '0';
        thankYouMessage.style.transform = 'translateY(20px)';
        thankYouMessage.style.transition = 'opacity 0.8s ease, transform 0.8s ease';
        
        // Mostrar con animación después de un retraso
        setTimeout(() => {
            thankYouMessage.style.opacity = '1';
            thankYouMessage.style.transform = 'translateY(0)';
        }, 1000);
    }
});