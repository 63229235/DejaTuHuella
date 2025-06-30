package com.proyecto.dejatuhuella.repository;

import com.proyecto.dejatuhuella.model.Carrito;
import com.proyecto.dejatuhuella.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByUsuario(Usuario usuario);
    Optional<Carrito> findByUsuarioId(Long usuarioId);
}