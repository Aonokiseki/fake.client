package fake.client.pojo.response;

import java.util.List;

import org.apache.hadoop.fs.LocatedFileStatus;

public class HdfsListFilesResponse extends BasicResponse{
	
	private List<LocatedFileStatus> files;
	
	public HdfsListFilesResponse setFiles(List<LocatedFileStatus> files) {
		this.files = files;
		return this;
	}
	public List<LocatedFileStatus> getFiles(){
		return files;
	}
}
