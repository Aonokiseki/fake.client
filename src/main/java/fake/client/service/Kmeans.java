package fake.client.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;

import com.huaban.analysis.jieba.SegToken;

public class Kmeans {
	
	private final static int DEFAULT_MAX_ITERATION = 255;
	
	private int maxIteration;
	private Set<String> stopwords;
	private Map<String, Double> idfDictionary;
	
	public Kmeans() {
		maxIteration = DEFAULT_MAX_ITERATION;
		stopwords = new HashSet<String>();
		idfDictionary = new HashMap<String, Double>();
	}
	
	public int maxiteration() {
		return this.maxIteration;
	}
	public Kmeans setMaxIteration(int maxIteration) {
		this.maxIteration = maxIteration;
		return this;
	}
	public Set<String> stopwords(){
		return Collections.unmodifiableSet(stopwords);
	}
	public Kmeans setStopWords(Set<String> stopwords) {
		this.stopwords = stopwords;
		return this;
	}
	public Kmeans appendStopWords(Set<String> stopwords) {
		this.stopwords.addAll(stopwords);
		return this;
	}
	public Map<String, Double> idfDictionary(){
		return Collections.unmodifiableMap(idfDictionary);
	}
	public Kmeans setIdfDictionary(Map<String, Double> idfDict) {
		this.idfDictionary = idfDict;
		return this;
	}
	public Kmeans appendIdfDictionary(Map<String, Double> idfDict) {
		for(Entry<String, Double> e : idfDict.entrySet())
			this.idfDictionary.put(e.getKey(), e.getValue());
		return this;
	}
	
	public Map<Item, List<Item>> process(List<String> texts, int k) {
		/* 全文按句分词 */
		List<List<String>> totalSegments = totalSegmentation(texts);
		/* 构造词袋模型 */
		List<String> segmentsBag = createSegmentsBag(totalSegments);
		/* 构造聚类向量 */
		List<Item> items = createItems(texts, totalSegments, segmentsBag);
		System.out.println(items);
		/* 聚类 */
		Map<Item, List<Item>> cluster = kmeans(items, k);
		return cluster;
	}
	/**
	 * Kmeans聚类
	 * @param items
	 * @param k
	 * @return
	 */
	private Map<Item, List<Item>> kmeans(List<Item> items, int k){
		if(k <= 1) {
			Map<Item, List<Item>> cluster = new HashMap<Item, List<Item>>();
			cluster.put(items.get(0), items);
			return cluster;
		}
		if(k > items.size()) {
			k = items.size();
		}
		Map<Item, List<Item>> last = null;
		Map<Item, List<Item>> current = initialize(items, k);
		
		int iteration = 0;
		while(iteration < maxIteration) {
			partition(items, current);
			if(isConvergence(last, current))
				break;
			last = current;
			current = recalculateClusterHeart(current);
			iteration++;
		}
		return current;
	}
	/**
	 * 重新计算簇心
	 * @param current
	 * @return
	 */
	private Map<Item, List<Item>> recalculateClusterHeart(Map<Item, List<Item>> current) {
		Map<Item, List<Item>> next = new HashMap<Item, List<Item>>();
		for(Entry<Item, List<Item>> subCluster : current.entrySet()) {
			if(subCluster.getValue().isEmpty())
				continue;
			Item electedLeader = electHeart(subCluster.getValue());
			next.put(electedLeader, new LinkedList<Item>());
		}
		return next;
	}
	/**
	 * 选举簇心
	 * @param items
	 * @return
	 */
	private Item electHeart(List<Item> items) {
		int dimension = items.get(0).vector.size();
		List<Double> heartCharacteristic = new LinkedList<Double>();
		for(int i = 0; i < dimension; i++) {
			double sum = 0;
			for(int j = 0; j < items.size(); j++)
				sum += items.get(j).vector.get(i);
			heartCharacteristic.add(sum / dimension);
		}
		return new Item("", heartCharacteristic);
	}
	/**
	 * 划分
	 * @param items
	 * @param current
	 */
	private void partition(List<Item> items, Map<Item, List<Item>> current) {
		for(int i = 0, size = items.size(); i < size; i++) {
			Item item = items.get(i);
			Item leader = null;
			double minDistance = Double.MAX_VALUE;
			for(Entry<Item, List<Item>> subCluster : current.entrySet()) {
				Item heart = subCluster.getKey();
				double distance = distance(item, heart);
				if(distance < minDistance) {
					leader = heart;
					minDistance = distance;
				}
			}
			current.get(leader).add(item);
		}
	}
	/**
	 * 判断收敛
	 * @param last
	 * @param current
	 * @return
	 */
	private boolean isConvergence(Map<Item, List<Item>> last, Map<Item, List<Item>> current) {
		if(last == null)
			return false;
		Set<Item> lastItems =  last.keySet();
		Set<Item> currentItems = current.keySet();
		return lastItems.equals(currentItems);
	}
	
