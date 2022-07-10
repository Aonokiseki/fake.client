package fake.client.pojo.response;

import java.util.List;
import java.util.Map;

public class ParseOOXMLResponse extends BasicResponse{
	private List<List<Map<String,String>>> xlsContent;
	
	public void setXlsContent(List<List<Map<String,String>>> xlsContent) {
		this.xlsContent = xlsContent;
	}
	
	public List<List<Map<String,String>>> getXlsContent(){
		return this.xlsContent;
	}
}
