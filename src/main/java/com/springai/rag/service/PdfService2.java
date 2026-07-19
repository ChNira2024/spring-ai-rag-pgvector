package com.springai.rag.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.springai.rag.dto.AskRequest;
import com.springai.rag.dto.AskResponse;

@Service
public class PdfService2 {
	
	private final VectorStore vectorStore;
	private final ChatClient chatClient;

    public PdfService2(VectorStore vectorStore,ChatClient.Builder builder) {

        this.vectorStore = vectorStore;
        this.chatClient = builder.build();
    }
    
    public AskResponse askQuestion(AskRequest request) {

    	long startTime = System.currentTimeMillis();

//        SearchRequest searchRequest = SearchRequest.builder()
//                .query(request.getQuestion())
//                .topK(3)
//                .build();
    	
    	SearchRequest searchRequest = SearchRequest.builder()
    	        .query(request.getQuestion())
    	        .topK(8)
    	        .similarityThreshold(0.0)
    	        .build();

        List<Document> documents = vectorStore.similaritySearch(searchRequest);
        

        System.out.println("Documents Found: " + documents.size());

        for (Document doc : documents) {
            System.out.println("==================================");
            System.out.println("Page : " + doc.getMetadata().get("page_number"));
            System.out.println("Chunk : " + doc.getMetadata().get("chunk_index"));
            System.out.println(doc.getText());
        }

        if (documents == null || documents.isEmpty()) {

            long responseTime = System.currentTimeMillis() - startTime;

            return new AskResponse(
                    request.getQuestion(),
                    "No relevant information found in uploaded documents.",
                    List.of(),
                    0,
                    responseTime
            );
        }

        String context = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));
        
        System.out.println("========== CONTEXT ==========");
        System.out.println(context);
        System.out.println("=============================");

        String answer = chatClient.prompt()
                .user("""
                        You are an AI Assistant.

                        Use ONLY the below context to answer.

                        Context:
                        %s

                        Question:
                        %s

                        If the answer is not available,
                        say "I don't know based on uploaded documents."
                        """
                        .formatted(context, request.getQuestion()))
                .call()
                .content();

        List<String> sources = documents.stream()
                .map(doc -> (String) doc.getMetadata().get("fileName"))
                .distinct()
                .toList();

        long responseTime = System.currentTimeMillis() - startTime;

        return new AskResponse(
                request.getQuestion(),
                answer,
                sources,
                documents.size(),
                responseTime
        );
    }
    
    
}	 