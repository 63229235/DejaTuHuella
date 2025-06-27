package com.proyecto.dejatuhuella.controller;

import com.proyecto.dejatuhuella.dto.PedidoRequestDTO;
import com.proyecto.dejatuhuella.model.Pedido;
import com.proyecto.dejatuhuella.service.CarritoPersistentService;
import com.proyecto.dejatuhuella.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carrito")
public class CarritoPersistenteController {

    @Autowired
    private CarritoPersistentService carritoPersistentService;

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String verCarrito(Model model) {
        model.addAttribute("items", carritoPersistentService.getItemsDetallados());
        model.addAttribute("total", carritoPersistentService.getTotal());
        return "carrito";
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public String agregarAlCarrito(
            @RequestParam Long productoId,
            @RequestParam Integer cantidad,
            RedirectAttributes redirectAttributes) {

        try {
            carritoPersistentService.agregarProducto(productoId, cantidad);
            redirectAttributes.addFlashAttribute("mensaje", "Producto añadido al carrito");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al agregar al carrito: " + e.getMessage());
        }
        return "redirect:/carrito";
    }

    @PostMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public String actualizarCarrito(
            @RequestParam Long productoId,
            @RequestParam Integer cantidad) {

        try {
            carritoPersistentService.actualizarCantidad(productoId, cantidad);
        } catch (Exception e) {
            // Manejar error
        }
        return "redirect:/carrito";
    }

    @PostMapping("/remove")
    @PreAuthorize("isAuthenticated()")
    public String eliminarDelCarrito(@RequestParam Long productoId) {
        try {
            carritoPersistentService.eliminarProducto(productoId);
        } catch (Exception e) {
            // Manejar error
        }
        return "redirect:/carrito";
    }

    @PostMapping("/clear")
    @PreAuthorize("isAuthenticated()")
    public String vaciarCarrito() {
        try {
            carritoPersistentService.vaciarCarrito();
        } catch (Exception e) {
            // Manejar error
        }
        return "redirect:/carrito";
    }

    @PostMapping("/checkout")
    @PreAuthorize("isAuthenticated()")
    public String procesarCompra(RedirectAttributes redirectAttributes) {
        // Verificar stock antes de procesar
        if (!carritoPersistentService.verificarStock()) {
            redirectAttributes.addFlashAttribute("error", "Algunos productos no tienen suficiente stock");
            return "redirect:/carrito";
        }

        try {
            // Obtener el usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            com.proyecto.dejatuhuella.security.CustomUserDetails userDetails =
                    (com.proyecto.dejatuhuella.security.CustomUserDetails) auth.getPrincipal();
            com.proyecto.dejatuhuella.model.Usuario usuario = userDetails.getUsuario();

            // Generar el pedido
            PedidoRequestDTO pedidoRequest = carritoPersistentService.generarPedidoRequest(usuario.getId());
            Pedido nuevoPedido = pedidoService.crearPedido(pedidoRequest);

            // Vaciar el carrito después de la compra exitosa
            carritoPersistentService.vaciarCarrito();

            // Redirigir al usuario a la página de pago
            return "redirect:/pagos/procesar/" + nuevoPedido.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la compra: " + e.getMessage());
            return "redirect:/carrito";
        }
    }
}