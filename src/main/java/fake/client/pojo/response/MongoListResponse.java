package fake.client.pojo.response;

import java.util.List;

public class MongoListResponse extends BasicResponse{
	private List<String> list;
	
	public MongoListResponse setList(List<String> list) {
		this.list = list;
		return this;
	}
	public List<String> getList(){
		return this.list;
	}
}
