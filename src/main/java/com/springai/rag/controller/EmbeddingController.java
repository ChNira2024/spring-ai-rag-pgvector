package com.springai.rag.controller;

import com.springai.rag.service.EmbeddingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/embedding")
public class EmbeddingController {

    private final EmbeddingService embeddingService;

    public EmbeddingController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @PostMapping
    public String save(@RequestBody String text) {

        embeddingService.saveText(text);

        return "Stored Successfully";

    }

}