package com.proyecto.dejatuhuella.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class JacksonConfig {

    @Bean
    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = converter.getObjectMapper();
        
        // Registrar el módulo Hibernate5Module para manejar los proxies de Hibernate
        Hibernate5JakartaModule hibernateModule = new Hibernate5JakartaModule();
        // Configurar el módulo para manejar referencias lazy
        hibernateModule.configure(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING, false);
        mapper.registerModule(hibernateModule);
        
        // Deshabilitar la serialización de beans vacíos
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        
        return converter;
    }
}