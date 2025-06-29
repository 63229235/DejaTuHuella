package com.proyecto.dejatuhuella.controller;

import com.proyecto.dejatuhuella.dto.PagoRequestDTO;
import com.proyecto.dejatuhuella.model.Pedido;
import com.proyecto.dejatuhuella.service.PagoService;
import com.proyecto.dejatuhuella.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/procesar/{pedidoId}")
    @PreAuthorize("isAuthenticated()")
    public String mostrarFormularioPago(@PathVariable Long pedidoId, Model model) {
        Optional<Pedido> pedidoOpt = pedidoService.obtenerPedidoPorId(pedidoId);
        
        if (pedidoOpt.isEmpty()) {
            return "redirect:/mis-compras?error=Pedido no encontrado";
        }
        
        Pedido pedido = pedidoOpt.get();
        
        // Verificar que el pedido pertenece al usuario autenticado
        if (!pagoService.verificarPedidoPertenecaUsuario(pedido)) {
            return "redirect:/mis-compras?error=No tienes permiso para pagar este pedido";
        }
        
        // Verificar que el pedido está en estado PENDIENTE
        if (!"PENDIENTE".equals(pedido.getEstado().getNombreEstado())) {
            return "redirect:/mis-compras?error=Este pedido no está pendiente de pago";
        }
        
        model.addAttribute("pedido", pedido);
        model.addAttribute("pagoRequest", new PagoRequestDTO());
        
        return "pago/formulario";
    }
    
    @PostMapping("/procesar/{pedidoId}")
    @PreAuthorize("isAuthenticated()")
    public String procesarPago(
            @PathVariable Long pedidoId,
            @ModelAttribute PagoRequestDTO pagoRequest,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Verificar que el pedido existe antes de procesar el pago
            if (!pedidoService.existePedido(pedidoId)) {
                redirectAttributes.addFlashAttribute("error", "Pedido no encontrado. Por favor, intente nuevamente.");
                return "redirect:/mis-compras";
            }
            
            // Asegurarse de que todos los campos del DTO estén correctamente mapeados
            if (pagoRequest.getNumeroTarjeta() == null || pagoRequest.getNumeroTarjeta().isEmpty() ||
                pagoRequest.getNombreTitular() == null || pagoRequest.getNombreTitular().isEmpty() ||
                pagoRequest.getFechaVencimiento() == null || pagoRequest.getFechaVencimiento().isEmpty() ||
                pagoRequest.getCodigoSeguridad() == null || pagoRequest.getCodigoSeguridad().isEmpty() ||
                pagoRequest.getMetodoPago() == null || pagoRequest.getMetodoPago().isEmpty()) {
                
                redirectAttributes.addFlashAttribute("error", "Por favor, complete todos los campos del formulario de pago.");
                return "redirect:/pagos/procesar/" + pedidoId;
            }
            
            boolean pagoExitoso = pagoService.procesarPago(pedidoId, pagoRequest);
            
            if (pagoExitoso) {
                // Actualizar el estado del pedido a PAGADO
                pedidoService.actualizarEstadoPedido(pedidoId, "PAGADO");
                redirectAttributes.addFlashAttribute("mensaje", "¡Pago realizado con éxito!");
                return "redirect:/pagos/agradecimiento";
            } else {
                redirectAttributes.addFlashAttribute("error", "Error al procesar el pago. Por favor, intente nuevamente.");
                return "redirect:/pagos/procesar/" + pedidoId;
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar el pago: " + e.getMessage());
            return "redirect:/pagos/procesar/" + pedidoId;
        }
    }
    
    @GetMapping("/confirmacion/{pedidoId}")
    @PreAuthorize("isAuthenticated()")
    public String mostrarConfirmacionPago(@PathVariable Long pedidoId, Model model) {
        Optional<Pedido> pedidoOpt = pedidoService.obtenerPedidoPorId(pedidoId);
        
        if (pedidoOpt.isEmpty()) {
            return "redirect:/mis-compras?error=Pedido no encontrado";
        }
        
        Pedido pedido = pedidoOpt.get();
        
        // Verificar que el pedido pertenece al usuario autenticado
        if (!pagoService.verificarPedidoPertenecaUsuario(pedido)) {
            return "redirect:/mis-compras?error=No tienes permiso para ver este pedido";
        }
        
        // Redirigir a la página de agradecimiento en lugar de mostrar la confirmación
        return "redirect:/pagos/agradecimiento";
    }
}