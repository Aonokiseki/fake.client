package fake.client.pojo.request;

public class KeywordsRequestBody{
	private String text;
	private Integer count;

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
}
