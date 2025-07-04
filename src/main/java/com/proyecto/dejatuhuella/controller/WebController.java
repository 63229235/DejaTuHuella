package com.proyecto.dejatuhuella.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.proyecto.dejatuhuella.model.Categoria;
import com.proyecto.dejatuhuella.model.Producto;
import com.proyecto.dejatuhuella.service.CategoriaService;
import com.proyecto.dejatuhuella.service.PedidoService;
import com.proyecto.dejatuhuella.service.ProductoService;
import com.proyecto.dejatuhuella.service.ResenaService;
import com.proyecto.dejatuhuella.service.UsuarioService;

@Controller
public class WebController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ResenaService resenaService;


    @Autowired
    private com.proyecto.dejatuhuella.service.CarritoPersistentService carritoPersistentService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UsuarioService usuarioService;

    @ModelAttribute("cartCount")
    public Integer cartCount() {
        // Solo contamos si el usuario está autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            try {
                // Usar el servicio de carrito persistente en lugar del servicio de sesión
                return carritoPersistentService.getCantidadTotal();
            } catch (Exception e) {
                System.err.println("Error al obtener cantidad del carrito: " + e.getMessage());
            }
        }
        return 0;
    }

    @GetMapping("/")
    public String home(Model model) {
        // Cargar categorías destacadas
        model.addAttribute("categoriasDestacadas", categoriaService.obtenerTodasLasCategorias());

        // Cargar productos destacados aleatorios
        model.addAttribute("productosDestacados", productoService.obtenerProductosDestacadosAleatorios());

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
    @PreAuthorize("isAuthenticated()")
    public String panelControl(Model model) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !("anonymousUser".equals(auth.getPrincipal()))) {
            try {
                // Cargar productos del usuario si es vendedor
                model.addAttribute("productos", productoService.obtenerProductosDelVendedor());
            } catch (Exception e) {
                model.addAttribute("errorProductos", "Error al cargar productos: " + e.getMessage());
                model.addAttribute("productos", List.of());
            }

            try {
                // Cargar pedidos del usuario si es comprador
                model.addAttribute("pedidos", pedidoService.obtenerPedidosDelUsuario());
            } catch (Exception e) {
                System.err.println("Error al cargar pedidos: " + e.getMessage());
                e.printStackTrace();
                model.addAttribute("errorPedidos", "Error al cargar pedidos: " + e.getMessage());
                model.addAttribute("pedidos", List.of());
            }

            try {
                // Cargar ventas del usuario si es vendedor
                model.addAttribute("ventas", pedidoService.obtenerVentasDelUsuario());
            } catch (Exception e) {
                model.addAttribute("errorVentas", "Error al cargar ventas: " + e.getMessage());
                model.addAttribute("ventas", List.of());
            }

            try {
                // Cargar categorías para el formulario de productos
                model.addAttribute("categorias", categoriaService.obtenerTodasLasCategorias());
            } catch (Exception e) {
                model.addAttribute("errorCategorias", "Error al cargar categorías: " + e.getMessage());
                model.addAttribute("categorias", List.of());
            }
        }

        return "panel-control"; // Devuelve el nombre de la plantilla HTML (panel-control.html)
    }

    @GetMapping("/categorias")
    public String categorias(Model model) {
        model.addAttribute("categorias", categoriaService.obtenerTodasLasCategorias());
        return "categorias";
    }

    @GetMapping("/categoria/{id}")
    public String productosPorCategoria(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        // Obtener la categoría y verificar si existe
        Optional<Categoria> categoriaOptional = categoriaService.obtenerCategoriaPorId(id);
        if (categoriaOptional.isEmpty()) {
            // Si la categoría no existe, redirigir a la página de categorías con un mensaje
            redirectAttributes.addFlashAttribute("errorMessage", "La categoría solicitada no existe");
            return "redirect:/categorias";
        }

        // Si la categoría existe, mostrar sus productos
        model.addAttribute("categoria", categoriaOptional.get());
        model.addAttribute("productos", productoService.obtenerProductosPorCategoria(id));
        return "productos-categoria";
    }

    @GetMapping("/productos/{id}")
    public String detalleProducto(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Producto> productoOptional = productoService.obtenerProductoPorId(id);
        if (productoOptional.isEmpty()) {
            // Si el producto no existe, redirigir a la página principal con un mensaje
            redirectAttributes.addFlashAttribute("errorMessage", "El producto solicitado no existe");
            return "redirect:/";
        }

        // Si el producto existe, mostrar sus detalles
        model.addAttribute("producto", productoOptional.get());

        // Obtener las reseñas del producto
        model.addAttribute("resenas", resenaService.obtenerResenasPorProducto(id));

        // Verificar si el usuario ya dejó una reseña
        model.addAttribute("yaDejoResena", resenaService.usuarioYaDejoResena(id));

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
