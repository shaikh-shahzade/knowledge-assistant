package com.org.assistance_app.service;

import java.io.InputStream;

public interface DocumentStorageService {

    boolean supports(String storageType);

    String store(String key, InputStream inputStream,
                 long contentLength, String contentType);

    InputStream load(String key);

    void delete(String key);

    boolean exists(String key);
}