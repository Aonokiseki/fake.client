package fake.client.pojo.response;

import java.util.List;

public class SegmentServiceResponse extends BasicResponse{
	private List<String> segments;

	public List<String> getSegments() {
		return segments;
	}
	public void setSegments(List<String> tokens) {
		this.segments = tokens;
	}
}
