package com.proyecto.dejatuhuella.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class FileStorageService {

    @Autowired
    private Cloudinary cloudinary;

    public String storeFile(MultipartFile file) {
        try {
            // Generar nombre único para el archivo
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String publicId = "dejatuhuella/productos/" + UUID.randomUUID().toString();

            // Subir archivo a Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), 
                ObjectUtils.asMap(
                    "public_id", publicId,
                    "resource_type", "auto",
                    "folder", "dejatuhuella/productos",
                    "transformation", ObjectUtils.asMap(
                        "quality", "auto:good",
                        "f_auto", true
                    )
                ));

            // Devolver la URL segura del archivo
            return (String) uploadResult.get("secure_url");
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo almacenar el archivo en Cloudinary. Error: " + ex.getMessage());
        }
    }
}