document.addEventListener('DOMContentLoaded', function() {
            const navbar = document.querySelector('.navbar-custom');
            let lastScrollTop = 0;
            const scrollThreshold = 10; // Umbral de desplazamiento para activar el efecto

            // Añadir padding-top al body para compensar el navbar fixed
            document.body.style.paddingTop = navbar.offsetHeight + 'px';

            // Función para manejar el scroll
            function handleScroll() {
                let currentScroll = window.pageYOffset || document.documentElement.scrollTop;

                // Determinar dirección del scroll
                if (currentScroll > lastScrollTop && currentScroll > scrollThreshold) {
                    // Scroll hacia abajo - ocultar navbar
                    navbar.classList.add('navbar-hidden');
                } else if (currentScroll < lastScrollTop || currentScroll <= scrollThreshold) {
                    // Scroll hacia arriba o en la parte superior - mostrar navbar
                    navbar.classList.remove('navbar-hidden');
                }

                lastScrollTop = currentScroll <= 0 ? 0 : currentScroll; // Para móviles
            }

            // Agregar el evento de scroll con passive: true para mejor rendimiento
            window.addEventListener('scroll', handleScroll, { passive: true });
            
            // Ejecutar una vez al cargar para establecer el estado inicial
            handleScroll();
        });