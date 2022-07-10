package fake.client.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.result.DeleteResult;

import fake.client.meta.Constants;
import fake.client.pojo.response.BasicResponse;
import fake.client.pojo.response.MongoFindResponse;
import fake.client.pojo.response.MongoListResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(tags = "MongoDB")
public class MongoDBController {
	
	@Autowired(required=false)
	private MongoClient client;
	
	@Autowired
	private Gson gson;
	
	@ApiOperation(value = "查询记录", notes="查询MongoDB指定database和collection的记录")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "databaseName", value="数据库名", required=true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "collectionName", value="集合名称", required=true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "query", value="检索表达式", required=false),
	})
	@GetMapping(path="/mongo/find", produces="application/json;charset=utf-8")
	@ApiResponse(code=Constants.SUCCESS, message=Constants.SUCCESS_MSG)
	public String find(String databaseName, String collectionName, String query) {
		MongoDatabase db = client.getDatabase(databaseName);
		MongoCollection<Document> collection = db.getCollection(collectionName);
		FindIterable<Document> iterable = null;
		if(query == null || query.trim().isEmpty()) {
			iterable = collection.find();
		}else {
			Bson bson = BsonDocument.parse(query);
			iterable = collection.find(bson);
		}
		MongoFindResponse response = new MongoFindResponse();
		if(iterable == null) {
			response.setCode(Constants.EMPTY).setMessage(Constants.EMPTY_MSG);
			return gson.toJson(response);
		}
		MongoCursor<Document> iterator = iterable.iterator();
		List<Document> records = new LinkedList<Document>();
		while(iterator.hasNext())
			records.add(iterator.next());
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
		response.setRecords(records);
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "列出数据库", notes="列出所有数据库")
	@GetMapping(path="/mongo/listDatabase", produces="application/json;charset=utf-8")
	@ApiResponses({
		@ApiResponse(code=Constants.SUCCESS, message=Constants.SUCCESS_MSG),
		@ApiResponse(code=Constants.EMPTY, message=Constants.EMPTY_MSG)
	})
	public String listDatabase() {
		MongoListResponse response = new MongoListResponse();
		MongoIterable<String> databaseNameIterable = client.listDatabaseNames();
		if(databaseNameIterable == null) {
			response.setCode(Constants.EMPTY).setMessage(Constants.EMPTY_MSG);
			return gson.toJson(response);
		}
		MongoCursor<String> databaseNames = databaseNameIterable.iterator();
		if(!databaseNames.hasNext()) {
			response.setCode(Constants.EMPTY).setMessage(Constants.EMPTY_MSG);
			return gson.toJson(response);
		}
		List<String> list = new LinkedList<String>();
		while(databaseNames.hasNext())
			list.add(databaseNames.next());
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
		response.setList(list);
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "列出集合", notes="列出指定数据库下的集合")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "databaseName", value="数据库名", required=true)
	})
	@GetMapping(path = "mongo/listCollection", produces="application/json;charset=utf-8")
	@ApiResponses({
		@ApiResponse(code=Constants.SUCCESS, message=Constants.SUCCESS_MSG),
		@ApiResponse(code=Constants.EMPTY, message=Constants.EMPTY_MSG)
	})
	public String listCollection(String databaseName) {
		MongoListResponse response = new MongoListResponse();
		MongoDatabase db = client.getDatabase(databaseName);
		Iterable<String> collectionIterable = db.listCollectionNames();
		if(collectionIterable == null || !collectionIterable.iterator().hasNext()) {
			response.setCode(Constants.EMPTY).setMessage(Constants.EMPTY_MSG);
			return gson.toJson(response);
		}
		Iterator<String> iterator = collectionIterable.iterator();
		List<String> list = new LinkedList<String>();
		while(iterator.hasNext())
			list.add(iterator.next());
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
		response.setList(list);
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "插入单条记录", notes="插入单条记录")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "databaseName", value="数据库名", required=true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "collectionName", value="集合名称", required=true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "record", value="记录(以json格式描述)", required=true),
	})
	@PostMapping(path = "mongo/insertOne", produces="application/json;charset=utf-8")
	@ApiResponses({
		@ApiResponse(code=Constants.SUCCESS, message=Constants.SUCCESS_MSG),
		@ApiResponse(code=Constants.ERROR, message=Constants.ERROR_MSG)
	})
	public String insertOne(String databaseName, String collectionName, String record) {
		MongoDatabase db = client.getDatabase(databaseName);
		MongoCollection<Document> collection = db.getCollection(collectionName);
		Document document = new Document();
		BasicResponse response = new BasicResponse();
		@SuppressWarnings("unchecked")
		Map<String,String> map = (Map<String,String>)gson.fromJson(record, Map.class);
		for(Entry<String,String> e : map.entrySet())
			document.put(e.getKey(), e.getValue());
		collection.insertOne(document);
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "删除数据库", notes="删除数据库")
	@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "databaseName", value="数据库名", required=true)
	@PostMapping(path = "mongo/dropDatabase", produces="application/json;charset=utf-8")
	@ApiResponse(code=Constants.SUCCESS, message=Constants.SUCCESS_MSG)
	public String drop(String databaseName) {
		client.getDatabase(databaseName).drop();
		return gson.toJson(new BasicResponse().setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG));
	}
	
	@ApiOperation(value = "删除集合", notes="删除集合")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "databaseName", value = "数据库名", required = true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "collectionName", value = "集合名", required = true),
	})
	@PostMapping(path = "mongo/dropCollection", produces="application/json; charset=utf-8")
	@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG)
	public String dropCollection(String databaseName, String collectionName) {
		MongoDatabase db = client.getDatabase(databaseName);
		MongoCollection<Document> collection = db.getCollection(collectionName);
		collection.drop();
		return gson.toJson(new BasicResponse().setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG));
	}
	
	@ApiOperation(value = "删除记录", notes="删除记录")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "databaseName", value = "数据库名", required = true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "collectionName", value = "集合名", required = true),
		@ApiImplicitParam(paramType = "query", dataTypeClass = String.class, name = "query", value = "检索表达式", required = true)
	})
	@PostMapping(path = "mongo/deleteMany", produces="application/json; charset=utf-8")
	@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG)
	public String deleteMany(String databaseName, String collectionName, String query) {
		MongoDatabase db = client.getDatabase(databaseName);
		MongoCollection<Document> collection = db.getCollection(collectionName);
		Bson bson = BsonDocument.parse(query);
		DeleteResult result = collection.deleteMany(bson);
		BasicResponse response = new BasicResponse();
		response.setCode(Constants.SUCCESS)
				.setMessage(Constants.SUCCESS_MSG)
				.setResult(String.valueOf(result.getDeletedCount()));
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "加载文件", notes = "加载json文件, 批量插入")
	@PostMapping(path = "mongo/load", produces = "application/json; charset=utf-8")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG),
	})
	public String load(
			@RequestParam(value = "databaseName", required = true) String databaseName, 
			@RequestParam(value = "collectionName", required = true) String collectionName, 
			@RequestPart("uploadFile") MultipartFile uploadFile, 
			@RequestParam(value = "encoding", required = false, defaultValue = "utf-8") String encoding) 
					throws JsonIOException, JsonSyntaxException, UnsupportedEncodingException, IOException {
		MongoDatabase database = client.getDatabase(databaseName);
		MongoCollection<Document> collection = database.getCollection(collectionName);
		List<Map<String,String>> inputRecords = null;
		BasicResponse response = new BasicResponse();
		if(encoding == null || encoding.trim().isEmpty())
			encoding = "utf-8";
		inputRecords = gson.fromJson(new JsonReader(new InputStreamReader(uploadFile.getInputStream(), encoding)), List.class);
		List<Document> documents = new ArrayList<Document>(inputRecords.size());
		Document document = null;
		for(Map<String,String> inputRecord : inputRecords) {
			document = new Document();
			for(Entry<String,String> entry : inputRecord.entrySet())
				document.append(entry.getKey(), entry.getValue());
			documents.add(document);
		}
		collection.insertMany(documents);
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(String.valueOf(inputRecords.size()));
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "导出记录", notes = "根据条件导出记录")
	@GetMapping(path = "mongo/export", produces = "*/*")
	public String export(
			String databaseName, String collectionName, String query, HttpServletResponse response) throws IOException {
		MongoDatabase database = client.getDatabase(databaseName);
		MongoCollection<Document> collection = database.getCollection(collectionName);
		FindIterable<Document> iterable = null;
		if(query == null || query.trim().isEmpty()) {
			iterable = collection.find();
		}else {
			Bson bson = BsonDocument.parse(query);
			iterable = collection.find(bson);
		}
		MongoCursor<Document> iterator = iterable.iterator();
		List<Document> records = new LinkedList<Document>();
		while(iterator.hasNext())
			records.add(iterator.next());
		String json = gson.toJson(records);
		String fileName = String.valueOf(System.currentTimeMillis()) + ".json";
		response.setContentType("application/octet-stream;charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		response.flushBuffer();
		OutputStream out = response.getOutputStream();
		out.write(json.getBytes("utf-8"));
		out.close();
		BasicResponse basicResponse = new BasicResponse();
		basicResponse.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(fileName);
		return gson.toJson(basicResponse);
	}
}