package com.proyecto.dejatuhuella.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configurar el acceso a la carpeta de uploads
        Path uploadPath = Path.of(uploadDir);
        String uploadAbsolutePath = uploadPath.toFile().getAbsolutePath();
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadAbsolutePath + "/");
        
        // Asegurarse de que los recursos estáticos estándar también estén disponibles
        // y deshabilitar la caché para desarrollo
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(0) // Deshabilitar la caché
                .resourceChain(false) // Deshabilitar la cadena de recursos por defecto
                .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**")); // Agregar versionado basado en contenido
        
        // Configuración específica para archivos JavaScript
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCachePeriod(0) // Deshabilitar la caché
                .resourceChain(false); // Deshabilitar la cadena de recursos por defecto
    }
}