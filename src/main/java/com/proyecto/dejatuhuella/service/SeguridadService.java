package com.proyecto.dejatuhuella.service;

import com.proyecto.dejatuhuella.model.Pedido;
import com.proyecto.dejatuhuella.model.Producto;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.repository.PedidoRepository;
import com.proyecto.dejatuhuella.repository.ProductoRepository;
import com.proyecto.dejatuhuella.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service("seguridadService") // El nombre debe coincidir si se usa con @PreAuthorize
public class SeguridadService {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Retorna el usuario actualmente autenticado, o null si no hay ninguno.
     */
    private Usuario getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        String emailUsuarioAutenticado = authentication.getName();
        return usuarioService.buscarPorEmail(emailUsuarioAutenticado).orElse(null);
    }

    /**
     * Verifica si el usuario autenticado es el mismo que el solicitado.
     */
    public boolean esUsuarioActual(Long idUsuarioSolicitado) {
        Usuario usuarioAutenticado = getUsuarioAutenticado();
        return usuarioAutenticado != null && usuarioAutenticado.getId().equals(idUsuarioSolicitado);
    }

    /**
     * Verifica si el producto pertenece al usuario autenticado (vendedor).
     */
    public boolean esPropietarioDelProducto(Long idProducto) {
        Usuario vendedorAutenticado = getUsuarioAutenticado();
        if (vendedorAutenticado == null) {
            return false;
        }
        return productoRepository.findById(idProducto)
                .map(producto -> producto.getUsuario() != null &&
                        producto.getUsuario().getId().equals(vendedorAutenticado.getId()))
                .orElse(false);
    }

    /**
     * Verifica si el pedido pertenece al usuario autenticado (comprador).
     */
    public boolean esPropietarioDelPedido(Long pedidoId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            return pedidoRepository.findById(pedidoId)
                    .map(pedido -> pedido.getUsuario().getId().equals(usuario.getId()))
                    .orElse(false);
        }
        return false;
    }

    /**
     * Verifica si el usuario autenticado es vendedor de algún producto en el pedido.
     */
    public boolean esVendedorEnPedido(Long pedidoId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            return pedidoRepository.findById(pedidoId)
                    .map(pedido -> pedido.getDetalles().stream()
                            .anyMatch(detalle -> detalle.getProducto().getUsuario().getId().equals(usuario.getId())))
                    .orElse(false);
        }
        return false;
    }
}
