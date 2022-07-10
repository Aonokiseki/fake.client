package fake.client.pojo.response;

public class BasicResponse implements IBasicResponse{
	
	private int code;
	private String message;
	private String result;
	
	public BasicResponse setCode(int code) {
		this.code = code;
		return this;
	}
	public BasicResponse setMessage(String message) {
		this.message = message;
		return this;
	}
	public BasicResponse setResult(String result) {
		this.result = result;
		return this;
	}
	@Override
	public int getCode() {
		return code;
	}
	@Override
	public String getMessage() {
		return message;
	}
	@Override
	public String getResult() {
		return result;
	}
}
