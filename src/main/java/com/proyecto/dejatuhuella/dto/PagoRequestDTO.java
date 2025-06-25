package com.proyecto.dejatuhuella.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PagoRequestDTO {

    @NotBlank(message = "El número de tarjeta es obligatorio")
    @Pattern(regexp = "^[0-9]{16}$", message = "El número de tarjeta debe tener 16 dígitos")
    private String numeroTarjeta;

    @NotBlank(message = "El nombre del titular es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre del titular debe tener entre 3 y 100 caracteres")
    private String nombreTitular;

    @NotBlank(message = "La fecha de vencimiento es obligatoria")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/([0-9]{2})$", message = "La fecha de vencimiento debe tener el formato MM/YY")
    private String fechaVencimiento;

    @NotBlank(message = "El código de seguridad es obligatorio")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "El código de seguridad debe tener 3 o 4 dígitos")
    private String codigoSeguridad;

    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago; // TARJETA_CREDITO, TARJETA_DEBITO, etc.

    // Getters y Setters
    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getCodigoSeguridad() {
        return codigoSeguridad;
    }

    public void setCodigoSeguridad(String codigoSeguridad) {
        this.codigoSeguridad = codigoSeguridad;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    // Método para enmascarar el número de tarjeta para propósitos de seguridad
    public String getNumeroTarjetaEnmascarado() {
        if (numeroTarjeta == null || numeroTarjeta.length() < 4) {
            return "";
        }
        return "**** **** **** " + numeroTarjeta.substring(numeroTarjeta.length() - 4);
    }
}