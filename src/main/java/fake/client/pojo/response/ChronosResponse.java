package fake.client.pojo.response;

import fake.client.pojo.data.Chronos;

public class ChronosResponse extends BasicResponse{
	private Chronos chronos;

	public Chronos getChronos() {
		return chronos;
	}
	public void setChronos(Chronos chronos) {
		this.chronos = chronos;
	}
}
