package fake.client.pojo.data;

import java.util.List;

public class DocumentContent {
	
	List<String> paragraphContents;
	List<String> tableContents;
	
	public DocumentContent() {}

	public List<String> getParagraphContents() {
		return paragraphContents;
	}
	public void setParagraphContents(List<String> paragraphContents) {
		this.paragraphContents = paragraphContents;
	}
	public List<String> getTableContents() {
		return tableContents;
	}
	public void setTableContents(List<String> tableContents) {
		this.tableContents = tableContents;
	}
}