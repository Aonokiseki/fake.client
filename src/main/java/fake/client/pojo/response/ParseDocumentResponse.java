package fake.client.pojo.response;

import fake.client.pojo.data.DocumentContent;

public class ParseDocumentResponse extends BasicResponse{
	
	private DocumentContent documentContent;

	public DocumentContent getDocumentContent() {
		return documentContent;
	}
	public void setDocumentContents(DocumentContent documentContent) {
		this.documentContent = documentContent;
	}
	
}
