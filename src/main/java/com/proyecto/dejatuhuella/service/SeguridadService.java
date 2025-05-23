package com.proyecto.dejatuhuella.service;

import com.proyecto.dejatuhuella.model.Pedido;
import com.proyecto.dejatuhuella.model.Producto;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.repository.PedidoRepository;
import com.proyecto.dejatuhuella.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("seguridadService") // El nombre debe coincidir si se usa con @PreAuthorize
public class SeguridadService {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

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
        return usuarioService.buscarPorEmail(emailUsuarioAutenticado);
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
                .map(producto -> producto.getVendedor() != null &&
                        producto.getVendedor().getId().equals(vendedorAutenticado.getId()))
                .orElse(false);
    }

    /**
     * Verifica si el pedido pertenece al usuario autenticado (comprador).
     */
    public boolean esPropietarioDelPedido(Long idPedido) {
        Usuario compradorAutenticado = getUsuarioAutenticado();
        if (compradorAutenticado == null) {
            return false;
        }
        return pedidoRepository.findById(idPedido)
                .map(pedido -> pedido.getComprador() != null &&
                        pedido.getComprador().getId().equals(compradorAutenticado.getId()))
                .orElse(false);
    }
}
