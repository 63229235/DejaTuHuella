package com.proyecto.dejatuhuella.model;

import jakarta.persistence.*;

@Entity
@Table(name = "estado_pedido")
public class EstadoPedidoEntity {
    
    @Id
    private Long id;
    
    @Column(name = "nombre_estado")
    private String nombreEstado;
    
    // Constructores
    public EstadoPedidoEntity() {
    }
    
    public EstadoPedidoEntity(Long id, String nombreEstado) {
        this.id = id;
        this.nombreEstado = nombreEstado;
    }
    
    // Getters y setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombreEstado() {
        return nombreEstado;
    }
    
    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }
    
    @Override
    public String toString() {
        return nombreEstado;
    }
}