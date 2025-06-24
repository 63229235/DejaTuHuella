package com.proyecto.dejatuhuella.repository;

import com.proyecto.dejatuhuella.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    // Puedes añadir métodos personalizados si son necesarios en el futuro
}