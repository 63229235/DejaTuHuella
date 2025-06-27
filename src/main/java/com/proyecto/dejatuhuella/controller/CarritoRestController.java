package com.proyecto.dejatuhuella.controller;

import com.proyecto.dejatuhuella.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CarritoRestController {
    // Este controlador está obsoleto, se mantiene para compatibilidad
    // Usar CarritoPersistenteRestController con la ruta /api/carrito en su lugar

    @Autowired
    private CarritoService carritoService;
    
    @Autowired
    private com.proyecto.dejatuhuella.service.CarritoPersistentService carritoPersistentService;

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> agregarAlCarrito(@RequestBody Map<String, Object> request) {
        Long productoId = Long.valueOf(request.get("productoId").toString());
        Integer cantidad = Integer.valueOf(request.get("cantidad").toString());
        
        try {
            // Usar el servicio de carrito persistente en lugar del servicio de sesión
            carritoPersistentService.agregarProducto(productoId, cantidad);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("cartCount", carritoPersistentService.getCantidadTotal());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}