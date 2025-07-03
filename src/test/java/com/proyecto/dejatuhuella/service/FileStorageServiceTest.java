package com.proyecto.dejatuhuella.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

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
        Path filePath = Path.of(tempDir.toString(), fileName);
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

        assertTrue(exception.getMessage().contains("No se pudo almacenar un archivo nulo")
                || exception.getMessage().contains("because \"file\" is null"));
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
        Path filePath = Path.of(tempDir.toString(), fileName);
        assertTrue(Files.exists(filePath));
    }

     @Test
    @DisplayName("Debería manejar un error de IO al almacenar un archivo")
    void storeFileIOException() throws IOException {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("ioerror.jpg");
        when(mockFile.getInputStream()).thenThrow(new IOException("Simulated IO error"));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            fileStorageService.storeFile(mockFile);
        });
        assertTrue(exception.getMessage().contains("No se pudo almacenar el archivo"));
    }

    @Test
    @DisplayName("Debería almacenar archivos sin extensión")
    void storeFileWithoutExtension() throws IOException {
        MultipartFile fileWithoutExt = new MockMultipartFile(
                "testfile",
                "filewithoutextension",
                "image/jpeg",
                "no extension".getBytes());

        String fileUrl = fileStorageService.storeFile(fileWithoutExt);

        assertNotNull(fileUrl);
        assertTrue(fileUrl.startsWith("/uploads/"));
        // El nombre generado no debe terminar con un punto
        assertFalse(fileUrl.endsWith("."));
        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
        Path filePath = Path.of(tempDir.toString(), fileName);
        assertTrue(Files.exists(filePath));
        byte[] content = Files.readAllBytes(filePath);
        assertEquals("no extension", new String(content));
    }

    @Test
    @DisplayName("Debería almacenar archivos con nombres largos")
    void storeFileWithLongName() throws IOException {
        String longName = "a".repeat(200) + ".jpg";
        MultipartFile longNameFile = new MockMultipartFile(
                "testfile",
                longName,
                "image/jpeg",
                "long name".getBytes());

        String fileUrl = fileStorageService.storeFile(longNameFile);

        assertNotNull(fileUrl);
        assertTrue(fileUrl.startsWith("/uploads/"));
        assertTrue(fileUrl.endsWith(".jpg"));
        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
        Path filePath = Path.of(tempDir.toString(), fileName);
        assertTrue(Files.exists(filePath));
        byte[] content = Files.readAllBytes(filePath);
        assertEquals("long name", new String(content));
    }

    @Test
    @DisplayName("Debería almacenar archivos con extensión inusual")
    void storeFileWithUnusualExtension() throws IOException {
        MultipartFile file = new MockMultipartFile(
                "testfile",
                "file.weirdext",
                "application/octet-stream",
                "weird extension".getBytes());

        String fileUrl = fileStorageService.storeFile(file);

        assertNotNull(fileUrl);
        assertTrue(fileUrl.startsWith("/uploads/"));
        assertTrue(fileUrl.endsWith(".weirdext"));
        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
        Path filePath = Path.of(tempDir.toString(), fileName);
        assertTrue(Files.exists(filePath));
        byte[] content = Files.readAllBytes(filePath);
        assertEquals("weird extension", new String(content));
    }
}