package com.proyecto.dejatuhuella.controller;

import com.proyecto.dejatuhuella.dto.PedidoDTO;
import com.proyecto.dejatuhuella.dto.PedidoRequestDTO;
import com.proyecto.dejatuhuella.model.Pedido;
import com.proyecto.dejatuhuella.service.PedidoService;
import jakarta.validation.Valid; // Para validar el DTO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // ... (otros métodos GET no cambian significativamente su firma, pero sí la data que devuelven internamente) ...
    // Obtener todos los pedidos (solo ADMINISTRADOR)
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<PedidoDTO>> obtenerTodosLosPedidos() {
        return ResponseEntity.ok(pedidoService.obtenerTodosLosPedidosDTO());
    }

    // Obtener un pedido por ID (ADMINISTRADOR o COMPRADOR propietario)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or @seguridadService.esPropietarioDelPedido(#id)")
    public ResponseEntity<?> obtenerPedidoPorId(@PathVariable Long id) {
        return pedidoService.obtenerPedidoDTOPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener pedidos de un comprador específico (ADMINISTRADOR o el mismo COMPRADOR)
    @GetMapping("/comprador/{compradorId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or @seguridadService.esUsuarioActual(#compradorId)")
    public ResponseEntity<List<PedidoDTO>> obtenerPedidosPorComprador(@PathVariable Long compradorId) {
        return ResponseEntity.ok(pedidoService.obtenerPedidosPorComprador(compradorId));
    }


    @PostMapping
    @PreAuthorize("hasRole('USUARIO') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> crearPedido(@Valid @RequestBody PedidoRequestDTO pedidoRequestDTO) {
        // En una implementación real, el compradorId se obtendría del Principal (usuario autenticado)
        // si el rol es COMPRADOR, para evitar que un comprador cree pedidos para otro.
        // Si es ADMIN, podría especificar el compradorId.
        try {
            Pedido nuevoPedido = pedidoService.crearPedido(pedidoRequestDTO);
            PedidoDTO pedidoDTO = pedidoService.obtenerPedidoDTOPorId(nuevoPedido.getId())
                    .orElseThrow(() -> new RuntimeException("Error al obtener el pedido creado"));
            return ResponseEntity.status(HttpStatus.CREATED).body(pedidoDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('USUARIO') or hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> actualizarEstadoPedido(@PathVariable Long id, @RequestParam String estado) {
        try {
            Pedido pedidoActualizado = pedidoService.actualizarEstadoPedido(id, estado);
            PedidoDTO pedidoDTO = pedidoService.obtenerPedidoDTOPorId(pedidoActualizado.getId())
                    .orElseThrow(() -> new RuntimeException("Error al obtener el pedido actualizado"));
            return ResponseEntity.ok(pedidoDTO);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<?> eliminarPedido(@PathVariable Long id) {
        try {
            pedidoService.eliminarPedido(id);
            return ResponseEntity.ok().body("Pedido eliminado con éxito.");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("isAuthenticated() and @seguridadService.esPropietarioDelPedido(#id)")
    public String cancelarPedido(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            pedidoService.actualizarEstadoPedido(id, "CANCELADO");
            redirectAttributes.addFlashAttribute("mensaje", "Pedido cancelado con éxito");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar el pedido: " + e.getMessage());
        }
        return "redirect:/mis-compras";
    }

    @PostMapping("/{id}/estado")
    @PreAuthorize("isAuthenticated() and @seguridadService.esVendedorEnPedido(#id)")
    public String actualizarEstadoPedido(@PathVariable Long id, @RequestParam String estado,
                                         RedirectAttributes redirectAttributes) {
        try {
            pedidoService.actualizarEstadoPedido(id, estado);
            redirectAttributes.addFlashAttribute("mensaje", "Estado del pedido actualizado con éxito");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el estado: " + e.getMessage());
        }
        return "redirect:/mis-ventas";
    }
}