package com.proyecto.dejatuhuella.controller;

import com.proyecto.dejatuhuella.dto.ProductoRequestDTO;
import com.proyecto.dejatuhuella.model.Producto;
import com.proyecto.dejatuhuella.repository.UsuarioRepository;
import com.proyecto.dejatuhuella.service.FileStorageService;
import com.proyecto.dejatuhuella.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.repository.UsuarioRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UsuarioRepository usuarioRepository;

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

    @PostMapping
    @PreAuthorize("hasRole('USUARIO') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> crearProducto(
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam BigDecimal precio,
            @RequestParam Integer stock,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) MultipartFile imagen) {
        try {
            // Obtener el usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Crear el DTO manualmente
            ProductoRequestDTO productoRequestDTO = new ProductoRequestDTO();
            productoRequestDTO.setNombre(nombre);
            productoRequestDTO.setDescripcion(descripcion);
            productoRequestDTO.setPrecio(precio);
            productoRequestDTO.setStock(stock);
            productoRequestDTO.setUsuarioId(usuario.getId()); // Usar el ID del usuario autenticado
            productoRequestDTO.setCategoriaId(categoriaId);

            // Procesar la imagen si existe
            if (imagen != null && !imagen.isEmpty()) {
                String imagenUrl = fileStorageService.storeFile(imagen);
                productoRequestDTO.setImagenUrl(imagenUrl);
            } else {
                // URL de imagen por defecto
                productoRequestDTO.setImagenUrl("/images/producto-default.jpg");
            }

            Producto nuevoProducto = productoService.crearProducto(productoRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Actualizar un producto (solo USUARIO propietario o ADMINISTRADOR)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or (hasRole('USUARIO') and @seguridadService.esPropietarioDelProducto(#id))")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam BigDecimal precio,
            @RequestParam Integer stock,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) MultipartFile imagen) {
        try {
            // Obtener el producto existente
            Producto productoExistente = productoService.obtenerProductoPorId(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

            // Crear el DTO manualmente
            ProductoRequestDTO productoRequestDTO = new ProductoRequestDTO();
            productoRequestDTO.setNombre(nombre);
            productoRequestDTO.setDescripcion(descripcion);
            productoRequestDTO.setPrecio(precio);
            productoRequestDTO.setStock(stock);
            productoRequestDTO.setCategoriaId(categoriaId);
            productoRequestDTO.setUsuarioId(productoExistente.getUsuario().getId());

            // Procesar la imagen si existe
            if (imagen != null && !imagen.isEmpty()) {
                String imagenUrl = fileStorageService.storeFile(imagen);
                productoRequestDTO.setImagenUrl(imagenUrl);
            } else {
                // Mantener la URL de imagen existente
                productoRequestDTO.setImagenUrl(productoExistente.getImagenUrl());
            }

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
    @PreAuthorize("hasRole('ADMINISTRADOR') or (hasRole('USUARIO') and @seguridadService.esPropietarioDelProducto(#id))")
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

    // Cambiar estado de un producto (solo ADMINISTRADOR)
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> cambiarEstadoProducto(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> estadoMap) {
        try {
            Boolean activo = estadoMap.get("activo");
            Producto producto = productoService.cambiarEstadoProducto(id, activo);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Obtener productos por categoría
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<Producto>> obtenerProductosPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(productoService.obtenerProductosPorCategoria(categoriaId));
    }

    // Buscar productos por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarProductos(@RequestParam String query) {
        return ResponseEntity.ok(productoService.buscarProductos(query));
    }
}