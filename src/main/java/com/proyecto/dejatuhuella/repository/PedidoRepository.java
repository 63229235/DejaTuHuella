package com.proyecto.dejatuhuella.repository;

import com.proyecto.dejatuhuella.model.Pedido;
import com.proyecto.dejatuhuella.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioId(Long usuarioId);
    List<Pedido> findByUsuario(Usuario usuario);

    @Query("SELECT DISTINCT p FROM Pedido p JOIN p.detalles d WHERE d.producto.usuario.id = :usuarioId")
    List<Pedido> findVentasByUsuario(@Param("usuarioId") Long usuarioId);
    ;// Puedes añadir más métodos de búsqueda personalizados
}



