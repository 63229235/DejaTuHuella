package com.proyecto.dejatuhuella.controller;

import com.proyecto.dejatuhuella.dto.PagoRequestDTO;
import com.proyecto.dejatuhuella.model.EstadoPedidoEntity;
import com.proyecto.dejatuhuella.model.Pedido;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.service.PagoService;
import com.proyecto.dejatuhuella.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PagoControllerTest {

    @Mock
    private PagoService pagoService;

    @Mock
    private PedidoService pedidoService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private PagoController pagoController;

    private MockMvc mockMvc;
    private Pedido pedido;
    private PagoRequestDTO pagoRequestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(pagoController).build();

        // Crear usuario de prueba
        Usuario usuario = new Usuario();
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
    }

    @Test
    void mostrarFormularioPago_deberiaRetornarVistaFormulario_cuandoPedidoExisteYPertenecaAlUsuario() {
        // Configurar mocks
        when(pedidoService.obtenerPedidoPorId(1L)).thenReturn(Optional.of(pedido));
        when(pagoService.verificarPedidoPertenecaUsuario(pedido)).thenReturn(true);

        // Ejecutar
        String vista = pagoController.mostrarFormularioPago(1L, model);

        // Verificar
        assertEquals("pago/formulario", vista);
        verify(model).addAttribute(eq("pedido"), eq(pedido));
        verify(model).addAttribute(eq("pagoRequest"), any(PagoRequestDTO.class));
    }

    @Test
    void mostrarFormularioPago_deberiaRedirigir_cuandoPedidoNoExiste() {
        // Configurar mocks
        when(pedidoService.obtenerPedidoPorId(999L)).thenReturn(Optional.empty());

        // Ejecutar
        String vista = pagoController.mostrarFormularioPago(999L, model);

        // Verificar
        assertEquals("redirect:/mis-compras?error=Pedido no encontrado", vista);
    }

    @Test
    void mostrarFormularioPago_deberiaRedirigir_cuandoPedidoNoPertenecaAlUsuario() {
        // Configurar mocks
        when(pedidoService.obtenerPedidoPorId(1L)).thenReturn(Optional.of(pedido));
        when(pagoService.verificarPedidoPertenecaUsuario(pedido)).thenReturn(false);

        // Ejecutar
        String vista = pagoController.mostrarFormularioPago(1L, model);

        // Verificar
        assertEquals("redirect:/mis-compras?error=No tienes permiso para pagar este pedido", vista);
    }

    @Test
    void mostrarFormularioPago_deberiaRedirigir_cuandoPedidoNoEstaPendiente() {
        // Cambiar el estado del pedido a PAGADO
        EstadoPedidoEntity estadoPagado = new EstadoPedidoEntity();
        estadoPagado.setId(2L);
        estadoPagado.setNombreEstado("PAGADO");
        pedido.setEstado(estadoPagado);

        // Configurar mocks
        when(pedidoService.obtenerPedidoPorId(1L)).thenReturn(Optional.of(pedido));
        when(pagoService.verificarPedidoPertenecaUsuario(pedido)).thenReturn(true);

        // Ejecutar
        String vista = pagoController.mostrarFormularioPago(1L, model);

        // Verificar
        assertEquals("redirect:/mis-compras?error=Este pedido no está pendiente de pago", vista);
    }

    @Test
    void procesarPago_deberiaRedirigirAConfirmacion_cuandoPagoEsExitoso() {
        // Configurar mocks
        when(pagoService.procesarPago(1L, pagoRequestDTO)).thenReturn(true);

        // Ejecutar
        String vista = pagoController.procesarPago(1L, pagoRequestDTO, redirectAttributes);

        // Verificar
        assertEquals("redirect:/pagos/confirmacion/1", vista);
        verify(pedidoService).actualizarEstadoPedido(1L, "PAGADO");
        verify(redirectAttributes).addFlashAttribute(eq("mensaje"), anyString());
    }

    @Test
    void procesarPago_deberiaRedirigirAFormulario_cuandoPagoFalla() {
        // Configurar mocks
        when(pagoService.procesarPago(1L, pagoRequestDTO)).thenReturn(false);

        // Ejecutar
        String vista = pagoController.procesarPago(1L, pagoRequestDTO, redirectAttributes);

        // Verificar
        assertEquals("redirect:/pagos/procesar/1", vista);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    @Test
    void procesarPago_deberiaRedirigirAFormulario_cuandoOcurreExcepcion() {
        // Configurar mocks
        when(pagoService.procesarPago(1L, pagoRequestDTO)).thenThrow(new RuntimeException("Error de prueba"));

        // Ejecutar
        String vista = pagoController.procesarPago(1L, pagoRequestDTO, redirectAttributes);

        // Verificar
        assertEquals("redirect:/pagos/procesar/1", vista);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    @Test
    void mostrarConfirmacionPago_deberiaRetornarVistaConfirmacion_cuandoPedidoExisteYPertenecaAlUsuario() {
        // Configurar mocks
        when(pedidoService.obtenerPedidoPorId(1L)).thenReturn(Optional.of(pedido));
        when(pagoService.verificarPedidoPertenecaUsuario(pedido)).thenReturn(true);

        // Ejecutar
        String vista = pagoController.mostrarConfirmacionPago(1L, model);

        // Verificar
        assertEquals("pago/confirmacion", vista);
        verify(model).addAttribute(eq("pedido"), eq(pedido));
    }

    @Test
    void mostrarConfirmacionPago_deberiaRedirigir_cuandoPedidoNoExiste() {
        // Configurar mocks
        when(pedidoService.obtenerPedidoPorId(999L)).thenReturn(Optional.empty());

        // Ejecutar
        String vista = pagoController.mostrarConfirmacionPago(999L, model);

        // Verificar
        assertEquals("redirect:/mis-compras?error=Pedido no encontrado", vista);
    }

    @Test
    void mostrarConfirmacionPago_deberiaRedirigir_cuandoPedidoNoPertenecaAlUsuario() {
        // Configurar mocks
        when(pedidoService.obtenerPedidoPorId(1L)).thenReturn(Optional.of(pedido));
        when(pagoService.verificarPedidoPertenecaUsuario(pedido)).thenReturn(false);

        // Ejecutar
        String vista = pagoController.mostrarConfirmacionPago(1L, model);

        // Verificar
        assertEquals("redirect:/mis-compras?error=No tienes permiso para ver este pedido", vista);
    }
}