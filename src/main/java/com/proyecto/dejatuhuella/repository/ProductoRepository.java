package com.proyecto.dejatuhuella.repository;

import com.proyecto.dejatuhuella.model.Producto;
import com.proyecto.dejatuhuella.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
    
    // Método optimizado para obtener solo IDs de productos activos (reduce memoria)
    @Query("SELECT p.id FROM Producto p WHERE p.activo = true")
    List<Long> findAllIds();
}