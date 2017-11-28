package objects;

import java.io.Serializable;

public class Availability implements Serializable {
	
	public static final long serialVersionUID = 1;
	private User user;
	private Interval interval;
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Interval getInterval() {
		return interval;
	}
	public void setInterval(Interval interval) {
		this.interval = interval;
	}
}