package com.org.assistance_app.serviceImpl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.org.assistance_app.exception.DocumentException;
import com.org.assistance_app.service.DocumentStorageService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DocumentStorageFactory {

    private final List<DocumentStorageService> storageServices;

    public DocumentStorageService getStorage(String storageType) {
        return storageServices.stream()
                .filter(service -> service.supports(storageType))
                .findFirst()
                .orElseThrow(() -> new DocumentException(
                        "Unsupported storage type: " + storageType,
                        HttpStatus.BAD_REQUEST
                ));
    }
}