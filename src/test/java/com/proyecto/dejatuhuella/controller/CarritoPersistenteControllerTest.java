package com.proyecto.dejatuhuella.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.proyecto.dejatuhuella.dto.PedidoRequestDTO;
import com.proyecto.dejatuhuella.model.Pedido;
import com.proyecto.dejatuhuella.model.Producto;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.security.CustomUserDetails;
import com.proyecto.dejatuhuella.service.CarritoPersistentService;
import com.proyecto.dejatuhuella.service.PedidoService;


@ExtendWith(MockitoExtension.class)
public class CarritoPersistenteControllerTest {

    @Mock
    private CarritoPersistentService carritoPersistentService;

    @Mock
    private PedidoService pedidoService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails userDetails;

    @InjectMocks
    private CarritoPersistenteController controller;

    private MockMvc mockMvc;
    private Usuario usuario;
    private Producto producto;
    private List<Map<String, Object>> itemsDetallados;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

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

        // Configurar items detallados
        itemsDetallados = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("producto", producto);
        item.put("cantidad", 2);
        item.put("subtotal", new BigDecimal("59.98"));
        itemsDetallados.add(item);

        // Configurar el contexto de seguridad simulado
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Debería mostrar el carrito")
    void verCarrito() throws Exception {
        // Configurar mocks
        when(carritoPersistentService.getItemsDetallados()).thenReturn(itemsDetallados);
        when(carritoPersistentService.getTotal()).thenReturn(new BigDecimal("59.98"));

        // Ejecutar
        String viewName = controller.verCarrito(model);

        // Verificar
        assertEquals("carrito", viewName);
        verify(model).addAttribute("items", itemsDetallados);
        verify(model).addAttribute("total", new BigDecimal("59.98"));
    }

    @Test
    @DisplayName("Debería manejar errores al mostrar el carrito")
    void verCarritoConError() throws Exception {
        // Configurar mocks para simular un error
        when(carritoPersistentService.getItemsDetallados()).thenThrow(new RuntimeException("Error de prueba"));

        // Ejecutar
        String viewName = controller.verCarrito(model);

        // Verificar
        assertEquals("carrito", viewName);
        verify(model).addAttribute(eq("error"), anyString());
        verify(model).addAttribute("items", List.of());
        verify(model).addAttribute(eq("total"), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Debería agregar un producto al carrito")
    void agregarAlCarrito() throws Exception {
        // Ejecutar
        String viewName = controller.agregarAlCarrito(1L, 2, redirectAttributes);

        // Verificar
        assertEquals("redirect:/carrito", viewName);
        verify(carritoPersistentService).agregarProducto(1L, 2);
        verify(redirectAttributes).addFlashAttribute(eq("mensaje"), anyString());
    }

    @Test
    @DisplayName("Debería manejar errores al agregar al carrito")
    void agregarAlCarritoConError() throws Exception {
        // Configurar mocks para simular un error
        doThrow(new RuntimeException("Error de prueba")).when(carritoPersistentService).agregarProducto(anyLong(), anyInt());

        // Ejecutar
        String viewName = controller.agregarAlCarrito(1L, 2, redirectAttributes);

        // Verificar
        assertEquals("redirect:/carrito", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
    }

    @Test
    @DisplayName("Debería actualizar la cantidad de un producto en el carrito")
    void actualizarCarrito() throws Exception {
        // Ejecutar
        String viewName = controller.actualizarCarrito(1L, 3);

        // Verificar
        assertEquals("redirect:/carrito", viewName);
        verify(carritoPersistentService).actualizarCantidad(1L, 3);
    }

    @Test
    @DisplayName("Debería eliminar un producto del carrito")
    void eliminarDelCarrito() throws Exception {
        // Ejecutar
        String viewName = controller.eliminarDelCarrito(1L);

        // Verificar
        assertEquals("redirect:/carrito", viewName);
        verify(carritoPersistentService).eliminarProducto(1L);
    }

    @Test
    @DisplayName("Debería vaciar el carrito")
    void vaciarCarrito() throws Exception {
        // Ejecutar
        String viewName = controller.vaciarCarrito();

        // Verificar
        assertEquals("redirect:/carrito", viewName);
        verify(carritoPersistentService).vaciarCarrito();
    }

    @Test
    @DisplayName("Debería procesar la compra correctamente")
    void procesarCompra() throws Exception {
        // Configurar mocks
        when(carritoPersistentService.verificarStock()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsuario()).thenReturn(usuario);

        PedidoRequestDTO pedidoRequest = new PedidoRequestDTO();
        when(carritoPersistentService.generarPedidoRequest(1L)).thenReturn(pedidoRequest);

        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setId(1L);
        when(pedidoService.crearPedido(pedidoRequest)).thenReturn(nuevoPedido);

        // Ejecutar
        String viewName = controller.procesarCompra(redirectAttributes);

        // Verificar
        assertEquals("redirect:/pagos/procesar/1", viewName);
        verify(carritoPersistentService).verificarStock();
        verify(carritoPersistentService).generarPedidoRequest(1L);
        verify(pedidoService).crearPedido(pedidoRequest);
        verify(carritoPersistentService).vaciarCarrito();
    }

    @Test
    @DisplayName("Debería manejar falta de stock al procesar la compra")
    void procesarCompraConStockInsuficiente() throws Exception {
        // Configurar mocks
        when(carritoPersistentService.verificarStock()).thenReturn(false);

        // Ejecutar
        String viewName = controller.procesarCompra(redirectAttributes);

        // Verificar
        assertEquals("redirect:/carrito", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
        verify(carritoPersistentService, never()).generarPedidoRequest(anyLong());
        verify(pedidoService, never()).crearPedido(any());
        verify(carritoPersistentService, never()).vaciarCarrito();
    }

    @Test
    @DisplayName("Debería manejar errores al procesar la compra")
    void procesarCompraConError() throws Exception {
        // Configurar mocks
        when(carritoPersistentService.verificarStock()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsuario()).thenReturn(usuario);

        // Simular error al generar pedido
        when(carritoPersistentService.generarPedidoRequest(1L)).thenThrow(new RuntimeException("Error de prueba"));

        // Ejecutar
        String viewName = controller.procesarCompra(redirectAttributes);

        // Verificar
        assertEquals("redirect:/carrito", viewName);
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
        verify(carritoPersistentService, never()).vaciarCarrito();
    }
}