package com.springai.rag.controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springai.rag.dto.AskRequest;
import com.springai.rag.dto.AskRequest2;
import com.springai.rag.dto.AskResponse;
import com.springai.rag.service.PdfService2;
import com.springai.rag.service.PdfService3;
import com.springai.rag.service.PdfService4;

@RestController
public class PdfController2 {

    private final PdfService2 pdfService2;
    
    private final PdfService3 pdfService3;
    private final PdfService4 pdfService4;

    public PdfController2(PdfService2 pdfService2, PdfService3 pdfService3, PdfService4 pdfService4) {
        this.pdfService2 = pdfService2;
        this.pdfService3 = pdfService3;
        this.pdfService4 = pdfService4;
    }

    //Retrieveing from vector store and then did context and also ollama response but all these i am doing manually using vectorStore.similaritySearch(request);
    //Next will do automate pipeline using QuestionAnswerAdvisor
    @PostMapping("/search-pdf-chunk-vector-store-retrieved-context")
    public AskResponse askOllama_retrieveFromVectorStoreManually(@RequestBody AskRequest request) {

        return pdfService2.askQuestion(request);
    }
    
    //QuestionAnswerAdvisor (Automatically retrieve)
    @PostMapping("/search-pdf-chunk-vector-store-retrieved-context2")
    public AskResponse askOllama_retrieveFromVectorStoreAutomatically(@RequestBody AskRequest request) {

        return pdfService3.askQuestion(request);
    }
    
  //Metadata filtering
    @PostMapping("/search-pdf-chunk-vector-store-retrieved-context3")
    public AskResponse askOllama_retrieveFromVectorStoreManually_Metadata(@RequestBody AskRequest2 request) {

        return pdfService4.askQuestion(request);
    }
}