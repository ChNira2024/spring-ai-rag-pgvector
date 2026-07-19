package com.springai.rag.service;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.springai.rag.dto.AskRequest2;
import com.springai.rag.dto.AskResponse;

//Manual way
/*
@Service
public class PdfService4 {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public PdfService4(VectorStore vectorStore,
                       ChatClient.Builder builder) {

        this.vectorStore = vectorStore;
        this.chatClient = builder.build();
    }

    public AskResponse askQuestion(AskRequest2 request) {

        long startTime = System.currentTimeMillis();

        // Build Search Request
        SearchRequest.Builder searchBuilder = SearchRequest.builder()
                .query(request.getQuestion())
                .topK(5)
                .similarityThreshold(0.0);

        // Metadata Filtering (Optional)
        if (request.getFileName() != null &&
                !request.getFileName().isBlank()) {

            searchBuilder.filterExpression(
                    "fileName == '" + request.getFileName() + "'");
        }

        // Retrieve Chunks
        List<Document> documents =
                vectorStore.similaritySearch(searchBuilder.build());

        System.out.println("Documents Retrieved : " + documents.size());

        for (Document doc : documents) {

            System.out.println("--------------------------------");
            System.out.println("File Name : " + doc.getMetadata().get("fileName"));
            System.out.println("Page No   : " + doc.getMetadata().get("page_number"));
            System.out.println("Chunk No  : " + doc.getMetadata().get("chunk_index"));
            System.out.println(doc.getText());
        }

        // No Data Found
        if (documents == null || documents.isEmpty()) {

            return new AskResponse(
                    request.getQuestion(),
                    "No relevant information found in uploaded documents.",
                    List.of(),
                    0,
                    System.currentTimeMillis() - startTime
            );
        }

        // Build Context
        String context = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        System.out.println("=========== CONTEXT ===========");
        System.out.println(context);
        System.out.println("===============================");

        // Ask LLM
        String answer = chatClient.prompt()
                .user("""
                        You are an AI Assistant.

                        Answer ONLY from the below context.

                        Context:
                        %s

                        Question:
                        %s

                        If the answer is not available in the context,
                        reply:

                        I don't know based on uploaded documents.
                        """
                        .formatted(context, request.getQuestion()))
                .call()
                .content();

        // Source Files
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
 */
//Automated way
@Service
public class PdfService4 {

    private final ChatClient.Builder chatClientBuilder;
    private final VectorStore vectorStore;

    public PdfService4(VectorStore vectorStore,
                       ChatClient.Builder builder) {

        this.vectorStore = vectorStore;
        this.chatClientBuilder = builder;
    }

    public AskResponse askQuestion(AskRequest2 request) {

        long startTime = System.currentTimeMillis();

        SearchRequest.Builder searchBuilder = SearchRequest.builder()
                .query(request.getQuestion())
                .topK(5)
                .similarityThreshold(0.0);

        // Metadata Filtering
        if (request.getFileName() != null &&
                !request.getFileName().isBlank()) {

            searchBuilder.filterExpression(
                    "fileName == '" + request.getFileName() + "'");
        }

        QuestionAnswerAdvisor advisor =
                QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(searchBuilder.build())
                        .build();

        ChatClient chatClient = chatClientBuilder
                .defaultAdvisors(advisor)
                .build();

        String answer = chatClient.prompt()
                .user(request.getQuestion())
                .call()
                .content();

        long responseTime = System.currentTimeMillis() - startTime;

        // Avoid NullPointerException
        List<String> sources;

        if (request.getFileName() != null &&
                !request.getFileName().isBlank()) {

            sources = List.of(request.getFileName());

        } else {

            sources = List.of();

        }

        return new AskResponse(
                request.getQuestion(),
                answer,
                sources,
                0,              // Advisor doesn't expose matched chunk count
                responseTime
        );
    }
}