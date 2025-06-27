package com.proyecto.dejatuhuella.repository;

import com.proyecto.dejatuhuella.model.Carrito;
import com.proyecto.dejatuhuella.model.CarritoItem;
import com.proyecto.dejatuhuella.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    List<CarritoItem> findByCarrito(Carrito carrito);
    List<CarritoItem> findByCarritoId(Long carritoId);
    Optional<CarritoItem> findByCarritoAndProducto(Carrito carrito, Producto producto);
    void deleteByCarrito(Carrito carrito);
}