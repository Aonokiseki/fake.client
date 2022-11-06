package fake.client.debug;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Segmentation {
	
	public static void main(String[] args) {
		Segmentation segmentation = new Segmentation();
		List<Segment> res = segmentation.process("帝国主义要把我们的地瓜分掉分结婚的和尚未结婚的和尚");
		System.out.println(res);
	}
	
	private Map<String, Integer> dictionary;
	private Map<String, String> markDictionary;
	private long totalFrequency;
	
	private final static String DEFAULT_SENTENCE_SEPARATOR_REGEX = "[!?！;？。；]";
	private final static String DEFAULT_BLOCK_SEPARATOR_REGEX = "([\\u4E00-\\u9FD5a-zA-Z0-9+#&\\._]+)";
	private final static String DETAILS_SEPARATOR_REGEX_IN_BUFFER = "([\\u4E00-\\u9FD5]|[\\.0-9]+|[a-zA-Z0-9]+)";
	private final static String NUMBER_REGEX = "[\\.0-9]+";
	private final static String ENGLISH_REGEX = "[a-zA-Z]+";
	private final static String DEFAULT_DICT = "E:\\Download\\fake.client\\dict\\tf.txt";
	
	public Segmentation() {
		this.dictionary = new HashMap<String, Integer>();
		this.markDictionary = new HashMap<String, String>();
		this.totalFrequency = 0;
		try {
			initialize();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initialize() throws NumberFormatException, IOException {
		String line = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(DEFAULT_DICT)));
		String[] parts = null;
		while((line = reader.readLine()) != null) {
			if(line.trim().isEmpty())
				continue;
			parts = line.split("\\s");
			String segment = parts[0];
			int frequency = (parts.length < 2) ? 1 : Integer.parseInt(parts[1]);
			String mark = (parts.length < 3) ? "x" : parts[2];
			dictionary.put(segment, frequency);
			markDictionary.put(segment, mark);
			totalFrequency += frequency;
		}
		reader.close();
	}
	
	
	public List<Segment> process(String text){
		List<Segment> globalSegments = new LinkedList<Segment>();
		List<String> sentences = splitText(text);
		for(int i = 0; i < sentences.size(); i++) {
			List<String> blocks = splitSentence(sentences.get(i));
			for(int j = 0; j < blocks.size(); j++) {
				List<Segment> segments = processBlock(blocks.get(j));
				globalSegments.addAll(segments);
			}
		}
		return globalSegments;
	}
	
	private List<String> splitText(String text){
		List<String> sentences = new LinkedList<String>();
		if(text == null || text.trim().isEmpty())
			return sentences;
		String[] array = text.split(DEFAULT_SENTENCE_SEPARATOR_REGEX);
		for(String sentence : array)
			sentences.add(sentence);
		return sentences;
	}
	
	private List<String> splitSentence(String sentence){
		List<String> blocks = new LinkedList<String>();
		if(sentence == null || sentence.trim().isEmpty())
			return blocks;
		Pattern pattern = Pattern.compile(DEFAULT_BLOCK_SEPARATOR_REGEX);
		Matcher matcher = pattern.matcher(sentence);
		while(matcher.find())
			blocks.add(matcher.group(0));
		return blocks;
	}
	
	private List<Segment> processBlock(String block){
		List<Segment> segments = new LinkedList<Segment>();
		if(block == null || block.trim().isEmpty())
			return segments;
		Map<Integer, List<Integer>> dag = createDAG_debug(block);
		Map<Integer, Pair> route = calculateMaxWeight(block, dag);
		int length = block.length();
		int left = 0;
		StringBuilder buffer = new StringBuilder();
		while(left < length) {
			int right = route.get(left).end + 1;
			String fragment = block.substring(left, right);
			if(right - left == 1) {
				buffer.append(fragment);
			}else {
				if(buffer.length() > 0) {
					processBuffer(buffer, segments);
					buffer.delete(0, buffer.length());
				}
				String mark = markDictionary.getOrDefault(fragment, "x");
				segments.add(new Segment().setString(fragment).setMark(mark));
			}
			left = right;
		}
		if(buffer.length() > 0)
			processBuffer(buffer, segments);
		return segments;
	}
	
	private void processBuffer(StringBuilder buffer, List<Segment> segments) {
		if(buffer.length() == 1) {
			segments.add(new Segment().setString(buffer.toString()).setMark("x"));
			return;
		}
		String allStringInBuffer = buffer.toString();
		if(!dictionary.containsKey(allStringInBuffer)) {
			List<Segment> tiny = cutDetails(buffer);
			segments.addAll(tiny);
			return;
		}
		for(int i = 0; i < buffer.length(); i++) {
			String fragment = allStringInBuffer.substring(i, i + 1);
			segments.add(new Segment().setString(fragment).setMark("x"));
		}
	}
	
	private List<Segment> cutDetails(StringBuilder buffer) {
		Pattern pattern = Pattern.compile(DETAILS_SEPARATOR_REGEX_IN_BUFFER);
		Matcher matcher = pattern.matcher(buffer.toString());
		List<Segment> result = new LinkedList<Segment>();
		while(matcher.find()) {
			String detail = matcher.group();
			if(Pattern.matches(ENGLISH_REGEX, detail)) {
				result.add(new Segment().setString(detail).setMark("eng"));
				continue;
			}
			if(Pattern.matches(NUMBER_REGEX, detail)) {
				result.add(new Segment().setString(detail).setMark("m"));
				continue;
			}
			result.add(new Segment().setString(detail).setMark("x"));
		}
		return result;
	}
	
	@SuppressWarnings("unused")
	private Map<Integer, List<Integer>> createDAG(String block){
		Map<Integer, List<Integer>> dag = new HashMap<Integer, List<Integer>>();
		if(block == null || block.trim().isEmpty())
			return dag;
		List<Integer> neighbours = null;
		for(int left = 0, length = block.length(); left < length; left++) {
			neighbours = new LinkedList<Integer>();
			String fragment = block.substring(left, left + 1);
			int right = left;
			while(right < length && dictionary.containsKey(fragment)) {
				if(dictionary.getOrDefault(fragment, 0) > 0) {
					neighbours.add(right);
				}
				right++;
				if(right >= length)
					break;
				fragment = block.substring(left, right + 1);
			}
			if(neighbours.isEmpty())
				neighbours.add(left);
			dag.put(left, neighbours);
		}
		return dag;
	}
	
	private Map<Integer, List<Integer>> createDAG_debug(String block){
		Map<Integer, List<Integer>> dag = new HashMap<Integer, List<Integer>>();
		if(block == null || block.trim().isEmpty())
			return dag;
		List<Integer> neighbours = null;
		for(int left = 0, length = block.length(); left < length; left++) {
			neighbours = new LinkedList<Integer>();
			String fragment = block.substring(left, left + 1);
			int right = left;
			while(right < length) {
				if(dictionary.getOrDefault(fragment, 0) > 0) {
					neighbours.add(right);
				}
				right++;
				if(right >= length)
					break;
				fragment = block.substring(left, right + 1);
			}
			if(neighbours.isEmpty())
				neighbours.add(left);
			dag.put(left, neighbours);
		}
		return dag;
	}
	
	private Map<Integer, Pair> calculateMaxWeight(String block, Map<Integer, List<Integer>> dag){
		Map<Integer, Pair> route = new HashMap<Integer, Pair>();
		int length = block.length();
		route.put(length, new Pair(length, 0, 0.0));
		double totalLnFrequency = Math.log(totalFrequency);
		String fragment = null;
		for(int start = length - 1; start >= 0; start--) {
			Pair max = null;
			for(int neighbour : dag.get(start)) {
				fragment = block.substring(start, neighbour + 1);
				double fragmentLnFrequency = Math.log(dictionary.getOrDefault(fragment, 1));
				double previousWeight = route.get(neighbour + 1).weight;
				double fragmentWeight = fragmentLnFrequency - totalLnFrequency + previousWeight;
				Pair fragmentInfo = new Pair().setStart(start).setEnd(neighbour).setWeight(fragmentWeight);
				if(max == null) {
					max = fragmentInfo;
					route.put(start, fragmentInfo);
					continue;
				}
				if(fragmentInfo.weight > max.weight) {
					route.put(start, fragmentInfo);
				}else {
					route.put(start, max);
				}
			}
		}
		return route;
	}
	
	public static class Pair{
		private int start;
		private int end;
		private double weight;
		
		public Pair() {}
		public Pair(int start, int end, double weight) {
			this.start = start;
			this.end = end;
			this.weight = weight;
		}
		public int getStart() {
			return start;
		}
		public Pair setStart(int start) {
			this.start = start;
			return this;
		}
		public int getEnd() {
			return end;
		}
		public Pair setEnd(int end) {
			this.end = end;
			return this;
		}
		public double getWeight() {
			return weight;
		}
		public Pair setWeight(double weight) {
			this.weight = weight;
			return this;
		}
		@Override
		public String toString() {
			return "Pair [start=" + start + ", end=" + end + ", weight=" + weight + "]";
		}
	}
	
	public static class Segment{
		private String string;
		private String mark;
		
		public Segment() {}
		public String getString() {
			return string;
		}
		public Segment setString(String string) {
			this.string = string;
			return this;
		}
		public String getMark() {
			return mark;
		}
		public Segment setMark(String mark) {
			this.mark = mark;
			return this;
		}
		@Override
		public String toString() {
			return "{string=" + string + ", mark=" + mark + "}";
		}
	}
}
