package com.proyecto.dejatuhuella.controller;

import com.proyecto.dejatuhuella.model.Resena;
import com.proyecto.dejatuhuella.service.ResenaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resenas")
public class ResenaController {

    @Autowired
    private ResenaService resenaService;

    /**
     * Obtiene todas las reseñas de un producto
     */
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<Resena>> obtenerResenasPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(resenaService.obtenerResenasPorProducto(productoId));
    }

    /**
     * Verifica si el usuario ya ha dejado una reseña para un producto
     */
    @GetMapping("/verificar/{productoId}")
    public ResponseEntity<Map<String, Boolean>> verificarResena(@PathVariable Long productoId) {
        boolean yaDejoResena = resenaService.usuarioYaDejoResena(productoId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("yaDejoResena", yaDejoResena);
        return ResponseEntity.ok(response);
    }

    /**
     * Crea una nueva reseña
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> crearResena(@RequestBody Map<String, Object> request) {
        try {
            Long productoId = Long.parseLong(request.get("productoId").toString());
            Integer calificacion = Integer.parseInt(request.get("calificacion").toString());
            String comentario = request.get("comentario").toString();

            Resena resena = resenaService.crearResena(productoId, calificacion, comentario);
            return ResponseEntity.ok(resena);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Elimina una reseña
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> eliminarResena(@PathVariable Long id) {
        try {
            resenaService.eliminarResena(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Reseña eliminada con éxito");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}