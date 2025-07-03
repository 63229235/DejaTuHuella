package com.proyecto.dejatuhuella.config;

import com.mercadopago.MercadoPago;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;

@Configuration
public class MercadoPagoConfig {

    @Value("${mercadopago.access.token}")
    private String mercadoPagoAccessToken;

    @PostConstruct
    public void initialize() {
        // Inicializar la configuración de Mercado Pago con el token de acceso
        MercadoPago.SDK.setAccessToken(mercadoPagoAccessToken);
    }
}