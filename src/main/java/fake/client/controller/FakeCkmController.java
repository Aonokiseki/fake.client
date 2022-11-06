package fake.client.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import fake.client.meta.Constants;
import fake.client.pojo.request.ClusterRequestBody;
import fake.client.pojo.request.KeywordsRequestBody;
import fake.client.pojo.request.SegmentRequestBody;
import fake.client.pojo.request.SimilarityRequestBody;
import fake.client.pojo.request.SummaryRequestBody;
import fake.client.pojo.request.TextRankRequestBody;
import fake.client.pojo.response.BasicResponse;
import fake.client.pojo.response.KeywordsResponse;
import fake.client.pojo.response.KmeansResponse;
import fake.client.pojo.response.SegmentServiceResponse;
import fake.client.pojo.response.SimilarityResponse;
import fake.client.service.EnhancedTextRank;
import fake.client.service.KeywordsService;
import fake.client.service.Kmeans;
import fake.client.service.Kmeans.Item;
import fake.client.service.SegmentService;
import fake.client.service.SimilarityService;
import fake.client.service.Summary;
import fake.client.service.Summary.Method;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@Api(tags = "Fake·CKM")
public class FakeCkmController {
	
	@Autowired
	private Gson gson;
	@Autowired
	@Qualifier("stopwords")
	private Set<String> stopwords;
	@Autowired
	@Qualifier("idf")
	private Map<String, Double> idf;
	
	@ApiOperation(value = "文本摘要", notes="文本摘要")
	@PostMapping(path = "/rs/abs", produces = "*/*", consumes = "application/x-www-form-urlencoded")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG),
		@ApiResponse(code = Constants.EMPTY, message = Constants.EMPTY_MSG)
	})
	public String summary(@RequestBody SummaryRequestBody requestBody) throws IOException {
		Integer maxCount = requestBody.getMaxCount();
		Integer topNSentence = requestBody.getTopNSentence();
		Double topSentenceRate = requestBody.getTopSentenceRate();
		Integer threshold = requestBody.getThreshold();
		String text = requestBody.getText();
		Summary summary = new Summary().setStopWords(stopwords);
		if(maxCount != null) 
			summary.setMaxCount(maxCount);
		if(topNSentence != null) 
			summary.setTopNSentence(topNSentence);
		if(topSentenceRate != null) 
			summary.setTopSentenceRate(topSentenceRate);
		if(threshold != null) 
			summary.setThreshold(threshold);
		BasicResponse response = new BasicResponse();
		String result = summary.execute(text, Method.STD);
		if(result == null || result.isEmpty()) {
			response.setCode(Constants.EMPTY).setMessage(Constants.EMPTY_MSG);
			return gson.toJson(response);
		}
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(result);
		return gson.toJson(response);
	}
	
	@ApiOperation(value="TextRank摘要", notes = "使用 text-rank 算法完成")
	@PostMapping(path = "/rs/textrank", produces = "*/*", consumes = "application/x-www-form-urlencoded")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG),
		@ApiResponse(code = Constants.EMPTY, message = Constants.EMPTY_MSG)
	})
	public String textRank(@RequestBody TextRankRequestBody requestBody) {
		String text = requestBody.getText();
		BasicResponse response = new BasicResponse();
		EnhancedTextRank textRank = new EnhancedTextRank().setStopwords(stopwords).setWordsIdfAvailable(true).setWordsIdf(idf);
		Integer topNSentences = requestBody.getTopNSentences();
		if(topNSentences != null)
			textRank.setTopNSentences(topNSentences);
		String result = textRank.summary(text);
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG).setResult(result);
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "分词", notes="jieba分词")
	@PostMapping(path="/rs/seg", produces = "*/*", consumes = "application/x-www-form-urlencoded")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG)
	})
	public String segment(@RequestBody SegmentRequestBody requestBody) throws IOException {
		SegmentServiceResponse response = new SegmentServiceResponse();
		List<String> segments = null;
		SegmentService segmentService = new SegmentService().setStopWords(stopwords);
		segments = segmentService.jieba(requestBody.getText());
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
		response.setSegments(segments);
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "短文本聚类", notes="Kmeans短文本聚类")
	@PostMapping(path="/rs/cluster", produces = "*/*", consumes = "multipart/form-data")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
	})
	public String cluster(
			@RequestPart("uploadFile") MultipartFile uploadFile, 
			@RequestBody ClusterRequestBody requestBody) throws IOException {
		Integer groupCount = requestBody.getGroupCount();
		String keyColumn = requestBody.getKeyColumn();
		KmeansResponse response = new KmeansResponse();
		int k = Integer.valueOf(groupCount);
		InputStream inputStream = uploadFile.getInputStream();
		InputStreamReader reader = new InputStreamReader(inputStream);
		List<Map<String, Object>> records = 
				gson.fromJson(new JsonReader(reader), new TypeToken<List<Map<String, Object>>>(){}.getType());
		List<String> texts = new LinkedList<String>();
		for(int i = 0, size = records.size(); i < size; i++)
			texts.add(records.get(i).get(keyColumn).toString());
		Kmeans kmeans = new Kmeans().setStopWords(stopwords).setIdfDictionary(idf);
		Map<Item, List<Item>> result = kmeans.process(texts, k);
		Map<String, List<String>> strRes = new HashMap<String, List<String>>();
		for(Entry<Item, List<Item>> e : result.entrySet()) {
			String key = e.getKey().key();
			if(key.isEmpty())
				key = UUID.randomUUID().toString();
			List<Item> value = e.getValue();
			List<String> followers = new LinkedList<String>();
			for(Item item : value)
				followers.add(item.key());
			strRes.put(key, followers);
		}
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
		response.setClusterResult(strRes);
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "相似度", notes="短文本计算余弦相似度")
	@PostMapping(path = "/rs/similarity", produces = "*/*", consumes = "application/x-www-form-urlencoded")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG),
		@ApiResponse(code = Constants.ERROR, message = Constants.ERROR_MSG)
	})
	public String similarity(@RequestBody SimilarityRequestBody requestBody) {
		String text1 = requestBody.getText1();
		String text2 = requestBody.getText2();
		SegmentService segmentService = new SegmentService().setStopWords(stopwords);
		SimilarityService similarityService = new SimilarityService(segmentService);
		double score = similarityService.cosineSimilarity(text1, text2);
		SimilarityResponse response = new SimilarityResponse();
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
		response.setScore(score);
		return gson.toJson(response);
	}
	
	@ApiOperation(value = "关键词", notes="TF-IDF计算文本关键词")
	@PostMapping(path = "/rs/keywords", produces = "*/*", consumes = "application/x-www-form-urlencoded")
	@ApiResponses({
		@ApiResponse(code = Constants.SUCCESS, message = Constants.SUCCESS_MSG)
	})
	public String keywords(@RequestBody KeywordsRequestBody requestBody) {
		String text = requestBody.getText();
		Integer count = requestBody.getCount();
		KeywordsService keywordsService = new KeywordsService().setIdfDictionary(idf).setStopWords(stopwords);
		List<String> keywords = keywordsService.process(text, count);
		KeywordsResponse response = new KeywordsResponse();
		response.setCode(Constants.SUCCESS).setMessage(Constants.SUCCESS_MSG);
		response.setKeywords(keywords);
		return gson.toJson(response);
	}
}