package com.proyecto.dejatuhuella.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.proyecto.dejatuhuella.service.CarritoPersistentService;

public class CarritoPersistenteRestControllerTest {

    @Mock
    private CarritoPersistentService carritoPersistentService;

    @InjectMocks
    private CarritoPersistenteRestController controller;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAgregarAlCarrito_Success() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("productoId", 1L);
        request.put("cantidad", 2);

        when(carritoPersistentService.getCantidadTotal()).thenReturn(2);
        doNothing().when(carritoPersistentService).agregarProducto(anyLong(), anyInt());

        // Act
        ResponseEntity<Map<String, Object>> response = controller.agregarAlCarrito(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().get("success"));
        assertEquals(2, response.getBody().get("cartCount"));
        verify(carritoPersistentService).agregarProducto(1L, 2);
    }

    @Test
    public void testAgregarAlCarrito_Error() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("productoId", 1L);
        request.put("cantidad", 2);

        doThrow(new RuntimeException("Error al agregar producto")).when(carritoPersistentService).agregarProducto(anyLong(), anyInt());

        // Act
        ResponseEntity<Map<String, Object>> response = controller.agregarAlCarrito(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(false, response.getBody().get("success"));
        assertEquals("Error al agregar producto", response.getBody().get("message"));
    }

    @Test
    public void testObtenerCantidadCarrito() {
        // Arrange
        when(carritoPersistentService.getCantidadTotal()).thenReturn(5);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.obtenerCantidadCarrito();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5, response.getBody().get("count"));
    }

    @Test
    public void testObtenerCarrito() {
        // Arrange
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", 1L);
        item.put("nombre", "Producto Test");
        items.add(item);

        when(carritoPersistentService.getItemsDetallados()).thenReturn(items);
        when(carritoPersistentService.getTotal()).thenReturn(BigDecimal.valueOf(100.0));

        // Act
        ResponseEntity<Map<String, Object>> response = controller.obtenerCarrito();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(items, response.getBody().get("items"));
        assertEquals(BigDecimal.valueOf(100.0), response.getBody().get("total")); // <-- Cambia aquí
    }
    @Test
    public void testActualizarCarrito_Success() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("productoId", 1L);
        request.put("cantidad", 3);

        when(carritoPersistentService.getCantidadTotal()).thenReturn(3);
        doNothing().when(carritoPersistentService).actualizarCantidad(anyLong(), anyInt());

        // Act
        ResponseEntity<Map<String, Object>> response = controller.actualizarCarrito(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().get("success"));
        assertEquals(3, response.getBody().get("cartCount"));
        verify(carritoPersistentService).actualizarCantidad(1L, 3);
    }

    @Test
    public void testActualizarCarrito_Error() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("productoId", 1L);
        request.put("cantidad", 3);

        doThrow(new RuntimeException("Error al actualizar carrito")).when(carritoPersistentService).actualizarCantidad(anyLong(), anyInt());

        // Act
        ResponseEntity<Map<String, Object>> response = controller.actualizarCarrito(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(false, response.getBody().get("success"));
        assertEquals("Error al actualizar carrito", response.getBody().get("message"));
    }

    @Test
    public void testEliminarDelCarrito_Success() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("productoId", 1L);

        when(carritoPersistentService.getCantidadTotal()).thenReturn(0);
        doNothing().when(carritoPersistentService).eliminarProducto(anyLong());

        // Act
        ResponseEntity<Map<String, Object>> response = controller.eliminarDelCarrito(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().get("success"));
        assertEquals(0, response.getBody().get("cartCount"));
        verify(carritoPersistentService).eliminarProducto(1L);
    }

    @Test
    public void testEliminarDelCarrito_Error() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("productoId", 1L);

        doThrow(new RuntimeException("Error al eliminar producto")).when(carritoPersistentService).eliminarProducto(anyLong());

        // Act
        ResponseEntity<Map<String, Object>> response = controller.eliminarDelCarrito(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(false, response.getBody().get("success"));
        assertEquals("Error al eliminar producto", response.getBody().get("message"));
    }

    @Test
    public void testVaciarCarrito_Success() {
        // Arrange
        doNothing().when(carritoPersistentService).vaciarCarrito();

        // Act
        ResponseEntity<Map<String, Object>> response = controller.vaciarCarrito();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().get("success"));
        verify(carritoPersistentService).vaciarCarrito();
    }

    @Test
    public void testVaciarCarrito_Error() {
        // Arrange
        doThrow(new RuntimeException("Error al vaciar carrito")).when(carritoPersistentService).vaciarCarrito();

        // Act
        ResponseEntity<Map<String, Object>> response = controller.vaciarCarrito();

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(false, response.getBody().get("success"));
        assertEquals("Error al vaciar carrito", response.getBody().get("message"));
    }
}