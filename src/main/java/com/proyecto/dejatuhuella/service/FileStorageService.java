package com.proyecto.dejatuhuella.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    @Autowired
    private Cloudinary cloudinary;

    public String storeFile(MultipartFile file) {
        try {
            logger.info("Iniciando subida de archivo: {}", file.getOriginalFilename());
            
            // Validar archivo
            if (file.isEmpty()) {
                throw new RuntimeException("El archivo está vacío");
            }
            
            // Generar nombre único para el archivo
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String publicId = "dejatuhuella/productos/" + UUID.randomUUID().toString();
            
            logger.info("Subiendo archivo con publicId: {}", publicId);

            // Subir archivo a Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), 
                ObjectUtils.asMap(
                    "public_id", publicId,
                    "resource_type", "auto",
                    "folder", "dejatuhuella/productos"
                ));
            
            String secureUrl = (String) uploadResult.get("secure_url");
            logger.info("Archivo subido exitosamente. URL: {}", secureUrl);

            // Devolver la URL segura del archivo
            return secureUrl;
        } catch (Exception ex) {
            logger.error("Error al subir archivo a Cloudinary: {}", ex.getMessage(), ex);
            throw new RuntimeException("No se pudo almacenar el archivo en Cloudinary. Error: " + ex.getMessage());
        }
    }
}