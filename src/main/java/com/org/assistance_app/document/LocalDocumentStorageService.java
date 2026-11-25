package com.org.assistance_app.document;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.org.assistance_app.exception.DocumentException;
import com.org.assistance_app.service.DocumentStorageService;

@Component
public class LocalDocumentStorageService implements DocumentStorageService {

    @Value("${app.storage.local.path}")
    private String storagePath;

    @Override
    public boolean supports(String storageType) {
        return "local".equalsIgnoreCase(storageType);
    }

    @Override
    public String store(String key, InputStream inputStream,
                        long contentLength, String contentType) {
        try {
            Path root = Paths.get(storagePath).toAbsolutePath().normalize();
            Path destination = root.resolve(key).normalize();

            if (!destination.startsWith(root)) {
                throw new DocumentException(
                        "Invalid storage key", HttpStatus.BAD_REQUEST);
            }

            Files.createDirectories(destination.getParent());
            Files.copy(inputStream, destination,
                    StandardCopyOption.REPLACE_EXISTING);

            return key;

        } catch (IOException ex) {
            throw new DocumentException(
                    "Failed to store document locally",
                    HttpStatus.INTERNAL_SERVER_ERROR, ex);
        }
    }

    @Override
    public InputStream load(String key) {
        try {
            Path path = resolvePath(key);

            if (!Files.exists(path)) {
                throw new DocumentException(
                        "Document not found", HttpStatus.NOT_FOUND);
            }

            return Files.newInputStream(path);

        } catch (IOException ex) {
            throw new DocumentException(
                    "Failed to load document",
                    HttpStatus.INTERNAL_SERVER_ERROR, ex);
        }
    }

    @Override
    public void delete(String key) {
        try {
            Files.deleteIfExists(resolvePath(key));
        } catch (IOException ex) {
            throw new DocumentException(
                    "Failed to delete document",
                    HttpStatus.INTERNAL_SERVER_ERROR, ex);
        }
    }

    @Override
    public boolean exists(String key) {
        return Files.exists(resolvePath(key));
    }

    private Path resolvePath(String key) {
        Path root = Paths.get(storagePath).toAbsolutePath().normalize();
        Path path = root.resolve(key).normalize();

        if (!path.startsWith(root)) {
            throw new DocumentException(
                    "Invalid storage key", HttpStatus.BAD_REQUEST);
        }

        return path;
    }
}
