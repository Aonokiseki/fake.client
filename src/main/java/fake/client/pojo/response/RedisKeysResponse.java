package fake.client.pojo.response;

import java.util.Set;

public class RedisKeysResponse extends BasicResponse{
	
	private Set<String> keys;
	
	public Set<String> getKeys() {
		return keys;
	}
	public RedisKeysResponse setKeys(Set<String> keys) {
		this.keys = keys;
		return this;
	}
}
