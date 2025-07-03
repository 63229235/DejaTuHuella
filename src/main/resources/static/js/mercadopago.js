/**
 * Funcionalidad específica para la integración con Mercado Pago
 */
document.addEventListener('DOMContentLoaded', function() {
    // Verificar si estamos en la página de Mercado Pago
    const mercadoPagoContainer = document.getElementById('wallet_container');
    if (!mercadoPagoContainer) return;

    // Obtener el ID de preferencia desde el atributo data
    const preferenceId = mercadoPagoContainer.getAttribute('data-preference-id');
    if (!preferenceId) {
        console.error('Error: No se encontró el ID de preferencia de Mercado Pago');
        return;
    }

    // Inicializar el SDK de Mercado Pago
    const mp = new MercadoPago(mercadoPagoContainer.getAttribute('data-public-key'), {
        locale: 'es-AR'
    });

    // Renderizar el botón de pago
    mp.checkout({
        preference: {
            id: preferenceId
        },
        render: {
            container: '#wallet_container',
            label: 'Pagar ahora'
        },
        theme: {
            elementsColor: '#4CAF50',
            headerColor: '#4CAF50'
        }
    });

    // Mostrar mensaje de procesamiento
    const processingMessage = document.getElementById('processing-message');
    if (processingMessage) {
        // Inicialmente oculto
        processingMessage.style.display = 'none';
        
        // Mostrar cuando se hace clic en el botón de pago
        const observer = new MutationObserver(function(mutations) {
            mutations.forEach(function(mutation) {
                if (mutation.type === 'childList' && mutation.addedNodes.length > 0) {
                    // Buscar el botón de Mercado Pago una vez que se haya renderizado
                    const mpButton = document.querySelector('.mercadopago-button');
                    if (mpButton) {
                        mpButton.addEventListener('click', function() {
                            processingMessage.style.display = 'block';
                        });
                        observer.disconnect(); // Dejar de observar una vez que se encuentra el botón
                    }
                }
            });
        });

        // Configurar el observador para detectar cuando se renderiza el botón
        observer.observe(mercadoPagoContainer, { childList: true, subtree: true });
    }

    // Animación para el contenedor de Mercado Pago
    const mpContainer = document.querySelector('.mercadopago-button-container');
    if (mpContainer) {
        mpContainer.style.opacity = '0';
        mpContainer.style.transform = 'translateY(20px)';
        mpContainer.style.transition = 'opacity 0.8s ease, transform 0.8s ease';
        
        // Mostrar con animación después de un retraso
        setTimeout(() => {
            mpContainer.style.opacity = '1';
            mpContainer.style.transform = 'translateY(0)';
        }, 500);
    }
});