	private static double distance(Item item1, Item item2) {
		return minkowskiDistance(item1.vector, item2.vector, 2);
	}
	/**
     * 两个向量(终点)之间的闵可夫斯基距离. <br>元素类型必须为Number类的子类,两个向量的长度必须相等.
     * 
     * @param vector1 向量1
     * @param vector2 向量2
     * @param dimension 维度, dimension不小于1. <br>1为曼哈顿距离;2为欧几里得距离;数字越大越接近切比雪夫距离
     * @return Double 距离
     */
    private static <T extends Number> Double minkowskiDistance(List<T> vector1, List<T> vector2, int dimension){
    	if(vector1.size() != vector2.size())
    		throw new IllegalArgumentException("One vector's size don't equal the other's");
    	double result = 0.0;
    	if(dimension < 1)
    		dimension = 1;
    	for(int i=0, vectorSize=vector1.size(); i<vectorSize; i++)
    		result+=Math.pow((vector1.get(i).doubleValue() - vector2.get(i).doubleValue()), dimension);
    	result = Math.pow(result, (1.0/((double)dimension)));
    	return result;
    }
	/**
	 * 初始化簇
	 * @param items
	 * @param k
	 * @return
	 */
	private Map<Item, List<Item>> initialize(List<Item> items, int k){
		Map<Item, List<Item>> cluster = new HashMap<Item, List<Item>>();
		List<Item> copies = new LinkedList<Item>(items);
		int i = 0;
		int randomIndex = -1;
		Item item = null;
		while(i < k) {
			randomIndex = (int)(Math.random() * copies.size());
			item = copies.remove(randomIndex);
			cluster.put(item, new LinkedList<Item>());
			i++;
		}
		return cluster;
	}
	/**
	 * 构造聚类用的向量
	 * @param texts
	 * @param totalSegments
	 * @param segmentsBag
	 * @return
	 */
	private List<Item> createItems(List<String> texts, List<List<String>> totalSegments, List<String> segmentsBag){
		List<Item> items = new LinkedList<Item>();
		Item item = null;
		for(int i = 0, size = totalSegments.size(); i < size; i++) {
			List<String> segments = totalSegments.get(i);
			List<Double> characteristicVector = createCharacteristicVector(segments, segmentsBag);
			item = new Item(texts.get(i), characteristicVector);
			items.add(item);
		}
		return items;
	}
	/**
	 * 构造特征向量<br><br>
	 * 
	 * 举例:<br>
	 * segments = [中华,人民,人民, 共和,国]<br>
	 * segmentBag = [中华,人民,共和,国,民主,政体]<br><br>
	 * 
	 * 假设此时不考虑逆文档频率和自订权重, 则返回<br>
	 * [1,2,1,1,0,0]<br><br>
	 * 
	 * 它表示对于segmentBag中列出的每个分词的分值<br>
	 * (此时由于只考虑词频, 因此在segments出现的次数就是分值)<br>
	 * [中华=1, 人民=2, 共和=1, 国=1, 民主=0, 政体=0]<br><br>
	 * 
	 * 如果考虑逆文档频率和自订权重, 则分值= 词频 * 逆文档频率 + 权重 
	 * 
	 * @param segments
	 * @param segmentBag
	 * @return
	 */
	private List<Double> createCharacteristicVector(List<String> segments, List<String> segmentBag){
		Map<String, Integer> termFrequency = termFrequency(segments);
		Map<String, Double> normalizedTermFrequency = normalizeTermFrequency(segments.size(), termFrequency);
		List<Double> characteristicVector = new ArrayList<Double>(segmentBag.size());
		String segment = null;
		double score = 0.0;
		for(int i = 0, size = segmentBag.size(); i < size; i++) {
			segment = segmentBag.get(i);
			score = segmentScore(segment, normalizedTermFrequency);
			characteristicVector.add(score);
		}
		return characteristicVector;
	}
	/**
	 * 给单个分词打分
	 * @param segment
	 * @return
	 */
	private double segmentScore(String segment, Map<String, Double> normalizedTermFrequency) {
		double tf = normalizedTermFrequency.getOrDefault(segment, 0.0);
		double idf = idfDictionary.getOrDefault(segment, 1.0);
		return tf * idf;
	}
	/**
	 * 统计单个语句各个分词的词频(Term Frequency)
	 * @param segments
	 * @return
	 */
	private Map<String, Integer> termFrequency(List<String> segments){
		Map<String, Integer> statistic = new HashMap<String, Integer>();
		String segment = null;
		for(int i = 0, size = segments.size(); i < size; i++) {
			segment = segments.get(i);
			statistic.put(segment, statistic.getOrDefault(segment, 0) + 1);
		}
		return statistic;
	}
	/**
	 * 归一化词频统计
	 * @param segmentsCount
	 * @param termFrequency
	 * @return
	 */
	private Map<String, Double> normalizeTermFrequency(int segmentsCount, Map<String, Integer> termFrequency){
		Map<String, Double> normalization = new HashMap<String, Double>();
		for(Entry<String, Integer> e : termFrequency.entrySet())
			normalization.put(e.getKey(), (double)e.getValue() / segmentsCount);
		return normalization;
	}
	/**
	 * 构造词袋
	 * @param segmentsInSentences
	 * @return
	 */
	private List<String> createSegmentsBag(List<List<String>> segmentsInSentences){
		Set<String> set = new HashSet<String>();
		for(int i = 0, size = segmentsInSentences.size(); i < size; i++) {
			List<String> segments = segmentsInSentences.get(i);
			set.addAll(segments);
		}
		List<String> union = new ArrayList<String>(set);
		return union;
	}
	/**
	 * 全语句分词
	 * @param texts
	 * @return
	 */
	private List<List<String>> totalSegmentation(List<String> texts){
		List<List<String>> totalSegments = new ArrayList<List<String>>(texts.size());
		for(int i = 0, size = texts.size(); i < size; i++) {
			List<String> segments = segmentation(texts.get(i));
			totalSegments.add(segments);
		}
		return totalSegments;
	}
	/**
	 * 分词
	 * @param text 文本
	 * @return List&ltString&gt 分词列表
	 */
	private List<String> segmentation(String text){
		JiebaSegmenter segmenter = new JiebaSegmenter();
		List<SegToken> segTokens = segmenter.process(text, SegMode.SEARCH);
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
	
	public static class Item{
		private String key;
		private List<Double> vector;
		
		public String key() {
			return key;
		}
		public List<Double> vector(){
			return vector;
		}
		
		public Item(String key, List<Double> vector) {
			this.key = key;
			this.vector = vector;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(vector);
		}
		
		@Override
		public boolean equals(Object object) {
			if(this == object)
				return true;
			if(!(object instanceof Item))
				return false;
			Item another = (Item) object;
			return this.vector.equals(another.vector);
		}
		
		@Override
		public String toString() {
			return String.format("{%s, %s}", key, vector);
		}
	}
}
