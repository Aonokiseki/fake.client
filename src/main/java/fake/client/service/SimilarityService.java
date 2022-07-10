package fake.client.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fake.client.util.MathUtil;

public class SimilarityService {

	private SegmentService tokenService;
	
	public SimilarityService(SegmentService tokenService) {
		this.tokenService = tokenService;
	}
	
	public double editDistanceSimilarity(String text1, String text2, boolean tokenization) throws IOException {
		List<String> segments1 = null, segments2 = null;
		if(tokenization) {
			segments1 = tokenService.jieba(text1);
			segments2 = tokenService.jieba(text2);
		}else {
			segments1 = Arrays.asList(text1.split(""));
			segments2 = Arrays.asList(text2.split(""));
		}
		double distance = editDistance(segments1, segments2);
		double similarity = 1 - distance / Math.max(segments1.size(), segments2.size());
		return similarity;
	}
	
	private int editDistance(List<String> segments1, List<String> segments2) throws IOException {
		int[][] dp = new int[segments1.size() + 1][segments2.size() + 1];
		for(int i = 0; i <= segments1.size(); i++)
			dp[i][0] = i;
		for(int j = 0; j <= segments2.size(); j++)
			dp[0][j] = j;
		for(int i = 1; i <= segments1.size(); i++) {
			for(int j = 1; j <= segments2.size(); j++) {
				if(segments1.get(i - 1).equals(segments2.get(j - 1))) {
					dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i - 1][j] + 1), dp[i - 1][j - 1]);
					continue;
				}
				dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);
			}
		}
		return dp[segments1.size()][segments2.size()];
	}
	
	/**
	 * 两篇文本之间的余弦相似度
	 * @param text1
	 * @param text2
	 * @return
	 */
	public double cosineSimilarity(String text1, String text2) {
		List<String> segments1 = tokenService.jieba(text1);
		List<String> segments2 = tokenService.jieba(text2);
		List<String> segmentBag = segmentBag(segments1, segments2);
		List<Double> vector1 = characteristicVector(segments1, segmentBag);
		List<Double> vector2 = characteristicVector(segments2, segmentBag);
		return MathUtil.cosine(vector1, vector2);
	}
	private List<String> segmentBag(List<String> segments1, List<String> segments2){
		Set<String> segmentBag = new HashSet<String>();
		segmentBag.addAll(segments1);
		segmentBag.addAll(segments2);
		return new ArrayList<String>(segmentBag);
	}
	private List<Double> characteristicVector(List<String> segments, List<String> segmentBag){
		List<Double> vector = new ArrayList<Double>(segmentBag.size());
		Map<String, Integer> termFrequency = termFrequency(segments);
		double score = 0.0;
		for(String segment : segmentBag) {
			score = termFrequency.getOrDefault(segment, 0).doubleValue();
			vector.add(score);
		}
		return vector;
	}
	private Map<String, Integer> termFrequency(List<String> segments){
		Map<String, Integer> statistic = new HashMap<String, Integer>();
		String segment = null;
		for(int i = 0, size = segments.size(); i < size; i++) {
			segment = segments.get(i);
			statistic.put(segment, statistic.getOrDefault(segment, 0) + 1);
		}
		return statistic;
	}
}
