package objects;

import java.io.Serializable;
import java.util.List;

public class EventList implements Serializable {
	
	public static final long serialVersionUID = 1;
	private List<Event> joinedEvents;
	private List<Event> invitedEvents;
	private List<Event> createdEvents;
	
	public EventList() {}
	
	public EventList(List<Event> createdEvents2, List<Event> invitedEvents2, List<Event> joinedEvents2) {
		// TODO Auto-generated constructor stub
		createdEvents = createdEvents2;
		invitedEvents = invitedEvents2;
		joinedEvents = joinedEvents2;
	}
	public List<Event> getCreatedEvents() {
		return createdEvents;
	}
	public void setCreatedEvents(List<Event> createdEvents) {
		this.createdEvents = createdEvents;
	}
	public List<Event> getJoinedEvents() {
		return joinedEvents;
	}
	public void setJoinedEvents(List<Event> joinedEvents) {
		this.joinedEvents = joinedEvents;
	}
	public List<Event> getInvitedEvents() {
		return invitedEvents;
	}
	public void setInvitedEvents(List<Event> invitedEvents) {
		this.invitedEvents = invitedEvents;
	}
}
