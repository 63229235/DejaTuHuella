package com.proyecto.dejatuhuella.controller;

import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.service.CarritoService;
import com.proyecto.dejatuhuella.service.CategoriaService;
import com.proyecto.dejatuhuella.service.PedidoService;
import com.proyecto.dejatuhuella.service.ProductoService;
import com.proyecto.dejatuhuella.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UsuarioService usuarioService;

    @ModelAttribute("cartCount")
    public Integer cartCount() {
        // Solo contamos si el usuario está autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
            return carritoService.getCantidadTotal();
        }
        return 0;
    }

    @GetMapping("/")
    public String home() {
        return "home"; // Devuelve el nombre de la plantilla HTML (home.html)
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro"; // Devuelve el nombre de la plantilla HTML (registro.html)
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Devuelve el nombre de la plantilla HTML (login.html)
    }

    @GetMapping("/panel-control")
    public String panelControl(Model model) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
            // Cargar productos del usuario si es vendedor
            model.addAttribute("productos", productoService.obtenerProductosDelVendedor());

            // Cargar pedidos del usuario si es comprador
            model.addAttribute("pedidos", pedidoService.obtenerPedidosDelUsuario());

            // Cargar ventas del usuario si es vendedor
            model.addAttribute("ventas", pedidoService.obtenerVentasDelUsuario());

            // Cargar categorías para el formulario de productos
            model.addAttribute("categorias", categoriaService.obtenerTodasLasCategorias());
        }

        return "panel-control"; // Devuelve el nombre de la plantilla HTML (panel-control.html)
    }

    @GetMapping("/categorias")
    public String categorias(Model model) {
        model.addAttribute("categorias", categoriaService.obtenerTodasLasCategorias());
        return "categorias";
    }

    @GetMapping("/categoria/{id}")
    public String productosPorCategoria(@PathVariable Long id, Model model) {
        model.addAttribute("categoria", categoriaService.obtenerCategoriaPorId(id).orElse(null));
        model.addAttribute("productos", productoService.obtenerProductosPorCategoria(id));
        return "productos-categoria";
    }

    @GetMapping("/productos/{id}")
    public String detalleProducto(@PathVariable Long id, Model model) {
        productoService.obtenerProductoPorId(id)
                .ifPresent(producto -> model.addAttribute("producto", producto));
        return "producto-detalle";
    }

    @GetMapping("/mis-compras")
    @PreAuthorize("isAuthenticated()")
    public String misCompras(Model model) {
        model.addAttribute("pedidos", pedidoService.obtenerPedidosDelUsuario());
        return "mis-compras";
    }

    @GetMapping("/mis-ventas")
    @PreAuthorize("isAuthenticated()")
    public String misVentas(Model model) {
        model.addAttribute("ventas", pedidoService.obtenerVentasDelUsuario());
        return "mis-ventas";
    }
}