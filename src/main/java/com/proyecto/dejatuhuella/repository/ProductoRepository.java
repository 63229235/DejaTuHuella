package com.proyecto.dejatuhuella.repository;

import com.proyecto.dejatuhuella.model.Producto;
import com.proyecto.dejatuhuella.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByUsuarioId(Long usuarioId);
    List<Producto> findByUsuario(Usuario usuario);
    // Añadimos método para filtrar por categoría
    List<Producto> findByCategoriaId(Long categoriaId);
    // Método para búsqueda por nombre (opcional)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}