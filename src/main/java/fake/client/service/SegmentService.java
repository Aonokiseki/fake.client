package fake.client.service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;

public class SegmentService {
	
	private Set<String> stopWords;
	
	public SegmentService() {
		stopWords = new HashSet<String>();
	}
	public Set<String> getStopWords() {
		return stopWords;
	}
	public SegmentService setStopWords(Set<String> stopWords) {
		this.stopWords = stopWords;
		return this;
	}
	
	public List<String> jieba(String text) {
		JiebaSegmenter segmenter = new JiebaSegmenter();
		List<SegToken> segTokens = segmenter.process(text, SegMode.SEARCH);
		List<String> segments = new LinkedList<String>();
		String segment = null;
		for(SegToken token : segTokens) {
			segment = token.word;
			if(segment.isEmpty())
				continue;
			if(segment.equals("\\s") || segment.equals("\\r") || segment.equals("\\n") || segment.equals("\\t"))
				continue;
			if(stopWords.contains(segment))
				continue;
			segments.add(token.word);
		}
		return segments;
	}
}
