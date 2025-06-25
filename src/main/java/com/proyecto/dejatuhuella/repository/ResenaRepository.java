package com.proyecto.dejatuhuella.repository;

import com.proyecto.dejatuhuella.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByProductoIdOrderByFechaCreacionDesc(Long productoId);
    boolean existsByUsuarioIdAndProductoId(Long usuarioId, Long productoId);
}