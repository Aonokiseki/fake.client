package fake.client.pojo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class SummaryRequestBody {
	@ApiModelProperty(value = "文本")
	private String text;
	@ApiModelProperty(value = "根据前maxCount个高频词计算高频文本")
	private Integer maxCount;
	@ApiModelProperty(value = "摘要句子数量")
	private Integer topNSentence;
	@ApiModelProperty(value = "摘要句子占全文比例")
	private Double topSentenceRate;
	@ApiModelProperty(value = "关键分词的距离阈值")
	private Integer threshold;
	
	public SummaryRequestBody() {}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Integer getMaxCount() {
		return maxCount;
	}
	public void setMaxCount(Integer maxCount) {
		this.maxCount = maxCount;
	}
	public Integer getTopNSentence() {
		return topNSentence;
	}
	public void setTopNSentence(Integer topNSentence) {
		this.topNSentence = topNSentence;
	}
	public Double getTopSentenceRate() {
		return topSentenceRate;
	}
	public void setTopSentenceRate(Double topSentenceRate) {
		this.topSentenceRate = topSentenceRate;
	}
	public Integer getThreshold() {
		return threshold;
	}
	public void setThreshold(Integer threshold) {
		this.threshold = threshold;
	}
}
