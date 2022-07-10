package fake.client.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;

import com.huaban.analysis.jieba.SegToken;

public class EnhancedTextRank {
	
	private final static double DEFAULT_ALPHA = 0.85;
	private final static int DEFAULT_ITERATION = 255;
	private final static int DEFAULT_TOP_N_SENTENCES = 3;
	private final static boolean DEFAULT_WORDS_WEIGHT_AVAILABLE = false;
	private final static boolean DEFAULT_WORDS_IDF_AVAILABLE = false;
	private final static String DEFAULT_SEPARATOR_REGEX = "[!?！;？。；]";
	
	private double alpha;
	private int iteration;
	private int topNSentences;
	private String separatorRegex;
	private Set<String> stopwords;
	private boolean wordsWeightAvailable;
	private Map<String, Double> wordsWeight;
	private boolean wordsIdfAvailable;
	private Map<String, Double> wordsIdf;
	
	public EnhancedTextRank setAlpha(double alpha) {
		this.alpha = alpha;
		return this;
	}
	public double alpha() {
		return this.alpha;
	}
	public int getIteration() {
		return iteration;
	}
	public EnhancedTextRank setIteration(int iteration) {
		this.iteration = iteration;
		return this;
	}
	public int getTopNSentences() {
		return topNSentences;
	}
	public EnhancedTextRank setTopNSentences(int topNSentences) {
		this.topNSentences = topNSentences;
		return this;
	}
	public String getSeparatorRegex() {
		return separatorRegex;
	}
	public EnhancedTextRank setSeparatorRegex(String separatorRegex) {
		this.separatorRegex = separatorRegex;
		return this;
	}
	public Set<String> getStopwords() {
		return stopwords;
	}
	public EnhancedTextRank setStopwords(Set<String> stopwords) {
		this.stopwords = stopwords;
		return this;
	}
	public boolean isWordsWeightAvailable() {
		return wordsWeightAvailable;
	}
	public EnhancedTextRank setWordsWeightAvailable(boolean wordsWeightAvailable) {
		this.wordsWeightAvailable = wordsWeightAvailable;
		return this;
	}
	public Map<String, Double> getWordsWeight() {
		return wordsWeight;
	}
	public EnhancedTextRank setWordsWeight(Map<String, Double> wordsWeight) {
		this.wordsWeight = wordsWeight;
		return this;
	}
	public boolean isWordsIdfAvailable() {
		return wordsIdfAvailable;
	}
	public EnhancedTextRank setWordsIdfAvailable(boolean wordsIdfAvailable) {
		this.wordsIdfAvailable = wordsIdfAvailable;
		return this;
	}
	public Map<String, Double> getWordsIdf() {
		return wordsIdf;
	}
	public EnhancedTextRank setWordsIdf(Map<String, Double> wordsIdf) {
		this.wordsIdf = wordsIdf;
		return this;
	}
	public EnhancedTextRank() {
		this.stopwords = new HashSet<String>();
		this.alpha = DEFAULT_ALPHA;
		this.iteration = DEFAULT_ITERATION;
		this.topNSentences = DEFAULT_TOP_N_SENTENCES;
		this.separatorRegex = DEFAULT_SEPARATOR_REGEX;
		this.wordsWeightAvailable = DEFAULT_WORDS_WEIGHT_AVAILABLE;
		this.wordsIdfAvailable = DEFAULT_WORDS_IDF_AVAILABLE;
		this.wordsWeight = new HashMap<String, Double>();
		this.wordsIdf = new HashMap<String, Double>();
	}
	
	/**
	 * 提取摘要
	 * @param text
	 * @return
	 */
	public String summary(String text) {
		/* 全文分句 */
		List<String> sentences = splitSentences(text);
		/* 文本过短, 直接返回即可 */
		if(sentences.size() <= this.topNSentences)
			return text;
		/* 全文分词 */
		List<List<String>> globalSegments = globalTextSegment(sentences);
		/* 构造相关度矩阵 */
		double[][] similarityMatrix = buildSimilarityMatrix(globalSegments);
		/* pageRank算法迭代 */
		Map<Integer, Double> result = pageRank(similarityMatrix, alpha, iteration);
		/* 按分值排序句子 */
		LinkedHashMap<Integer, Double> sortedResult = sortByValue(result);
		/* 取出得分最高的前N句并返回 */
		return fetchHighestScoreSentences(sortedResult, sentences);
	}
	
