package fake.client.pojo.response;

import java.util.Map;

public class HdfsDeleteByRegexResponse extends BasicResponse{
	private Map<String, Boolean> deletedFiles;
	
	public HdfsDeleteByRegexResponse setDeletedFiles(Map<String, Boolean> deletedFiles) {
		this.deletedFiles = deletedFiles;
		return this;
	}
	public Map<String, Boolean> getDeletedFiles(){
		return this.deletedFiles;
	}
}
