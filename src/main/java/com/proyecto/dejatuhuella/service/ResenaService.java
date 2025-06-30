package com.proyecto.dejatuhuella.service;

import com.proyecto.dejatuhuella.model.Producto;
import com.proyecto.dejatuhuella.model.Resena;
import com.proyecto.dejatuhuella.model.Usuario;
import com.proyecto.dejatuhuella.repository.ProductoRepository;
import com.proyecto.dejatuhuella.repository.ResenaRepository;
import com.proyecto.dejatuhuella.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ResenaService {

    @Autowired
    private ResenaRepository resenaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Obtiene todas las reseñas de un producto
     */
    public List<Resena> obtenerResenasPorProducto(Long productoId) {
        return resenaRepository.findByProductoIdOrderByFechaCreacionDesc(productoId);
    }

    /**
     * Crea una nueva reseña para un producto
     */
    public Resena crearResena(Long productoId, Integer calificacion, String comentario) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar si el producto existe
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar si el usuario ya ha dejado una reseña para este producto
        boolean yaExisteResena = resenaRepository.existsByUsuarioIdAndProductoId(usuario.getId(), productoId);
        if (yaExisteResena) {
            throw new RuntimeException("Ya has dejado una reseña para este producto");
        }

        // Crear la nueva reseña
        Resena resena = new Resena();
        resena.setProducto(producto);
        resena.setUsuario(usuario);
        resena.setCalificacion(calificacion);
        resena.setComentario(comentario);
        resena.setFechaCreacion(LocalDateTime.now());

        return resenaRepository.save(resena);
    }

    /**
     * Elimina una reseña
     */
    public void eliminarResena(Long resenaId) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar si la reseña existe
        Optional<Resena> resenaOpt = resenaRepository.findById(resenaId);
        if (resenaOpt.isEmpty()) {
            throw new RuntimeException("Reseña no encontrada");
        }

        Resena resena = resenaOpt.get();

        // Verificar si el usuario es el autor de la reseña o es administrador
        boolean esAutor = resena.getUsuario().getId().equals(usuario.getId());
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

        if (!esAutor && !esAdmin) {
            throw new RuntimeException("No tienes permiso para eliminar esta reseña");
        }

        resenaRepository.delete(resena);
    }

    /**
     * Verifica si un usuario ya ha dejado una reseña para un producto
     */
    public boolean usuarioYaDejoResena(Long productoId) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return false; // Usuario no autenticado
        }

        String email = auth.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return false;
        }

        return resenaRepository.existsByUsuarioIdAndProductoId(usuarioOpt.get().getId(), productoId);
    }
}