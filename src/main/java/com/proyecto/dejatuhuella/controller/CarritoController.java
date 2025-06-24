package com.proyecto.dejatuhuella.controller;

import com.proyecto.dejatuhuella.dto.PedidoRequestDTO;
import com.proyecto.dejatuhuella.model.Pedido;
import com.proyecto.dejatuhuella.service.CarritoService;
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
@RequestMapping("/cart")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String verCarrito(Model model) {
        model.addAttribute("items", carritoService.getItemsDetallados());
        model.addAttribute("total", carritoService.getTotal());
        return "carrito";
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public String agregarAlCarrito(
            @RequestParam Long productoId,
            @RequestParam Integer cantidad,
            RedirectAttributes redirectAttributes) {

        carritoService.agregarProducto(productoId, cantidad);
        redirectAttributes.addFlashAttribute("mensaje", "Producto añadido al carrito");
        return "redirect:/cart";
    }

    @PostMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public String actualizarCarrito(
            @RequestParam Long productoId,
            @RequestParam Integer cantidad) {

        carritoService.actualizarCantidad(productoId, cantidad);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    @PreAuthorize("isAuthenticated()")
    public String eliminarDelCarrito(@RequestParam Long productoId) {
        carritoService.eliminarProducto(productoId);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    @PreAuthorize("isAuthenticated()")
    public String vaciarCarrito() {
        carritoService.vaciarCarrito();
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    @PreAuthorize("isAuthenticated()")
    public String procesarCompra(RedirectAttributes redirectAttributes) {
        // Verificar stock antes de procesar
        if (!carritoService.verificarStock()) {
            redirectAttributes.addFlashAttribute("error", "Algunos productos no tienen suficiente stock");
            return "redirect:/cart";
        }

        try {
            // Obtener el ID del usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            org.springframework.security.core.userdetails.UserDetails userDetails =
                    (org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal();

            // Buscar el usuario por su nombre de usuario (email)
            com.proyecto.dejatuhuella.model.Usuario usuario =
                    ((com.proyecto.dejatuhuella.security.CustomUserDetails)
                            org.springframework.security.core.context.SecurityContextHolder
                                    .getContext().getAuthentication().getPrincipal()).getUsuario();

            // Generar el pedido
            PedidoRequestDTO pedidoRequest = carritoService.generarPedidoRequest(usuario.getId());
            Pedido nuevoPedido = pedidoService.crearPedido(pedidoRequest);

            // Vaciar el carrito después de la compra exitosa
            carritoService.vaciarCarrito();

            // Redirigir al usuario a la página de pago
            return "redirect:/pagos/procesar/" + nuevoPedido.getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la compra: " + e.getMessage());
            return "redirect:/cart";
        }
    }
}