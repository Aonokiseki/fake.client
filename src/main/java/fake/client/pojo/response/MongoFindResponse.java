package fake.client.pojo.response;

import java.util.List;

import org.bson.Document;

public class MongoFindResponse extends BasicResponse{
	
	private List<Document> records;
	
	public MongoFindResponse setRecords(List<Document> records) {
		this.records = records;
		return this;
	}
	public List<Document> getRecords(){
		return records;
	}

}
