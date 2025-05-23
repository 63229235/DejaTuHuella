package com.proyecto.dejatuhuella.repository;

import com.proyecto.dejatuhuella.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByVendedorId(Long vendedorId);
    // Puedes añadir más métodos de búsqueda personalizados si los necesitas
}