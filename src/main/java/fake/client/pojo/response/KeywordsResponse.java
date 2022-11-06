package fake.client.pojo.response;

import java.util.List;

public class KeywordsResponse extends BasicResponse{
	private List<String> keywords;

	public List<String> getKeywords() {
		return keywords;
	}
	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}
}
