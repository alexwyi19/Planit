package objects;

import java.io.Serializable;

public class Availability implements Serializable {
	
	public static final long serialVersionUID = 1;
	private User user;
	private Interval interval;
	
	public Availability() {}
	
	public Availability(User userAva, Interval interval2) {
		// TODO Auto-generated constructor stub
		user = userAva;
		interval = interval2;
	}
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