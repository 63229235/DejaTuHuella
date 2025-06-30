package com.proyecto.dejatuhuella.repository;

import com.proyecto.dejatuhuella.model.EstadoPedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoPedidoRepository extends JpaRepository<EstadoPedidoEntity, Long> {
    EstadoPedidoEntity findByNombreEstado(String nombreEstado);
}