	/**
	 * PageRank算法
	 * @param similarityMatrix 相关度矩阵(状态变化的规则)
	 * @param alpha 修正参数, 根据历史经验设置0.85比较合适
	 * @param maxIteration 最大迭代次数, 超过次数若还未收敛也停止迭代
	 * @return
	 */
	private Map<Integer, Double> pageRank(double[][] similarityMatrix, double alpha, int maxIteration){
		int n = similarityMatrix.length;
		if(n == 0)
			return new HashMap<Integer, Double>();
		double[][] normalizedSimilarityMatrix = normalization(similarityMatrix);
		double[] last = null;
		double[] current = initializeSentenceScoreVector(n);
		double[] initialzation = initializeSentenceScoreVector(n);
		double[][] wrapper = null;
		for(int i = 0; i < maxIteration; i++) {
			last = current;
			/* current 是一个一维数组, 不能直接参与矩阵乘法, 只能先包装成二维数组(矩阵) */
			wrapper = new double[][] {current};
			/* 
			 * 记符号 "@" 为矩阵乘法, 迭代公式表示为
			 * current = alpha * last @ similarityMatrix + (1 - alpha) * initialization
			 * 
			 * 其中:
			 * 1.initialization 为"初始句子得分"向量
			 * 2.current 表示本次的"句子得分"向量
			 * 3.last 表示上次迭代的"句子得分"向量
			 */
			double[] part1 = multiplyMatrix(multiplyMatrix(wrapper, normalizedSimilarityMatrix)[0], alpha);
			double[] part2 = multiplyMatrix(initialzation, 1 - alpha);
			current = addVector(part1, part2);
			boolean convergence = isConvergence(current, last, (1.0e-6) * n);
			if(convergence)
				break;
		}
		Map<Integer, Double> pageRankScores = new HashMap<Integer, Double>();
		for(int k = 0; k < current.length; k++)
			pageRankScores.put(k, current[k]);
		return pageRankScores;
	}
	/**
	 * 取出分值最高的前N条语句
	 * @param sortedResult
	 * @param sentences
	 * @return
	 */
	private String fetchHighestScoreSentences(LinkedHashMap<Integer, Double> sortedResult, List<String> sentences) {
		int index = 0;
		String[] highestScoreSentences = new String[sentences.size()];
		for(Entry<Integer, Double> e : sortedResult.entrySet()) {
			if(index++ < topNSentences)
				highestScoreSentences[e.getKey()] = sentences.get(e.getKey());
		}
		StringBuilder result = new StringBuilder();
		for(int i = 0, size = highestScoreSentences.length; i < size; i++)
			if(highestScoreSentences[i] != null)
				result.append(highestScoreSentences[i]);
		return result.toString();
	}
	/**
	 * 初始化句子得分向量
	 * @param n
	 * @return
	 */
	private double[] initializeSentenceScoreVector(int n) {
		double[] vector = new double[n];
		for(int i = 0; i < n; i++)
			vector[i] = 1.0 / n;
		return vector;
	}
	/**
	 * 矩阵归一化
	 * @param similarityMatrix 原始矩阵
	 * @return 归一化的矩阵
	 */
	private double[][] normalization(double[][] similarityMatrix){
		List<Double> sumVector = sumVector(similarityMatrix);
		List<Double> backwardSumVector = backwards(sumVector);
		double[][] diagonalMatrix = buildDiagonalMatrix(backwardSumVector);
		double[][] result = multiplyMatrix(diagonalMatrix, similarityMatrix);
		return result;
	}
	/**
	 * 计算和向量<br><br>
	 * 
	 * 举例<br>
	 * A = [<br>
	 * 	[1, 0.5, 0.25], 相加=1.75<br>
	 *  [0.5, 1, 0.75], 相加=2.25<br>
	 *  [0.25, 0.75, 1]  相加=2<br>
	 * ]<br><br>
	 * 
	 * 最终构造出一个列向量<br>
	 * vector = [<br>
	 *  1.75,<br>
	 *  2.25,<br>
	 *  2<br>
	 * ]
	 * 
	 * @param similarityMatrix
	 * @return
	 */
	private List<Double> sumVector(double[][] similarityMatrix){
		List<Double> result = new ArrayList<Double>();
		for(int i = 0; i < similarityMatrix.length; i++) {
			double sum = 0.0;
			for(int j = 0; j < similarityMatrix[i].length; j++)
				sum += similarityMatrix[i][j];
			result.add(sum);
		}
		return result;
	}
	/**
	 * 向量每个元素取倒数<br><br>
	 * 
	 * 举例<br>
	 * vector = [1.75, 2.25, 2]<br><br>
	 * 
	 * 取倒数变为<br>
	 * result = [0.5714, 0.4444, 0.5]
	 * @param vector
	 * @return
	 */
	private List<Double> backwards(List<Double> vector){
		List<Double> backWardsVector = new ArrayList<Double>(vector.size());
		for(int i = 0, size = vector.size(); i < size; i++)
			backWardsVector.add( 1 / vector.get(i));
		return backWardsVector;
	}
	/**
	 * 构造对角矩阵<br><br>
	 * 
	 * 举例:<br>
	 * backwardsSumVector = [0.5714, 0.4444, 0.5]<br><br>
	 * 
	 * 结果
	 * matrix = [<br>
	 *     [0.5714, 0,      0  ],<br>
	 *     [0,      0.4444, 0  ],<br>
	 *     [0,      0,      0.5]<br>
	 * ]<br>
	 * 
	 * @param backwardsSumVector
	 * @return
	 */
	private double[][] buildDiagonalMatrix(List<Double> backwardsSumVector){
		double[][] diagonalMatrix = new double[backwardsSumVector.size()][backwardsSumVector.size()];
		for(int i = 0; i < backwardsSumVector.size(); i++) {
			for(int j = 0; j < backwardsSumVector.size(); j++) {
				if (i == j)
					diagonalMatrix[i][j] = backwardsSumVector.get(i);
			}
		}
		return diagonalMatrix;
	}
	/**
	 * 矩阵乘法
	 * @param left
	 * @param right
	 * @return
	 */
	private double[][] multiplyMatrix(double[][] left, double[][] right){
		double[][] result = new double[left.length][right[0].length];
		double temp;
		for(int i=0; i<result.length; i++){
			for(int j=0; j<result[i].length; j++){
				for(int k=0; k<right.length; k++){
					temp = left[i][k] * right[k][j];
					result[i][j] += temp;
				}
			}
		}
		return result;
	}
	/**
	 * 矩阵数乘运算
	 * @param vector
	 * @param p
	 * @return
	 */
	private double[] multiplyMatrix(double[] vector, double p) {
		double[] result = new double[vector.length];
		for(int i = 0; i < vector.length; i++)
				result[i] = vector[i] * p;
		return result;
	}
	/**
	 * 向量加法
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	private double[] addVector(double[] vector1, double[] vector2) {
		double[] result = new double[vector1.length];
		for(int i = 0; i < vector1.length; i++)
			result[i] = vector1[i] + vector2[i];
		return result;
	}
	/**
	 * 两个向量的差值是否足够小, 用于判断是否收敛
	 * @param now
	 * @param last
	 * @param standard
	 * @return
	 */
	private boolean isConvergence(double[] now, double[] last, double standard) {
		double sum = 0.0;
		for(int i = 0; i < now.length; i++)
			sum += Math.abs(now[i] - last[i]);
		return sum < standard;
	}
	/**
	 * 全文分句
	 * @param text 原始文本
	 * @return List&ltString&gt 句子列表
	 */
	private List<String> splitSentences(String text){
		List<String> sentences = new ArrayList<String>();
		Pattern pattern = Pattern.compile(separatorRegex);
		String[] sentenceArray = pattern.split(text);
		Matcher matcher = pattern.matcher(text);
		if(sentenceArray.length > 0) {
			for(int i = 0; i < sentenceArray.length; i++) {
				if(matcher.find()) {
					sentenceArray[i] += matcher.group();
				}
			}
			for(String sentence : sentenceArray) {
				sentences.add(sentence);
			}
		}
		return sentences;
	}
	/**
	 * 全文分词
	 * @param sentences 句子列表
	 * @return 二维分词列表
	 */
	private List<List<String>> globalTextSegment(List<String> sentences){
		List<List<String>> globalSegments = new ArrayList<List<String>>(sentences.size());
		for(int i = 0, size = sentences.size(); i < size; i++)
			globalSegments.add(segments(sentences.get(i)));
		return globalSegments;
	}
	/**
	 * 单句分词
	 * @param sentence 句子
	 * @return List&ltString&gt 分词列表
	 */
	private List<String> segments(String sentence){
		JiebaSegmenter segmenter = new JiebaSegmenter();
		List<SegToken> segTokens = segmenter.process(sentence, SegMode.SEARCH);
		List<String> segments = new LinkedList<String>();
		String segment = null;
		for(SegToken token : segTokens) {
			segment = token.word;
			if(segment.isEmpty())
				continue;
			if(isSepcialSymbol(segment))
				continue;
			if(stopwords.contains(segment))
				continue;
			segments.add(segment);
		}
		return segments;
	}
	/**
	 * 检查是否为特殊符号
	 * @param segment
	 * @return
	 */
	private boolean isSepcialSymbol(String segment) {
		return segment.equals("\\s") || segment.equals("\r") || segment.equals("\n") || segment.equals("\t");
	}
	/**
	 * 构造相关度矩阵
	 * @param sentences
	 * @param segments
	 * @return
	 */
	private double[][] buildSimilarityMatrix(List<List<String>> segments){
		int n = segments.size();
		double[][] matrix = new double[n][n];
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < i; j++) {
				double similarity = similarity(segments.get(i), segments.get(j));
				matrix[i][j] = similarity;
				matrix[j][i] = similarity;
			}
		}
		return matrix;
	}
	/**
	 * 两句文本的相似度计算
	 * @param former
	 * @param latter
	 * @return
	 */
	private double similarity(List<String> former, List<String> latter) {
		List<String> union = unionSegments(former, latter);
		List<Double> vector1 = buildCharacteristicVector(former, union);
		List<Double> vector2 = buildCharacteristicVector(latter, union);
		double cosineSimilarity = cosine(vector1, vector2);
		double editDistanceSimilarity = editDistanceSimilarity(former, latter);
		return cosineSimilarity * 0.75 + editDistanceSimilarity * 0.25;
	}
	/**
	 * 余弦相似度
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	private double cosine(List<Double> vector1, List<Double> vector2) {
		double innerProduct = innerProduct(vector1, vector2);
		double length1 = vectorLength(vector1);
		double length2 = vectorLength(vector2);
		return innerProduct / (length1 * length2);
	}
	/**
	 * 编辑距离相似度
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	private double editDistanceSimilarity(List<String> segments1, List<String> segments2) {
		double distance = editDistance(segments1, segments2);
		double similarity = 1 - distance / Math.max(segments1.size(), segments2.size());
		return similarity;
	}
	/**
	 * 编辑距离
	 * @param segments1
	 * @param segments2
	 * @return
	 */
	private int editDistance(List<String> segments1, List<String> segments2){
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
	 * 向量内积
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	private double innerProduct(List<Double> vector1, List<Double> vector2) {
		double sum = 0.0;
		for(int i = 0, size = vector1.size(); i < size; i++)
			sum += vector1.get(i) * vector2.get(i);
		return sum;
	}
	private double vectorLength(List<Double> vector) {
		double sum = 0.0;
		for(int i = 0, size = vector.size(); i < size; i++)
			sum += Math.pow(vector.get(i), 2);
		return Math.pow(sum, 0.5);
	}
	/**
	 * 取两个句子所有分词的全集并去重<br><br>
	 * 
	 * 举例:<br>
	 * former = [中华, 人民, 人民, 共和, 国]<br>
	 * latter = [人民, 民主, 共和, 政体]<br><br>
	 * 
	 * 返回: [中华,人民,共和,国,民主,政体]<br>
	 * @param former 前一个分词列表
	 * @param latter 后一个分词列表
	 * @return
	 */
	private List<String> unionSegments(List<String> former, List<String> latter){
		Set<String> set = new HashSet<String>();
		set.addAll(former);
		set.addAll(latter);
		List<String> union = new ArrayList<String>(set);
		return union;
	}
	/**
	 * 构造特征向量<br><br>
	 * 
	 * 举例:<br>
	 * segments = [中华,人民,人民, 共和,国]<br>
	 * union = [中华,人民,共和,国,民主,政体]<br><br>
	 * 
	 * 假设此时不考虑逆文档频率和自订权重, 则返回<br>
	 * [1,2,1,1,0,0]<br><br>
	 * 
	 * 它表示对于union中列出的每个分词的分值<br>
	 * (此时由于只考虑词频, 因此在segments出现的次数就是分值)<br>
	 * [中华=1, 人民=2, 共和=1, 国=1, 民主=0, 政体=0]<br><br>
	 * 
	 * 如果考虑逆文档频率和自订权重, 则分值= 词频 * 逆文档频率 + 权重 
	 * 
	 * @param segments
	 * @param union
	 * @return
	 */
	private List<Double> buildCharacteristicVector(List<String> segments, List<String> union){
		Map<String, Integer> termFrequency = termFrequency(segments);
		List<Double> characteristicVector = new ArrayList<Double>(union.size());
		String segment = null;
		double score = 0.0;
		for(int i = 0, size = union.size(); i < size; i++) {
			segment = union.get(i);
			score = segmentScore(segment, termFrequency);
			characteristicVector.add(score);
		}
		return characteristicVector;
	}
	/**
	 * 统计单个语句各个分词的词频(Term Frequency)
	 * @param segments
	 * @return
	 */
	public Map<String, Integer> termFrequency(List<String> segments){
		Map<String, Integer> statistic = new HashMap<String, Integer>();
		String segment = null;
		for(int i = 0, size = segments.size(); i < size; i++) {
			segment = segments.get(i);
			statistic.put(segment, statistic.getOrDefault(segment, 0) + 1);
		}
		return statistic;
	}
	/**
	 * 给单个分词打分
	 * @param segment
	 * @return
	 */
	private double segmentScore(String segment, Map<String, Integer> termFrequency) {
		double score = (double)termFrequency.getOrDefault(segment, 0);
		if(this.wordsIdfAvailable) {
			double idf = this.wordsIdf.getOrDefault(segment, 1.0);
			score *= idf;
		}
		if(this.wordsWeightAvailable) {
			double weight = this.wordsWeight.getOrDefault(segment, 0.0);
			score += weight;
		}
		return score;
	}
	/**
	 * 对HashMap按Value排序
	 * @param scores
	 * @return
	 */
	private LinkedHashMap<Integer, Double> sortByValue(Map<Integer, Double> scores){
		List<Entry<Integer, Double>> entries = new ArrayList<Entry<Integer, Double>>(scores.entrySet());
		Collections.sort(entries, new Comparator<Entry<Integer, Double>>(){
			@Override
			public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		LinkedHashMap<Integer, Double> result = new LinkedHashMap<Integer, Double>();
		for(Entry<Integer, Double> entry : entries)
			result.put(entry.getKey(), entry.getValue());
		return result;
	}
}