package com.proyecto.dejatuhuella.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proyecto.dejatuhuella.model.enums.EstadoPedido;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaPedido;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    private BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<DetallePedido> detalles = new HashSet<>(); // Cambiado de productos a detalles

    // Constructores
    public Pedido() {
        this.fechaPedido = LocalDateTime.now();
        this.estado = EstadoPedido.PENDIENTE;
    }

    // El constructor con parámetros se simplifica o se elimina,
    // ya que los detalles se añadirán a través de métodos o en el servicio.
    public Pedido(Usuario usuario) {
        this();
        this.usuario = usuario;
    }

    // Getters y Setters
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

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Set<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(Set<DetallePedido> detalles) {
        this.detalles = detalles;
    }

    // Método útil para añadir detalles y mantener la bidireccionalidad
    public void addDetalle(DetallePedido detalle) {
        this.detalles.add(detalle);
        detalle.setPedido(this);
    }

    public void removeDetalle(DetallePedido detalle) {
        this.detalles.remove(detalle);
        detalle.setPedido(null);
    }

}