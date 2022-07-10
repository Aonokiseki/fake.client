package fake.client.pojo.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KmeansResponse extends BasicResponse{
	
	private Map<String, List<String>> clusterResult;
	
	public KmeansResponse() {
		this.clusterResult = new HashMap<String, List<String>>();
	}
	
	public KmeansResponse setClusterResult(Map<String, List<String>> clusterResult) {
		this.clusterResult = clusterResult;
		return this;
	}
	
	public Map<String, List<String>> getClusterResult(){
		return this.clusterResult;
	}
}
