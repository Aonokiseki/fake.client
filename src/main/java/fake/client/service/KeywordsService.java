package fake.client.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
import com.huaban.analysis.jieba.SegToken;

public class KeywordsService {
	
	private Map<String, Double> idfDictionary;
	private Set<String> stopwords;
	private Map<String, Double> weightDictionary;
	
	public KeywordsService() {
		this.idfDictionary = new HashMap<String, Double>();
		this.stopwords = new HashSet<String>();
		this.weightDictionary = new HashMap<String, Double>();
	}
	
	public KeywordsService setIdfDictionary(Map<String, Double> idfDictionary) {
		this.idfDictionary = idfDictionary;
		return this;
	}
	public Map<String, Double> idfDictionary(){
		return Collections.unmodifiableMap(idfDictionary);
	}
	
	public KeywordsService setStopWords(Set<String> stopwords) {
		this.stopwords = stopwords;
		return this;
	}
	public Set<String> stopwords(){
		return Collections.unmodifiableSet(stopwords);
	}
	
	public KeywordsService setWeightDictionary(Map<String, Double> weightDictionary) {
		this.weightDictionary = weightDictionary;
		return this;
	}
	public Map<String, Double> weightDictionary(){
		return Collections.unmodifiableMap(weightDictionary);
	}
	/**
	 * 处理
	 * @param text
	 * @param count
	 * @return
	 */
	public List<String> process(String text, int count){
		List<String> segments = segments(text);
		Map<String, Integer> termFrequency = termFrequency(segments);
		Map<String, Double> scores = termScoring(termFrequency);
		List<Entry<String, Double>> sorted = sorting(scores);
		return cutNSegments(sorted, count);
	}
	/**
	 * 截取前N个分词
	 * @param sorted
	 * @param count
	 * @return
	 */
	private List<String> cutNSegments(List<Entry<String, Double>> sorted, int count){
		if(count < 1) 
			count = 1;
		int k = Math.min(count, sorted.size());
		List<String> topSegments = new ArrayList<String>(k);
		for(int i = 0; i < count; i++)
			topSegments.add(sorted.get(i).getKey());
		return topSegments;
	}
	
	/**
	 * 按分值排序
	 * @param scores
	 * @return
	 */
	private List<Entry<String, Double>> sorting(Map<String, Double> scores){
		List<Entry<String, Double>> entries = new ArrayList<Entry<String, Double>>(scores.entrySet());
		Collections.sort(entries, new Comparator<Entry<String, Double>>(){
			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		return entries;
	}
	/**
	 * 所有分词打分
	 * @param termFrequency
	 * @return
	 */
	private Map<String, Double> termScoring(Map<String, Integer> termFrequency){
		Map<String, Double> scores = new HashMap<String, Double>();
		String segment = null;
		double score = 0.0, idf = 0.0, weight = 0.0;
		int frequency = 0;
		for(Entry<String, Integer> e : termFrequency.entrySet()) {
			segment = e.getKey();
			frequency = e.getValue();
			idf = idfDictionary.getOrDefault(segment, 1.0);
			weight = weightDictionary.getOrDefault(segment, 0.0);
			score = frequency * idf + weight;
			scores.put(segment, score);
		}
		return scores;
	}
	/**
	 * 统计词频
	 * @param segments
	 * @return
	 */
	private Map<String, Integer> termFrequency(List<String> segments){
		Map<String, Integer> termFrequency = new HashMap<String, Integer>();
		String segment = null;
		for(int i = 0, size = segments.size(); i < size; i++) {
			segment = segments.get(i);
			termFrequency.put(segment, termFrequency.getOrDefault(segment, 0) + 1);
		}
		return termFrequency;
	}
	/**
	 * 全文分词
	 * @param sentence 句子
	 * @return List&ltString&gt 分词列表
	 */
	private List<String> segments(String text){
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
}
