package fake.client.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import fake.client.meta.Constants;
import fake.client.pojo.data.DocumentContent;
import fake.client.pojo.response.BasicResponse;
import fake.client.pojo.response.ParseDocumentResponse;
import fake.client.pojo.response.ParseOOXMLResponse;
import fake.client.pojo.response.ParsePdfResponse;
import fake.client.service.DocService;
import fake.client.service.DocxService;
import fake.client.service.PdfService;
import fake.client.service.XlsService;
import fake.client.service.XlsxService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(tags = "Document")
public class DocumentController {
	
	@Autowired
	private Gson gson;
	
	@ApiOperation(value = "提取xls", notes="提取xls文件内容")
	@PostMapping(path = "extract/xls", produces = "application/json; charset=utf-8")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG),
	})
	public String extractXls(
			@RequestPart("uploadFile") MultipartFile uploadFile, boolean firstRowAsTitle) throws IOException {
		List<List<Map<String,String>>> xlsContent = new LinkedList<List<Map<String,String>>>();
		ParseOOXMLResponse response = new ParseOOXMLResponse();
		InputStream in = uploadFile.getInputStream();
		xlsContent = XlsService.extract(in, firstRowAsTitle);
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
		response.setXlsContent(xlsContent);
		in.close();
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "提取xlsx", notes="提取xlsx文件的内容")
	@PostMapping(path = "extract/xlsx", produces = "application/json; charset=utf-8")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG),
	})
	public String extractXlsx(
			@RequestPart("uploadFile") MultipartFile uploadFile, boolean firstRowAsTitle) throws IOException {
		List<List<Map<String,String>>> xlsContent = new LinkedList<List<Map<String,String>>>();
		ParseOOXMLResponse response = new ParseOOXMLResponse();
		InputStream in = uploadFile.getInputStream();
		xlsContent = XlsxService.extract(in, firstRowAsTitle);
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
		response.setXlsContent(xlsContent);
		in.close();
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "提取doc", notes="提取doc文件文本全部内容")
	@PostMapping(path = "extract/doc", produces = "application/json; charset=utf-8")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG),
	})
	public String extractDoc(@RequestPart("uploadFile") MultipartFile uploadFile) throws IOException {
		InputStream in;
		BasicResponse response = new BasicResponse();
		in = uploadFile.getInputStream();
		String content = DocService.parse(in);
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(content);
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "提取docx", notes="提取docx文件文本和表格内容")
	@PostMapping(path = "extract/docx", produces = "application/json; charset=utf-8")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG),
	})
	public String extractDocx(@RequestPart("uploadFile") MultipartFile uploadFile) throws IOException {
		InputStream in;
		ParseDocumentResponse response = new ParseDocumentResponse();
		in = uploadFile.getInputStream();
		DocumentContent content = DocxService.extract(in);
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
		response.setDocumentContents(content);
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "提取pdf", notes="提取pdf文件文本")
	@PostMapping(path = "extract/pdf", produces = "application/json; charset=utf-8")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG),
	})
	public String extractPdf(@RequestPart("uploadFile") MultipartFile uploadFile) throws IOException {
		InputStream in;
		ParsePdfResponse response = new ParsePdfResponse();
		in = uploadFile.getInputStream();
		List<String> pageContents = PdfService.extract(in);
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
		response.setPageContents(pageContents);
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "计算MD5", notes="计算文件的MD5")
	@PostMapping(path = "sum/md5", produces="application/json; charset=utf-8")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG),
	})
	public String generateMd5(@RequestPart("uploadFile") MultipartFile uploadFile) throws IOException {
		BasicResponse response = new BasicResponse();
		String md5 = DigestUtils.md5DigestAsHex(uploadFile.getInputStream());
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(md5);
		return gson.toJson(response);
	}
}