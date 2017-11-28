package objects;

import java.io.Serializable;

public class Interval implements Serializable {
	
	public static final long serialVersionUID = 1;
	private long start;
	private int duration;
	
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
}
