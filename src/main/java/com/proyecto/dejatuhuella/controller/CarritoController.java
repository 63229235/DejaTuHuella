package com.proyecto.dejatuhuella.controller;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/cart")
public class CarritoController {

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String verCarrito() {
        // Redirigir a la nueva ruta del carrito persistente
        return "redirect:/carrito";
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public String agregarAlCarrito(
            @RequestParam Long productoId,
            @RequestParam Integer cantidad,
            RedirectAttributes redirectAttributes) {

        // Redirigir a la nueva ruta del carrito persistente
        return "redirect:/carrito/add?productoId=" + productoId + "&cantidad=" + cantidad;
    }

    @PostMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public String actualizarCarrito(
            @RequestParam Long productoId,
            @RequestParam Integer cantidad) {

        // Redirigir a la nueva ruta del carrito persistente
        return "redirect:/carrito/update?productoId=" + productoId + "&cantidad=" + cantidad;
    }

    @PostMapping("/remove")
    @PreAuthorize("isAuthenticated()")
    public String eliminarDelCarrito(@RequestParam Long productoId) {
        // Redirigir a la nueva ruta del carrito persistente
        return "redirect:/carrito/remove?productoId=" + productoId;
    }

    @PostMapping("/clear")
    @PreAuthorize("isAuthenticated()")
    public String vaciarCarrito() {
        // Redirigir a la nueva ruta del carrito persistente
        return "redirect:/carrito/clear";
    }

    @PostMapping("/checkout")
    @PreAuthorize("isAuthenticated()")
    public String procesarCompra() {
        // Redirigir a la nueva ruta del carrito persistente
        return "redirect:/carrito/checkout";
    }
}