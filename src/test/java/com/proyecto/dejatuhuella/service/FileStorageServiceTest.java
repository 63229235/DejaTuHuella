package com.proyecto.dejatuhuella.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    @InjectMocks
    private FileStorageService fileStorageService;

    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        // Configurar el directorio de carga temporal para las pruebas
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());

        // Crear un archivo multipart simulado para las pruebas
        multipartFile = new MockMultipartFile(
                "testfile",
                "testfile.jpg",
                "image/jpeg",
                "test image content".getBytes());
    }

    @Test
    @DisplayName("Debería almacenar un archivo correctamente")
    void storeFile() throws IOException {
        // Act
        String fileUrl = fileStorageService.storeFile(multipartFile);

        // Assert
        assertNotNull(fileUrl);
        assertTrue(fileUrl.startsWith("/uploads/"));
        assertTrue(fileUrl.endsWith(".jpg"));

        // Verificar que el archivo se haya creado físicamente
        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
        Path filePath = Paths.get(tempDir.toString(), fileName);
        assertTrue(Files.exists(filePath));

        // Verificar el contenido del archivo
        byte[] content = Files.readAllBytes(filePath);
        assertEquals("test image content", new String(content));
    }

    @Test
    @DisplayName("Debería manejar un archivo nulo")
    void storeFileNull() {
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            fileStorageService.storeFile(null);
        });

        assertEquals("No se pudo almacenar un archivo nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Debería manejar un archivo con nombre inválido")
    void storeFileInvalidName() {
        // Arrange
        MultipartFile invalidFile = new MockMultipartFile(
                "testfile",
                "../../../malicious.jpg", // Intento de path traversal
                "image/jpeg",
                "malicious content".getBytes());

        // Act
        String fileUrl = fileStorageService.storeFile(invalidFile);

        // Assert
        assertNotNull(fileUrl);
        assertFalse(fileUrl.contains("../"));
        assertTrue(fileUrl.startsWith("/uploads/"));

        // Verificar que el archivo se haya creado con un nombre seguro
        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
        Path filePath = Paths.get(tempDir.toString(), fileName);
        assertTrue(Files.exists(filePath));
    }

    @Test
    @DisplayName("Debería manejar un error de IO al almacenar un archivo")
    void storeFileIOException() throws IOException {
        // Arrange - Crear un directorio que no se puede escribir
        Path readOnlyDir = tempDir.resolve("readonly");
        Files.createDirectory(readOnlyDir);
        File readOnlyFile = readOnlyDir.toFile();
        readOnlyFile.setReadOnly();

        // Configurar el servicio para usar el directorio de solo lectura
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", readOnlyDir.toString());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            fileStorageService.storeFile(multipartFile);
        });

        assertTrue(exception.getMessage().contains("No se pudo almacenar el archivo"));

        // Limpiar - Restaurar permisos para permitir la eliminación
        readOnlyFile.setWritable(true);
    }
}