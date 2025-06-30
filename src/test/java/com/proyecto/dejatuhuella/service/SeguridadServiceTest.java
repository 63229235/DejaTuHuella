package com.proyecto.dejatuhuella.service;

import com.proyecto.dejatuhuella.model.DetallePedido;
import com.proyecto.dejatuhuella.model.Pedido;
import com.proyecto.dejatuhuella.model.Producto;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.model.enums.Rol;
import com.proyecto.dejatuhuella.repository.PedidoRepository;
import com.proyecto.dejatuhuella.repository.ProductoRepository;
import com.proyecto.dejatuhuella.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SeguridadServiceTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private SeguridadService seguridadService;

    private Usuario usuarioAutenticado;
    private Usuario otroUsuario;
    private Producto producto;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        // Configurar usuario autenticado
        usuarioAutenticado = new Usuario();
        usuarioAutenticado.setId(1L);
        usuarioAutenticado.setEmail("usuario@test.com");
        usuarioAutenticado.setRol(Rol.USUARIO);

        // Configurar otro usuario
        otroUsuario = new Usuario();
        otroUsuario.setId(2L);
        otroUsuario.setEmail("otro@test.com");
        otroUsuario.setRol(Rol.USUARIO);

        // Configurar producto
        producto = new Producto();
        producto.setId(1L);
        producto.setUsuario(usuarioAutenticado);

        // Configurar pedido
        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuario(usuarioAutenticado);
        pedido.setDetalles(new HashSet<>()); // Inicializar el conjunto de detalles
    }

    @Test
    @DisplayName("Debería verificar que el usuario autenticado es el mismo que el solicitado")
    void verificarUsuarioAutenticado() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("usuario@test.com");
        when(usuarioService.buscarPorEmail("usuario@test.com")).thenReturn(Optional.of(usuarioAutenticado));

        // Act
        boolean resultado = seguridadService.esUsuarioActual(1L);

        // Assert
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Debería retornar false cuando no hay usuario autenticado")
    void esUsuarioActualSinUsuarioAutenticado() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("noexiste@test.com");
        when(usuarioService.buscarPorEmail("noexiste@test.com")).thenReturn(Optional.empty());

        // Act
        boolean resultado = seguridadService.esUsuarioActual(1L);

        // Assert
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Debería verificar si el usuario autenticado es el mismo que el solicitado")
    void esUsuarioActual() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("usuario@test.com");
        when(usuarioService.buscarPorEmail("usuario@test.com")).thenReturn(Optional.of(usuarioAutenticado));

        // Act
        boolean resultado = seguridadService.esUsuarioActual(1L);

        // Assert
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Debería verificar que el usuario autenticado no es el mismo que el solicitado")
    void noEsUsuarioActual() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("usuario@test.com");
        when(usuarioService.buscarPorEmail("usuario@test.com")).thenReturn(Optional.of(usuarioAutenticado));

        // Act
        boolean resultado = seguridadService.esUsuarioActual(2L);

        // Assert
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Debería verificar si el usuario autenticado es propietario del producto")
    void esPropietarioDelProducto() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("usuario@test.com");
        when(usuarioService.buscarPorEmail("usuario@test.com")).thenReturn(Optional.of(usuarioAutenticado));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act
        boolean resultado = seguridadService.esPropietarioDelProducto(1L);

        // Assert
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Debería verificar que el usuario autenticado no es propietario del producto")
    void noEsPropietarioDelProducto() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("usuario@test.com");
        when(usuarioService.buscarPorEmail("usuario@test.com")).thenReturn(Optional.of(usuarioAutenticado));

        Producto productoDeOtroUsuario = new Producto();
        productoDeOtroUsuario.setId(2L);
        productoDeOtroUsuario.setUsuario(otroUsuario);

        when(productoRepository.findById(2L)).thenReturn(Optional.of(productoDeOtroUsuario));

        // Act
        boolean resultado = seguridadService.esPropietarioDelProducto(2L);

        // Assert
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Debería verificar si el usuario autenticado es propietario del pedido")
    void esPropietarioDelPedido() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("usuario@test.com");
        when(usuarioRepository.findByEmail("usuario@test.com")).thenReturn(Optional.of(usuarioAutenticado));
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Act
        boolean resultado = seguridadService.esPropietarioDelPedido(1L);

        // Assert
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Debería verificar que el usuario autenticado no es propietario del pedido")
    void noEsPropietarioDelPedido() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("usuario@test.com");
        when(usuarioRepository.findByEmail("usuario@test.com")).thenReturn(Optional.of(usuarioAutenticado));

        Pedido pedidoDeOtroUsuario = new Pedido();
        pedidoDeOtroUsuario.setId(2L);
        pedidoDeOtroUsuario.setUsuario(otroUsuario);

        when(pedidoRepository.findById(2L)).thenReturn(Optional.of(pedidoDeOtroUsuario));

        // Act
        boolean resultado = seguridadService.esPropietarioDelPedido(2L);

        // Assert
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Debería verificar si el usuario autenticado es vendedor en el pedido")
    void esVendedorEnPedido() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails); // ✅ solución
        when(userDetails.getUsername()).thenReturn("usuario@test.com");

        when(usuarioService.buscarPorEmail("usuario@test.com")).thenReturn(Optional.of(usuarioAutenticado));

        // Crear un pedido con un producto del usuario autenticado
        Pedido pedidoConProductoDelUsuario = new Pedido();
        pedidoConProductoDelUsuario.setId(3L);
        pedidoConProductoDelUsuario.setUsuario(otroUsuario);

        DetallePedido detalle = new DetallePedido();
        detalle.setPedido(pedidoConProductoDelUsuario);
        detalle.setProducto(producto); // producto pertenece a usuarioAutenticado

        Set<DetallePedido> detalles = new HashSet<>();
        detalles.add(detalle);
        pedidoConProductoDelUsuario.setDetalles(detalles);

        when(pedidoRepository.findById(3L)).thenReturn(Optional.of(pedidoConProductoDelUsuario));

        // Act
        boolean resultado = seguridadService.esVendedorEnPedido(3L);

        // Assert
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Debería verificar que el usuario autenticado no es vendedor en el pedido")
    void noEsVendedorEnPedido() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails); // ✅ solución
        when(userDetails.getUsername()).thenReturn("usuario@test.com");

        when(usuarioService.buscarPorEmail("usuario@test.com")).thenReturn(Optional.of(usuarioAutenticado));

        // Crear un pedido con productos de otro usuario
        Pedido pedidoSinProductosDelUsuario = new Pedido();
        pedidoSinProductosDelUsuario.setId(4L);
        pedidoSinProductosDelUsuario.setUsuario(otroUsuario);

        Producto productoDeOtroUsuario = new Producto();
        productoDeOtroUsuario.setId(3L);
        productoDeOtroUsuario.setUsuario(otroUsuario);

        DetallePedido detalle = new DetallePedido();
        detalle.setPedido(pedidoSinProductosDelUsuario);
        detalle.setProducto(productoDeOtroUsuario);

        Set<DetallePedido> detalles = new HashSet<>();
        detalles.add(detalle);
        pedidoSinProductosDelUsuario.setDetalles(detalles);

        when(pedidoRepository.findById(4L)).thenReturn(Optional.of(pedidoSinProductosDelUsuario));

        // Act
        boolean resultado = seguridadService.esVendedorEnPedido(4L);

        // Assert
        assertFalse(resultado);
    }

}
