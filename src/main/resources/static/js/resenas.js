document.addEventListener('DOMContentLoaded', function() {
    // Formulario para agregar reseña
    const resenaForm = document.getElementById('resenaForm');
    if (resenaForm) {
        resenaForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            // Obtener los valores del formulario
            const productoId = document.getElementById('productoId').value;
            const calificacion = document.querySelector('input[name="calificacion"]:checked');
            const comentario = document.getElementById('comentario').value;
            
            // Validar que se haya seleccionado una calificación
            if (!calificacion) {
                showError('Por favor, selecciona una calificación');
                return;
            }
            
            // Validar que se haya escrito un comentario
            if (!comentario.trim()) {
                showError('Por favor, escribe un comentario');
                return;
            }
            
            // Enviar la reseña al servidor
            fetch('/api/resenas', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    productoId: productoId,
                    calificacion: calificacion.value,
                    comentario: comentario
                })
            })
            .then(response => response.json())
            .then(data => {
                if (data.error) {
                    showError(data.error);
                } else {
                    showSuccess('Reseña enviada exitosamente');
                    // Recargar la página para mostrar la nueva reseña
                    setTimeout(() => window.location.reload(), 1500);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showError('Ha ocurrido un error al enviar la reseña');
            });
        });
    }
    
    // Botones para eliminar reseña
    const eliminarBotones = document.querySelectorAll('.eliminar-resena');
    eliminarBotones.forEach(boton => {
        boton.addEventListener('click', function() {
            const resenaId = this.getAttribute('data-resena-id');
            
            showConfirm('¿Estás seguro?', '¿Deseas eliminar esta reseña? Esta acción no se puede deshacer.', function() {
                fetch(`/api/resenas/${resenaId}`, {
                    method: 'DELETE'
                })
                .then(response => response.json())
                .then(data => {
                    if (data.error) {
                        showError(data.error);
                    } else {
                        showSuccess('Reseña eliminada exitosamente');
                        // Recargar la página para actualizar la lista de reseñas
                        setTimeout(() => window.location.reload(), 1500);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showError('Ha ocurrido un error al eliminar la reseña');
                });
            });
        });
    });
});