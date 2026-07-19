package com.springai.rag.controller;


import java.io.IOException;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.springai.rag.dto.UploadResponse;
import com.springai.rag.service.PdfService;

@RestController
public class PdfController {

    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/classpath-pdf")
    public List<String> readPdf(@RequestParam String fileName) {
        List<Document> documents = pdfService.readFromClasspath(fileName);
        //return documents.stream().map(Document::getText).toList();
        return documents.stream().map(Document::getText).map(text -> text.replaceAll("\\s+", " ").trim()).toList();

    }
    
    @PostMapping("/upload-pdf")
    public List<String> uploadPdf(@RequestParam MultipartFile file) throws IOException {
        List<Document> documents = pdfService.uploadPdf(file);
        //return documents.stream().map(Document::getText).toList();
        return documents.stream().map(Document::getText).map(text -> text.replaceAll("\\s+", " ").trim()).toList();

    }
    
    @GetMapping("/chunk")
    public List<String> chunk(@RequestParam String fileName) {
        List<Document> documents = pdfService.readFromClasspath(fileName);
        
        // Original documents (pages)
        System.out.println("Original Documents : " + documents.size());
        List<Document> chunks = pdfService.chunkDocuments(documents);
        System.out.println("chunk size: "+chunks.size());
        return chunks.stream().map(Document::getText).toList();
    }
    
    @GetMapping("/classpath-pdf-chunk-vector-store")
    public UploadResponse loadPdfFrom_Classpath_DoChunk_VectorStore(@RequestParam String fileName) {
      
    	return pdfService.loadPdfFromClasspath_DoChunk_VectorStore(fileName);
    }
    
    //PDF data store in vector store by chunk
    @PostMapping("/upload-pdf-chunk-vector-store")
    public UploadResponse loadPdfFrom_Multipartfile_DoChunk_VectorStore(@RequestParam MultipartFile file) throws IOException {
        return pdfService.loadPdfFromMultipartfile_DoChunk_VectorStore(file);

    }
    
    //Similarity Search(PDF data retrieve from vector store by chunk)
    @PostMapping("/search-pdf-chunk-vector-store") //It retrieves from the vector_store database, NOT from the uploaded PDF file.
    public List<String> loadPdfFromMultipartfile_DoChunk_VectorStore_Retrieve_Retrieve(@RequestParam String question) {

        List<Document> documents = pdfService.loadPdfFromMultipartfile_DoChunk_VectorStore_Retrieve(question);

        return documents.stream().map(Document::getText).toList();
    }

}