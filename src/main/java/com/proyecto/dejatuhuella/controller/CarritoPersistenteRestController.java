package com.proyecto.dejatuhuella.controller;

import com.proyecto.dejatuhuella.service.CarritoPersistentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "*")
public class CarritoPersistenteRestController {

    @Autowired
    private CarritoPersistentService carritoPersistentService;

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> agregarAlCarrito(@RequestBody Map<String, Object> request) {
        Long productoId = Long.valueOf(request.get("productoId").toString());
        Integer cantidad = Integer.valueOf(request.get("cantidad").toString());
        
        try {
            carritoPersistentService.agregarProducto(productoId, cantidad);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto agregado al carrito");
            response.put("cartCount", carritoPersistentService.getCantidadTotal());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> obtenerCantidadCarrito() {
        Map<String, Object> response = new HashMap<>();
        response.put("count", carritoPersistentService.getCantidadTotal());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> obtenerCarrito() {
        Map<String, Object> response = new HashMap<>();
        response.put("items", carritoPersistentService.getItemsDetallados());
        response.put("total", carritoPersistentService.getTotal());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> actualizarCarrito(@RequestBody Map<String, Object> request) {
        Long productoId = Long.valueOf(request.get("productoId").toString());
        Integer cantidad = Integer.valueOf(request.get("cantidad").toString());
        
        try {
            carritoPersistentService.actualizarCantidad(productoId, cantidad);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Carrito actualizado");
            response.put("cartCount", carritoPersistentService.getCantidadTotal());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/remove")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> eliminarDelCarrito(@RequestBody Map<String, Object> request) {
        Long productoId = Long.valueOf(request.get("productoId").toString());
        
        try {
            carritoPersistentService.eliminarProducto(productoId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto eliminado del carrito");
            response.put("cartCount", carritoPersistentService.getCantidadTotal());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/clear")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> vaciarCarrito() {
        try {
            carritoPersistentService.vaciarCarrito();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Carrito vaciado");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}