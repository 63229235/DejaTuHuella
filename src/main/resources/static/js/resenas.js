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
                alert('Por favor, selecciona una calificación');
                return;
            }
            
            // Validar que se haya escrito un comentario
            if (!comentario.trim()) {
                alert('Por favor, escribe un comentario');
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
                    alert(data.error);
                } else {
                    // Recargar la página para mostrar la nueva reseña
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Ha ocurrido un error al enviar la reseña');
            });
        });
    }
    
    // Botones para eliminar reseña
    const eliminarBotones = document.querySelectorAll('.eliminar-resena');
    eliminarBotones.forEach(boton => {
        boton.addEventListener('click', function() {
            if (confirm('¿Estás seguro de que deseas eliminar esta reseña?')) {
                const resenaId = this.getAttribute('data-resena-id');
                
                fetch(`/api/resenas/${resenaId}`, {
                    method: 'DELETE'
                })
                .then(response => response.json())
                .then(data => {
                    if (data.error) {
                        alert(data.error);
                    } else {
                        // Recargar la página para actualizar la lista de reseñas
                        window.location.reload();
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Ha ocurrido un error al eliminar la reseña');
                });
            }
        });
    });
});