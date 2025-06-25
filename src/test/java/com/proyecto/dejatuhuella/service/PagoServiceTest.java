package com.proyecto.dejatuhuella.service;

import com.proyecto.dejatuhuella.dto.PagoRequestDTO;
import com.proyecto.dejatuhuella.model.EstadoPedidoEntity;
import com.proyecto.dejatuhuella.model.Pedido;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.repository.EstadoPedidoRepository;
import com.proyecto.dejatuhuella.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PagoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private EstadoPedidoRepository estadoPedidoRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PagoService pagoService;

    private Pedido pedido;
    private Usuario usuario;
    private PagoRequestDTO pagoRequestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configurar SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Crear usuario de prueba
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Usuario Test");
        usuario.setEmail("test@example.com");

        // Crear estado de pedido
        EstadoPedidoEntity estadoPendiente = new EstadoPedidoEntity();
        estadoPendiente.setId(1L);
        estadoPendiente.setNombreEstado("PENDIENTE");

        // Crear pedido de prueba
        pedido = new Pedido(usuario);
        pedido.setId(1L);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado(estadoPendiente);
        pedido.setTotal(new BigDecimal("100.00"));

        // Crear DTO de pago
        pagoRequestDTO = new PagoRequestDTO();
        pagoRequestDTO.setNumeroTarjeta("4111111111111111");
        pagoRequestDTO.setNombreTitular("Usuario Test");
        pagoRequestDTO.setFechaVencimiento("12/25");
        pagoRequestDTO.setCodigoSeguridad("123");
        pagoRequestDTO.setMetodoPago("VISA");

        // Configurar mocks
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(estadoPedidoRepository.findByNombreEstado("PAGADO")).thenReturn(new EstadoPedidoEntity());
    }

    @Test
    void verificarPedidoPertenecaUsuario_deberiaRetornarTrue_cuandoPedidoPerteneceAlUsuarioAutenticado() {
        // Configurar el usuario autenticado
        when(authentication.getName()).thenReturn("test@example.com");
        when(authentication.isAuthenticated()).thenReturn(true);

        // Ejecutar
        boolean resultado = pagoService.verificarPedidoPertenecaUsuario(pedido);

        // Verificar
        assertTrue(resultado);
    }

    @Test
    void verificarPedidoPertenecaUsuario_deberiaRetornarFalse_cuandoPedidoNoPerteneceAlUsuarioAutenticado() {
        // Configurar el usuario autenticado con un email diferente
        when(authentication.getName()).thenReturn("otro@example.com");
        when(authentication.isAuthenticated()).thenReturn(true);

        // Ejecutar
        boolean resultado = pagoService.verificarPedidoPertenecaUsuario(pedido);

        // Verificar
        assertFalse(resultado);
    }

    @Test
    void procesarPago_deberiaRetornarTrue_cuandoPagoEsExitoso() {
        // Configurar el usuario autenticado
        when(authentication.getName()).thenReturn("test@example.com");
        when(authentication.isAuthenticated()).thenReturn(true);

        // Ejecutar
        boolean resultado = pagoService.procesarPago(1L, pagoRequestDTO);

        // Verificar
        assertTrue(resultado);
        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    void procesarPago_deberiaLanzarExcepcion_cuandoPedidoNoExiste() {
        // Configurar para que el pedido no exista
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        // Ejecutar y verificar
        Exception exception = assertThrows(RuntimeException.class, () -> {
            pagoService.procesarPago(999L, pagoRequestDTO);
        });

        assertEquals("Pedido no encontrado", exception.getMessage());
    }

    @Test
    void procesarPago_deberiaLanzarExcepcion_cuandoPedidoNoPertenecaAlUsuario() {
        // Configurar el usuario autenticado con un email diferente
        when(authentication.getName()).thenReturn("otro@example.com");
        when(authentication.isAuthenticated()).thenReturn(true);

        // Ejecutar y verificar
        Exception exception = assertThrows(RuntimeException.class, () -> {
            pagoService.procesarPago(1L, pagoRequestDTO);
        });

        assertEquals("No tienes permiso para pagar este pedido", exception.getMessage());
    }

    @Test
    void procesarPago_deberiaLanzarExcepcion_cuandoPedidoNoEstaPendiente() {
        // Cambiar el estado del pedido a PAGADO
        EstadoPedidoEntity estadoPagado = new EstadoPedidoEntity();
        estadoPagado.setId(2L);
        estadoPagado.setNombreEstado("PAGADO");
        pedido.setEstado(estadoPagado);

        // Configurar el usuario autenticado
        when(authentication.getName()).thenReturn("test@example.com");
        when(authentication.isAuthenticated()).thenReturn(true);

        // Ejecutar y verificar
        Exception exception = assertThrows(RuntimeException.class, () -> {
            pagoService.procesarPago(1L, pagoRequestDTO);
        });

        assertEquals("Este pedido no está pendiente de pago", exception.getMessage());
    }

    @Test
    void validarTarjeta_noDeberiaLanzarExcepcion_cuandoTarjetaEsValida() {
        // Ejecutar y verificar que no se lance excepción
        assertDoesNotThrow(() -> {
            pagoService.validarTarjeta(pagoRequestDTO);
        });
    }

    @Test
    void validarTarjeta_deberiaLanzarExcepcion_cuandoNumeroTarjetaNoEsValido() {
        // Configurar una tarjeta con número inválido
        pagoRequestDTO.setNumeroTarjeta("123456789012"); // Menos de 16 dígitos

        // Ejecutar y verificar
        Exception exception = assertThrows(RuntimeException.class, () -> {
            pagoService.validarTarjeta(pagoRequestDTO);
        });

        assertEquals("El número de tarjeta debe tener 16 dígitos", exception.getMessage());
    }
    
    @Test
    void validarTarjeta_deberiaLanzarExcepcion_cuandoFechaVencimientoNoEsValida() {
        // Configurar una fecha de vencimiento inválida
        pagoRequestDTO.setFechaVencimiento("13/25"); // Mes inválido

        // Ejecutar y verificar
        Exception exception = assertThrows(RuntimeException.class, () -> {
            pagoService.validarTarjeta(pagoRequestDTO);
        });

        assertEquals("La fecha de vencimiento debe tener el formato MM/YY", exception.getMessage());
    }
    
    @Test
    void validarTarjeta_deberiaLanzarExcepcion_cuandoCodigoSeguridadNoEsValido() {
        // Configurar un código de seguridad inválido
        pagoRequestDTO.setCodigoSeguridad("12"); // Menos de 3 dígitos

        // Ejecutar y verificar
        Exception exception = assertThrows(RuntimeException.class, () -> {
            pagoService.validarTarjeta(pagoRequestDTO);
        });

        assertEquals("El código de seguridad debe tener 3 o 4 dígitos", exception.getMessage());
    }
}