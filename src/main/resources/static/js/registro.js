document.addEventListener('DOMContentLoaded', function() {
        const form = document.querySelector('form');

        form.addEventListener('submit', function(e) {
            e.preventDefault();

            const userData = {
                nombre: document.getElementById('nombre').value,
                apellido: document.getElementById('apellido').value,
                email: document.getElementById('email').value,
                password: document.getElementById('password').value,
                rol: document.getElementById('rol').value
            };

            fetch('/api/usuarios', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            })
            .then(response => {
                if (response.ok) {
                    alert('Usuario registrado correctamente');
                    window.location.href = '/login';
                } else {
                    return response.text().then(text => {
                        throw new Error(text || 'Error al registrar usuario');
                    });
                }
            })
            .catch(error => {
                alert('Error: ' + error.message);
            });
        });
    });