package com.springai.rag.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.springai.rag.dto.UploadResponse;
import com.springai.rag.entity.UploadedDocument;
import com.springai.rag.repository.UploadedDocumentRepository;

@Service
public class PdfService {
	
	private final VectorStore vectorStore;
    private final UploadedDocumentRepository repository;

    public PdfService(VectorStore vectorStore,
                      UploadedDocumentRepository repository) {
        this.vectorStore = vectorStore;
        this.repository = repository;
    }


	public List<Document> readFromClasspath(String fileName) {
		PagePdfDocumentReader reader = new PagePdfDocumentReader(new ClassPathResource("pdf/" + fileName));
		return reader.get();
	}

	public List<Document> uploadPdf(MultipartFile file) throws IOException {

		File temp = File.createTempFile("rag-", ".pdf");
		file.transferTo(temp);	
		PagePdfDocumentReader reader = new PagePdfDocumentReader(new FileSystemResource(temp)); //C:\Users\niran\AppData\Local\Temp
		return reader.get();
	}

	 public List<Document> chunkDocuments(List<Document> documents) {
	        TokenTextSplitter splitter = new TokenTextSplitter();	//This one is deprecated..use builder()
	        //TokenTextSplitter splitter = TokenTextSplitter.builder().build();
	        return splitter.apply(documents);
	    }
	 
	 public UploadResponse loadPdfFromClasspath_DoChunk_VectorStore(String fileName) {

	        DocumentReader reader = new PagePdfDocumentReader(new ClassPathResource("pdf/" + fileName));

	        List<Document> documents = reader.get();

	        System.out.println("Pages size : " + documents.size());

	        TokenTextSplitter splitter = new TokenTextSplitter();

	        List<Document> chunks = splitter.apply(documents);

	        System.out.println("Chunks : " + chunks.size());

	        vectorStore.add(chunks);

	        System.out.println("PDF Indexed Successfully");
	        
	        return new UploadResponse(
	                fileName,
	                documents.size(),
	                chunks.size(),
	                "SUCCESS"
	        );

	    }
	 
	 public UploadResponse loadPdfFromMultipartfile_DoChunk_VectorStore(MultipartFile file)throws IOException {
		 String fileName = file.getOriginalFilename();

	        // Check duplicate
	        if (repository.existsByFileName(fileName)) {
	            return new UploadResponse(fileName,0,0,"FILE_ALREADY_EXISTS");
	        }
		        // Create temporary file
		        File tempFile = File.createTempFile("rag-", ".pdf");

		        // Copy uploaded file
		        file.transferTo(tempFile);

		        // Read PDF
		        DocumentReader reader = new PagePdfDocumentReader(new FileSystemResource(tempFile));

		        List<Document> pages = reader.get();

		        System.out.println("Pages : " + pages.size());

		        // Chunk
		        TokenTextSplitter splitter = new TokenTextSplitter();

		        List<Document> chunks = splitter.apply(pages);

		        System.out.println("Chunks size : " + chunks.size());
		        
		        System.out.println("========== CHUNKS ==========");

		        int i = 1;
		        for (Document chunk : chunks) {
		            System.out.println("Chunk " + i++);
		            System.out.println("-------------------------");
		            System.out.println(chunk.getText());
		            System.out.println();
		        }
		        
		        // Add metadata
		        for (Document chunk : chunks) {
		            chunk.getMetadata().put("fileName", fileName);
		            System.out.println(chunk.getMetadata());

		        }
		        // Store in Vector Database
		        vectorStore.add(chunks);
		        
		        // Save uploaded document
		        UploadedDocument document = new UploadedDocument();
		        document.setFileName(fileName);
		        document.setUploadedAt(LocalDateTime.now());
		        repository.save(document);
		        return new UploadResponse(file.getOriginalFilename(),pages.size(),chunks.size(),"SUCCESS");
		    }
	 
	 public List<Document> loadPdfFromMultipartfile_DoChunk_VectorStore_Retrieve(String question) { 
		 /*
		 //It retrieves from the vector_store database, NOT from the uploaded PDF file.
		
		 Chunk 1
			↓
			
			Embedding
			↓
			
			INSERT INTO vector_store
		  */
		    SearchRequest request = SearchRequest.builder().query(question).topK(3).build();
		    
		    List<Document> result = vectorStore.similaritySearch(request);

		    System.out.println("Matched Chunks: " + result.size());

		    for (Document document : result) {
		        System.out.println("---------------------------");
		        System.out.println("text data from document: "+document.getText());
		    }

		    return result;

		   }
	 
	 //next part 7 is in PdfService2 and PdfController
}	 