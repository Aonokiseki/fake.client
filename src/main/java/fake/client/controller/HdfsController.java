package fake.client.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import fake.client.meta.Constants;
import fake.client.pojo.response.BasicResponse;
import fake.client.pojo.response.HdfsDeleteByRegexResponse;
import fake.client.pojo.response.HdfsListFilesResponse;
import fake.client.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(tags = "HDFS")
public class HdfsController {
	
	@Autowired(required=false)
	private FileSystem fileSystem;
	
	@Autowired
	private Gson gson;
	
	@ApiOperation(value="列出文件", notes="列出HDFS的文件")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "remotePath", value="HDFS目录", required=false),
		@ApiImplicitParam(paramType = "query", dataTypeClass = Boolean.class, name = "recursive", value="是否递归搜索", required=false)
	})
	@GetMapping(path="/hdfs/listFiles", produces="application/json;charset=utf-8")
	@ApiResponses({
		@ApiResponse(code=Constants.SUCCESS, message=Constants.SUCCESS_MSG),
		@ApiResponse(code=Constants.EMPTY, message=Constants.EMPTY_MSG)
	})
	public String listFiles(String remotePath, boolean recursive) throws IOException{
		Path path = new Path(remotePath);
		RemoteIterator<LocatedFileStatus> iterator;
		HdfsListFilesResponse response = new HdfsListFilesResponse();
		iterator = fileSystem.listFiles(path, recursive);
		if(iterator == null || !iterator.hasNext()) {
			response.setCode(Constants.EMPTY).setMessage(Constants.EMPTY_MSG);
			return gson.toJson(response);
		}
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
		List<LocatedFileStatus> files = new LinkedList<LocatedFileStatus>();
		while(iterator.hasNext())
			files.add(iterator.next());
		response.setFiles(files);
		return gson.toJson(response);
	}
	
	@ApiOperation(value="查看文件", notes="以字符流方式查看HDFS的文件内容")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "remotePath", value="HDFS文件路径", required=true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "charsetName", value="编码(为空视为utf-8)", required=false),
	})
	@GetMapping(path="/hdfs/cat", produces="application/json;charset=utf-8")
	@ApiResponses({
		@ApiResponse(code=Constants.SUCCESS, message=Constants.SUCCESS_MSG)
	})
	public String cat(String remotePath, String charsetName) throws IOException{
		Path path = new Path(remotePath);
		FSDataInputStream in;
		BasicResponse response = new BasicResponse();
		in = fileSystem.open(path);
		StringBuilder sb = new StringBuilder();
		if(charsetName == null || charsetName.trim().isEmpty())
			charsetName = "utf-8";
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName(charsetName)));
		String line = null;
		while((line = reader.readLine()) != null)
			sb.append(line).append(System.lineSeparator());
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(sb.toString());
		reader.close();
		in.close();
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "写入文件", notes="以覆盖方式写入文件, 文件不存在时则创建, 若已存在则覆盖")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "remotePath", value = "HDFS文件路径", required = true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "content", value = "文本内容", required = true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "encoding", value = "编码(空视为utf-8)", required = false),
	})
	@PostMapping(path = "hdfs/write", produces="application/json; charset=utf-8")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG)
	})
	public String write(String remotePath, String content, String encoding) throws IOException {
		Path path = new Path(remotePath);
		if(encoding == null || encoding.trim().isEmpty())
			encoding = "utf-8";
		BasicResponse response = new BasicResponse();
		FSDataOutputStream out = fileSystem.create(path);
		out.write(content.getBytes(Charset.forName(encoding)));
		out.flush();
		out.close();
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "创建目录", notes="递归创建目录")
	@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "remotePath", value = "HDFS文件路径", required = true)
	@PostMapping(path = "hdfs/mkdirs", produces="application/json; charset=utf-8")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG)
	})
	public String mkdirs(String remotePath) throws IOException {
		Path path = new Path(remotePath);
		BasicResponse response = new BasicResponse();
		boolean result = fileSystem.mkdirs(path);
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(String.valueOf(result));
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "删除文件", notes="删除文件")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "remotePath", value = "HDFS文件路径", required = true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = Boolean.class, name = "recursive", value = "是否递归删除", required = true)
	})
	@PostMapping(path = "hdfs/delete", produces = "application/json; charset=utf-8")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG)
	})
	public String delete(String remotePath, boolean recursive) throws IllegalArgumentException, IOException {
		BasicResponse response = new BasicResponse();
		boolean result = fileSystem.delete(new Path(remotePath), recursive);
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(String.valueOf(result));
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "按规则删除", notes="按正则表达式描述的规则删除指定目录下的文件")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "remotePath", value = "HDFS文件路径", required = true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = Boolean.class, name = "recursive", value = "是否递归删除", required = true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "regex", value = "正则表达式", required = true),
	})
	@PostMapping(path = "hdfs/deleteByRegex", produces = "application/json; charset=utf-8")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG),
		@ApiResponse(code = Constants.EMPTY, message = Constants.EMPTY_MSG)
	})
	public String deleteByRegex(String remotePath, boolean recursive, String regex) throws IOException {
		LocatedFileStatus status = null;
		HdfsDeleteByRegexResponse response = new HdfsDeleteByRegexResponse();
		Pattern pattern = null;
		pattern = Pattern.compile(regex);
		Matcher matcher = null;
		RemoteIterator<LocatedFileStatus> iterator = fileSystem.listFiles(new Path(remotePath), true);
		if(!iterator.hasNext()) {
			response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
			return gson.toJson(response);
		}
		Map<String, Boolean> deleteStatistic = new HashMap<String,Boolean>();
		String filePath = null;
		while(iterator.hasNext()) {
			status = iterator.next();
			filePath = status.getPath().toString();
			matcher = pattern.matcher(filePath);
			if(matcher.find()) {
				boolean deleteResult = fileSystem.delete(status.getPath(), false);
				deleteStatistic.put(filePath, deleteResult);
			}
		}
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
		response.setDeletedFiles(deleteStatistic);
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "上传文件", notes="将本地文件上传到HDFS")
	@PostMapping(path = "hdfs/upload", produces = "application/json; charset=utf-8", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG),
	})
	public String upload(
			String remoteDirectory, @RequestPart("uploadFile") MultipartFile uploadFile) throws IllegalArgumentException, IOException {
		BasicResponse response = new BasicResponse();
		Path path = new Path(remoteDirectory);
		FileStatus fileStatus = null;
		fileStatus = fileSystem.getFileStatus(path);
		if(!fileStatus.isDirectory()) {
			String reason = String.format("[%s] is not a directory.", remoteDirectory); 
			response.setCode(Constants.ERROR).setMessage(Constants.ERROR_MSG).setResult(reason);
			return gson.toJson(response);
		}
		String newName = StringUtil.fileNameAppendCurrentTimeMillis(uploadFile.getOriginalFilename());
		String newPathStr = remoteDirectory + "/" + newName;
		FSDataOutputStream out = null;
		out = fileSystem.create(new Path(newPathStr));
		out.write(uploadFile.getBytes());
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(newPathStr);
		return gson.toJson(response);
	}
	
	@ApiOperation(value="下载文件", notes="将HDFS文件下载到本地")
	@GetMapping(path = "hdfs/download")
	public String download(String remoteFilePath, HttpServletResponse response) throws IOException {
		Path path = new Path(remoteFilePath);
		FileStatus fileStatus = null;
		fileStatus = fileSystem.getFileStatus(path);
		if(!fileStatus.isFile())
			throw new IOException(String.format("[%s] is not a file.", remoteFilePath));
		String parentPathStr = path.getParent().getName();
		String fileName = path.getName().replaceAll(parentPathStr, "");
		response.setContentType("application/octet-stream;charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		response.flushBuffer();
		OutputStream out = response.getOutputStream();
		FSDataInputStream in = fileSystem.open(path);
		IOUtils.copyBytes(in, out, 4096, true);
		BasicResponse basicResponse = new BasicResponse();
		basicResponse.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(fileName);
		return gson.toJson(basicResponse);
	}
}
