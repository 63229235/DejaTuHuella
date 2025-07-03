package com.proyecto.dejatuhuella.service;

import com.proyecto.dejatuhuella.model.Carrito;
import com.proyecto.dejatuhuella.model.CarritoItem;
import com.proyecto.dejatuhuella.model.Producto;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.repository.CarritoItemRepository;
import com.proyecto.dejatuhuella.repository.CarritoRepository;
import com.proyecto.dejatuhuella.repository.ProductoRepository;
import com.proyecto.dejatuhuella.repository.UsuarioRepository;
import com.proyecto.dejatuhuella.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarritoPersistentServiceTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private CarritoItemRepository carritoItemRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails userDetails;

    @InjectMocks
    private CarritoPersistentService carritoPersistentService;

    private Usuario usuario;
    private Producto producto;
    private Carrito carrito;
    private CarritoItem carritoItem;

    @BeforeEach
    void setUp() {
        // Configurar usuario
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setEmail("juan.perez@example.com");

        // Configurar producto
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Collar para perro");
        producto.setPrecio(new BigDecimal("29.99"));
        producto.setStock(10);

        // Configurar carrito
        carrito = new Carrito(usuario);
        carrito.setId(1L);

        // Configurar item de carrito
        carritoItem = new CarritoItem(carrito, producto, 2);

        // Configurar el contexto de seguridad simulado
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Debería obtener el carrito del usuario actual")
    void obtenerCarritoUsuarioActual() {
        // Configurar el contexto de seguridad
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsuario()).thenReturn(usuario);

        // Configurar el repositorio
        when(carritoRepository.findByUsuario(usuario)).thenReturn(Optional.of(carrito));

        // Ejecutar el método
        Carrito resultado = carritoPersistentService.obtenerCarritoUsuarioActual();

        // Verificar
        assertNotNull(resultado);
        assertEquals(carrito.getId(), resultado.getId());
        assertEquals(usuario, resultado.getUsuario());
        verify(carritoRepository).findByUsuario(usuario);
    }

    @Test
    @DisplayName("Debería crear un nuevo carrito si no existe")
    void crearNuevoCarritoSiNoExiste() {
        // Configurar el contexto de seguridad
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsuario()).thenReturn(usuario);

        // Configurar el repositorio para que no encuentre un carrito existente
        when(carritoRepository.findByUsuario(usuario)).thenReturn(Optional.empty());
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carrito);

        // Ejecutar el método
        Carrito resultado = carritoPersistentService.obtenerCarritoUsuarioActual();

        // Verificar
        assertNotNull(resultado);
        assertEquals(carrito.getId(), resultado.getId());
        verify(carritoRepository).findByUsuario(usuario);
        verify(carritoRepository).save(any(Carrito.class));
    }

    @Test
    @DisplayName("Debería agregar un producto al carrito")
    void agregarProducto() {
        // Configurar el contexto de seguridad
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsuario()).thenReturn(usuario);

        // Configurar los repositorios
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(carritoRepository.findByUsuario(usuario)).thenReturn(Optional.of(carrito));
        when(carritoItemRepository.findByCarritoAndProducto(carrito, producto)).thenReturn(Optional.empty());

        // Ejecutar el método
        carritoPersistentService.agregarProducto(1L, 2);

        // Verificar
        verify(carritoItemRepository).save(any(CarritoItem.class));
    }

    @Test
    @DisplayName("Debería actualizar la cantidad si el producto ya está en el carrito")
    void actualizarCantidadSiProductoExiste() {
        // Configurar el contexto de seguridad
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsuario()).thenReturn(usuario);

        // Configurar los repositorios
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(carritoRepository.findByUsuario(usuario)).thenReturn(Optional.of(carrito));
        when(carritoItemRepository.findByCarritoAndProducto(carrito, producto)).thenReturn(Optional.of(carritoItem));

        // Ejecutar el método
        carritoPersistentService.agregarProducto(1L, 3);

        // Verificar
        assertEquals(5, carritoItem.getCantidad()); // 2 (original) + 3 (agregado)
        verify(carritoItemRepository).save(carritoItem);
    }

    @Test
    @DisplayName("Debería lanzar excepción si la cantidad es menor o igual a cero")
    void lanzarExcepcionSiCantidadInvalida() {
        // Ejecutar y verificar
        assertThrows(IllegalArgumentException.class, () -> {
            carritoPersistentService.agregarProducto(1L, 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            carritoPersistentService.agregarProducto(1L, -1);
        });
    }

    @Test
    @DisplayName("Debería obtener los items detallados del carrito")
    void getItemsDetallados() {
        // Configurar el contexto de seguridad
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsuario()).thenReturn(usuario);

        // Configurar los repositorios
        when(carritoRepository.findByUsuario(usuario)).thenReturn(Optional.of(carrito));
        when(carritoItemRepository.findByCarrito(carrito)).thenReturn(List.of(carritoItem));

        // Ejecutar el método
        List<Map<String, Object>> resultado = carritoPersistentService.getItemsDetallados();

        // Verificar
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(producto, resultado.get(0).get("producto"));
        assertEquals(2, resultado.get(0).get("cantidad"));
    }

    @Test
    @DisplayName("Debería calcular el total del carrito")
    void getTotal() {
        // Configurar el contexto de seguridad
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsuario()).thenReturn(usuario);

        // Configurar los repositorios
        when(carritoRepository.findByUsuario(usuario)).thenReturn(Optional.of(carrito));
        when(carritoItemRepository.findByCarrito(carrito)).thenReturn(List.of(carritoItem));

        // Ejecutar el método
        BigDecimal total = carritoPersistentService.getTotal();

        // Verificar
        assertEquals(new BigDecimal("59.98"), total); // 29.99 * 2
    }

    @Test
    @DisplayName("Debería verificar el stock correctamente")
    void verificarStock() {
        // Configurar el contexto de seguridad
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsuario()).thenReturn(usuario);

        // Configurar los repositorios
        when(carritoRepository.findByUsuario(usuario)).thenReturn(Optional.of(carrito));
        when(carritoItemRepository.findByCarrito(carrito)).thenReturn(List.of(carritoItem));

        // Caso 1: Stock suficiente
        boolean resultado1 = carritoPersistentService.verificarStock();
        assertTrue(resultado1);

        // Caso 2: Stock insuficiente
        producto.setStock(1); // Menos que la cantidad en el carrito (2)
        boolean resultado2 = carritoPersistentService.verificarStock();
        assertFalse(resultado2);
    }

    @Test
    @DisplayName("Debería vaciar el carrito")
    void vaciarCarrito() {
        // Configurar el contexto de seguridad
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsuario()).thenReturn(usuario);

        // Configurar los repositorios
        when(carritoRepository.findByUsuario(usuario)).thenReturn(Optional.of(carrito));

        // Ejecutar el método
        carritoPersistentService.vaciarCarrito();

        // Verificar
        verify(carritoItemRepository).deleteByCarrito(carrito);
    }
}