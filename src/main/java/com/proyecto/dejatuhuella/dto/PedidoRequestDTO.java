package com.proyecto.dejatuhuella.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List; // Cambiado de Set a List para mantener el orden si es necesario

public class PedidoRequestDTO {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId; // En una app real, esto se obtendría del usuario autenticado

    @NotEmpty(message = "El pedido debe contener al menos un producto")
    @Valid // Para que se validen los DTOs anidados
    private List<DetallePedidoRequestDTO> detalles;

    // Getters y Setters
    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public List<DetallePedidoRequestDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedidoRequestDTO> detalles) {
        this.detalles = detalles;
    }
}