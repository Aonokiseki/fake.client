package fake.client.pojo.response;

import java.util.List;

public class ParsePdfResponse extends BasicResponse{
	private List<String> pageContents;

	public List<String> getPageContents() {
		return pageContents;
	}

	public void setPageContents(List<String> pageContents) {
		this.pageContents = pageContents;
	}
}
