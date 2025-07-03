package com.proyecto.dejatuhuella.dto;

import java.math.BigDecimal;

public class MercadoPagoRequestDTO {

    private Long pedidoId;
    private String descripcion;
    private BigDecimal monto;
    private String emailComprador;
    private String nombreComprador;
    
    // Constructores
    public MercadoPagoRequestDTO() {
    }
    
    public MercadoPagoRequestDTO(Long pedidoId, String descripcion, BigDecimal monto, String emailComprador, String nombreComprador) {
        this.pedidoId = pedidoId;
        this.descripcion = descripcion;
        this.monto = monto;
        this.emailComprador = emailComprador;
        this.nombreComprador = nombreComprador;
    }
    
    // Getters y Setters
    public Long getPedidoId() {
        return pedidoId;
    }
    
    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public BigDecimal getMonto() {
        return monto;
    }
    
    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
    
    public String getEmailComprador() {
        return emailComprador;
    }
    
    public void setEmailComprador(String emailComprador) {
        this.emailComprador = emailComprador;
    }
    
    public String getNombreComprador() {
        return nombreComprador;
    }
    
    public void setNombreComprador(String nombreComprador) {
        this.nombreComprador = nombreComprador;
    }
}