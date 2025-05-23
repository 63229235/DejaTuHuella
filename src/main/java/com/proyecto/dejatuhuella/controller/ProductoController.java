package com.proyecto.dejatuhuella.controller;

import com.proyecto.dejatuhuella.dto.ProductoRequestDTO;
import com.proyecto.dejatuhuella.model.Producto;
import com.proyecto.dejatuhuella.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // Obtener todos los productos (público o para cualquier rol autenticado)
    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodosLosProductos() {
        return ResponseEntity.ok(productoService.obtenerTodosLosProductos());
    }

    // Obtener un producto por ID (público o para cualquier rol autenticado)
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        return productoService.obtenerProductoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear un nuevo producto (solo VENDEDOR o ADMINISTRADOR)
    @PostMapping
    @PreAuthorize("hasRole('VENDEDOR') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> crearProducto(@Valid @RequestBody ProductoRequestDTO productoRequestDTO) {
        // En una implementación más robusta, el vendedorId se tomaría del usuario autenticado (Principal)
        // y no se pasaría en el DTO, o se validaría que el vendedorId del DTO coincida con el autenticado si es VENDEDOR.
        // Por ahora, confiamos en el DTO y la autorización de rol.
        try {
            Producto nuevoProducto = productoService.crearProducto(productoRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
        } catch (RuntimeException e) {
            // Considerar un manejo de excepciones más específico
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Actualizar un producto (solo VENDEDOR propietario o ADMINISTRADOR)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or (hasRole('VENDEDOR') and @seguridadService.esPropietarioDelProducto(#id))")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoRequestDTO productoRequestDTO) {
        try {
            Producto productoActualizado = productoService.actualizarProducto(id, productoRequestDTO);
            return ResponseEntity.ok(productoActualizado);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Eliminar un producto (solo VENDEDOR propietario o ADMINISTRADOR)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or (hasRole('VENDEDOR') and @seguridadService.esPropietarioDelProducto(#id))")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.ok().body("Producto eliminado con éxito.");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}