package com.springai.rag.service;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.springai.rag.dto.AskRequest;
import com.springai.rag.dto.AskResponse;

@Service
public class PdfService3 {

    private final ChatClient chatClient;

    public PdfService3(VectorStore vectorStore, ChatClient.Builder builder) {

    	QuestionAnswerAdvisor advisor =
    	        QuestionAnswerAdvisor.builder(vectorStore)
    	                .searchRequest(
    	                        SearchRequest.builder()
    	                                .topK(5)
    	                                .similarityThreshold(0.0)
    	                                .build()
    	                )
    	                .build();
        this.chatClient = builder
                .defaultAdvisors(advisor)
                .build();
    }
    
    public AskResponse askQuestion(AskRequest request) {

        long startTime = System.currentTimeMillis();

        String answer = chatClient.prompt()
                .user(request.getQuestion())  // <-- Advisor uses this
                .call()
                .content();

        long responseTime = System.currentTimeMillis() - startTime;

        return new AskResponse(
                request.getQuestion(),
                answer,
                List.of(),
                0, // Advisor doesn't expose matched chunk count
                responseTime
        );
    }

}