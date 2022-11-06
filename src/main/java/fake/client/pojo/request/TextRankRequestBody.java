package fake.client.pojo.request;

public class TextRankRequestBody {
	private String text;
	private Integer topNSentences;
	
	public TextRankRequestBody(String text, Integer topNSentences) {
		this.text = text;
		this.topNSentences = topNSentences;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Integer getTopNSentences() {
		return topNSentences;
	}
	public void setTopNSentences(Integer topNSentences) {
		this.topNSentences = topNSentences;
	}
}
