package com.springai.rag.dto;


public class UploadResponse {

    private String fileName;
    private int pages;
    private int chunks;
    private String status;

    public UploadResponse() {
    }

    
    public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public int getPages() {
		return pages;
	}


	public void setPages(int pages) {
		this.pages = pages;
	}


	public int getChunks() {
		return chunks;
	}


	public void setChunks(int chunks) {
		this.chunks = chunks;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public UploadResponse(String fileName,
                          int pages,
                          int chunks,
                          String status) {

        this.fileName = fileName;
        this.pages = pages;
        this.chunks = chunks;
        this.status = status;
    }


	@Override
	public String toString() {
		return "UploadResponse [fileName=" + fileName + ", pages=" + pages + ", chunks=" + chunks + ", status=" + status
				+ "]";
	}

}