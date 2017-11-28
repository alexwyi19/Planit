package objects;

import java.io.Serializable;

public class Interval implements Serializable {
	
	public static final long serialVersionUID = 1;
	private String start;
	private String duration;
	
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
}
