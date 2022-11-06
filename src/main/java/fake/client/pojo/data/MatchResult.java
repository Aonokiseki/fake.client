package fake.client.pojo.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatchResult {
	private boolean isFind;
	private boolean isMatches;
	private List<Group> groups;
	
	public MatchResult() {
		this.groups = new ArrayList<Group>();
	}
	
	public MatchResult setIsFind(boolean isFind) {
		this.isFind = isFind;
		return this;
	}
	public boolean isFind() {
		return this.isFind;
	}
	public MatchResult setGroups(List<Group> groups) {
		this.groups = groups;
		return this;
	}
	public List<Group> groups(){
		return Collections.unmodifiableList(this.groups);
	}
	public MatchResult setIsMatches(boolean isMatches) {
		this.isMatches = isMatches;
		return this;
	}
	public boolean isMatches() {
		return this.isMatches;
	}

	@Override
	public String toString() {
		return "{isFind=" + isFind + ", isMatches=" + isMatches + ", groups=" + groups + "}";
	}
	
	public static class Group{
		private int start;
		private int end;
		private String groupZero;
		
		public Group() {}
		
		public int start() {
			return start;
		}
		public Group setStart(int start) {
			this.start = start;
			return this;
		}
		public int end() {
			return end;
		}
		public Group setEnd(int end) {
			this.end = end;
			return this;
		}
		public String groupZero() {
			return groupZero;
		}
		public Group setGroupZero(String groupZero) {
			this.groupZero = groupZero;
			return this;
		}

		@Override
		public String toString() {
			return "{start=" + start + ", end=" + end + ", groupZero=" + groupZero + "}";
		}
	}
}
