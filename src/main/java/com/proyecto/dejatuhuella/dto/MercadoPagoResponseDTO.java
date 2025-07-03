package com.proyecto.dejatuhuella.dto;

public class MercadoPagoResponseDTO {

    private String preferenceId;
    private String initPoint;
    private String sandboxInitPoint;
    private String status;
    private String message;
    
    // Constructores
    public MercadoPagoResponseDTO() {
    }
    
    public MercadoPagoResponseDTO(String preferenceId, String initPoint, String sandboxInitPoint) {
        this.preferenceId = preferenceId;
        this.initPoint = initPoint;
        this.sandboxInitPoint = sandboxInitPoint;
        this.status = "success";
    }
    
    public MercadoPagoResponseDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }
    
    // Getters y Setters
    public String getPreferenceId() {
        return preferenceId;
    }
    
    public void setPreferenceId(String preferenceId) {
        this.preferenceId = preferenceId;
    }
    
    public String getInitPoint() {
        return initPoint;
    }
    
    public void setInitPoint(String initPoint) {
        this.initPoint = initPoint;
    }
    
    public String getSandboxInitPoint() {
        return sandboxInitPoint;
    }
    
    public void setSandboxInitPoint(String sandboxInitPoint) {
        this.sandboxInitPoint = sandboxInitPoint;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}