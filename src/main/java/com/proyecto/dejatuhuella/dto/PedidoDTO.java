package com.proyecto.dejatuhuella.dto;

import com.proyecto.dejatuhuella.model.DetallePedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public class PedidoDTO {
    private Long id;
    private LocalDateTime fechaPedido;
    private String estado;
    private BigDecimal total;
    private String nombreUsuario;
    private Long usuarioId;
    private Set<DetallePedido> detalles;

    // Constructores
    public PedidoDTO() {
    }

    public PedidoDTO(Long id, LocalDateTime fechaPedido, String estado, BigDecimal total, String nombreUsuario, Long usuarioId) {
        this.id = id;
        this.fechaPedido = fechaPedido;
        this.estado = estado;
        this.total = total;
        this.nombreUsuario = nombreUsuario;
        this.usuarioId = usuarioId;
    }
    
    public PedidoDTO(Long id, LocalDateTime fechaPedido, String estado, BigDecimal total, String nombreUsuario, Long usuarioId, Set<DetallePedido> detalles) {
        this.id = id;
        this.fechaPedido = fechaPedido;
        this.estado = estado;
        this.total = total;
        this.nombreUsuario = nombreUsuario;
        this.usuarioId = usuarioId;
        this.detalles = detalles;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    public Set<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(Set<DetallePedido> detalles) {
        this.detalles = detalles;
    }
}