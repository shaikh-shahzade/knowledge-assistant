package com.org.assistance_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.assistance_app.entity.DocumentData;
import com.org.assistance_app.entity.DocumentStatus;

public interface DocumentRepository extends  JpaRepository<DocumentData , Long>{

    List<DocumentData> findByStatus(DocumentStatus status);

    boolean existsByChecksum(String checksum);

}