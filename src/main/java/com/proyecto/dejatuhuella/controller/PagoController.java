package com.proyecto.dejatuhuella.controller;

import com.proyecto.dejatuhuella.dto.MercadoPagoResponseDTO;
import com.proyecto.dejatuhuella.dto.PagoRequestDTO;
import com.proyecto.dejatuhuella.model.Pedido;
import com.proyecto.dejatuhuella.service.MercadoPagoService;
import com.proyecto.dejatuhuella.service.PagoService;
import com.proyecto.dejatuhuella.service.PedidoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/pagos")
public class PagoController {

    private static final Logger logger = LoggerFactory.getLogger(PagoController.class);

    @Autowired
    private PagoService pagoService;

    @Autowired
    private PedidoService pedidoService;
    
    @Autowired
    private MercadoPagoService mercadoPagoService;

    @GetMapping("/procesar/{pedidoId}")
    @PreAuthorize("isAuthenticated()")
    public String mostrarFormularioPago(@PathVariable Long pedidoId, Model model) {
        try {
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
            
            // Crear preferencia de pago con Mercado Pago
            MercadoPagoResponseDTO mpResponse = pagoService.crearPreferenciaPago(pedidoId);
            
            if ("error".equals(mpResponse.getStatus())) {
                model.addAttribute("error", "Error al procesar el pago: " + mpResponse.getMessage());
                return "redirect:/mis-compras?error=" + mpResponse.getMessage();
            }
            
            model.addAttribute("pedido", pedido);
            model.addAttribute("preferenceId", mpResponse.getPreferenceId());
            model.addAttribute("pagoRequest", new PagoRequestDTO());
            
            return "pago/mercadopago";
        } catch (Exception e) {
            logger.error("Error al mostrar formulario de pago: {}", e.getMessage());
            return "redirect:/mis-compras?error=" + e.getMessage();
        }
    }
    
    @GetMapping("/procesar-legacy/{pedidoId}")
    @PreAuthorize("isAuthenticated()")
    public String mostrarFormularioPagoLegacy(@PathVariable Long pedidoId, Model model) {
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
    
    /**
     * Endpoint para recibir la respuesta exitosa de Mercado Pago
     */
    @GetMapping("/success")
    public String pagoExitoso(@RequestParam("pedido_id") Long pedidoId, 
                             @RequestParam(value = "collection_id", required = false) String collectionId,
                             @RequestParam(value = "collection_status", required = false) String collectionStatus,
                             @RequestParam(value = "payment_id", required = false) String paymentId,
                             @RequestParam(value = "status", required = false) String status,
                             @RequestParam(value = "external_reference", required = false) String externalReference,
                             @RequestParam(value = "preference_id", required = false) String preferenceId,
                             Model model) {
        
        logger.info("Pago exitoso - Pedido ID: {}, Estado: {}, Payment ID: {}", pedidoId, status, paymentId);
        
        try {
            // Actualizar el estado del pedido a PAGADO
            pedidoService.actualizarEstadoPedido(pedidoId, "PAGADO");
            
            // Actualizar el estado en Mercado Pago
            mercadoPagoService.actualizarEstadoPedido(pedidoId, "approved");
            
            return "redirect:/pagos/agradecimiento";
        } catch (Exception e) {
            logger.error("Error al procesar pago exitoso: {}", e.getMessage());
            return "redirect:/mis-compras?error=" + e.getMessage();
        }
    }
    
    /**
     * Endpoint para recibir la respuesta fallida de Mercado Pago
     */
    @GetMapping("/failure")
    public String pagoFallido(@RequestParam("pedido_id") Long pedidoId,
                             @RequestParam(value = "collection_id", required = false) String collectionId,
                             @RequestParam(value = "collection_status", required = false) String collectionStatus,
                             @RequestParam(value = "payment_id", required = false) String paymentId,
                             @RequestParam(value = "status", required = false) String status,
                             @RequestParam(value = "external_reference", required = false) String externalReference,
                             @RequestParam(value = "preference_id", required = false) String preferenceId,
                             RedirectAttributes redirectAttributes) {
        
        logger.info("Pago fallido - Pedido ID: {}, Estado: {}, Payment ID: {}", pedidoId, status, paymentId);
        
        redirectAttributes.addFlashAttribute("error", "El pago no pudo ser procesado. Por favor, intente nuevamente.");
        return "redirect:/pagos/procesar/" + pedidoId;
    }
    
    /**
     * Endpoint para recibir la respuesta pendiente de Mercado Pago
     */
    @GetMapping("/pending")
    public String pagoPendiente(@RequestParam("pedido_id") Long pedidoId,
                              @RequestParam(value = "collection_id", required = false) String collectionId,
                              @RequestParam(value = "collection_status", required = false) String collectionStatus,
                              @RequestParam(value = "payment_id", required = false) String paymentId,
                              @RequestParam(value = "status", required = false) String status,
                              @RequestParam(value = "external_reference", required = false) String externalReference,
                              @RequestParam(value = "preference_id", required = false) String preferenceId,
                              Model model) {
        
        logger.info("Pago pendiente - Pedido ID: {}, Estado: {}, Payment ID: {}", pedidoId, status, paymentId);
        
        model.addAttribute("mensaje", "Su pago está siendo procesado. Le notificaremos cuando se complete.");
        return "redirect:/mis-compras";
    }
    
    /**
     * Endpoint para recibir notificaciones de Mercado Pago (IPN - Instant Payment Notification)
     */
    @PostMapping("/webhook")
    @ResponseBody
    public ResponseEntity<String> webhookMercadoPago(
            @RequestParam(value = "id", required = false) String notificationId,
            @RequestParam(value = "topic", required = false) String topic,
            @RequestBody(required = false) String requestBody) {
        
        logger.info("Webhook recibido - Topic: {}, ID: {}", topic, notificationId);
        logger.debug("Webhook body: {}", requestBody);
        
        try {
            // Procesar la notificación
            boolean procesado = mercadoPagoService.procesarNotificacionPago(notificationId, topic);
            
            if (procesado) {
                return ResponseEntity.ok("Webhook procesado correctamente");
            } else {
                return ResponseEntity.badRequest().body("Error al procesar webhook");
            }
        } catch (Exception e) {
            logger.error("Error al procesar webhook: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}