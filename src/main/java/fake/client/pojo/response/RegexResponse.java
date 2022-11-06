package fake.client.pojo.response;

import fake.client.pojo.data.MatchResult;

public class RegexResponse extends BasicResponse{
	
	private MatchResult matchResult;

	public MatchResult matchResult() {
		return this.matchResult;
	}
	
	public void setMatchResult(MatchResult matchResult) {
		this.matchResult = matchResult;
	}
}
