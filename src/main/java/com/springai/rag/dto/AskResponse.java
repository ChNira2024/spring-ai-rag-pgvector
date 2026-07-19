package com.springai.rag.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AskResponse {

	private String question;
    private String answer;
    private List<String> sources;
    private int retrievedChunks;
    private long responseTimeMs;
    
}