package com.springai.rag.repository;


import com.springai.rag.entity.UploadedDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadedDocumentRepository
        extends JpaRepository<UploadedDocument, Long> {

    boolean existsByFileName(String fileName);

}