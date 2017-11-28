package objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Event implements Serializable {
	public static final long serialVersionUID = 1;
	private int identifier; 
	private String name;
	private int duration;
	private Boolean isRecurring;
	private Boolean isPublic;
	private String URL;
	private User creator;
	private List<String> invitedEmails;
	private List<User> joinedUsers;
	private List<Interval> availabilityIntervals;
	private List<Availability> availabilities;
	
	public Event() {}
	
	
public Event(int identifier, String name, int duration, Boolean isRecurring, Boolean isPublic, String uRL,
			User creator, List<String> invitedEmails, List<User> joinedUsers, List<Interval> availabilityIntervals,
			List<Availability> availabilities) {
		super();
		this.identifier = identifier;
		this.name = name;
		this.duration = duration;
		this.isRecurring = isRecurring;
		this.isPublic = isPublic;
		URL = uRL;
		this.creator = creator;
		this.invitedEmails = invitedEmails;
		this.joinedUsers = joinedUsers;
		this.availabilityIntervals = availabilityIntervals;
		this.availabilities = availabilities;
	}


// Custom toString method if we need later on
//	@Override
//	public String toString() {
//		return new ToStringBuilder(this)
//				.append("id", id)
//				.append("name", name)
//				.append("duration", duration)
//				.append("isRecurring", isRecurring)
//				.append("isPublic", isPublic)
//				.append("url", url)
//				.append("creator", creator)
//				.append("invitedEmails", invitedEmails)
//				.append("joinedUsers", joinedUsers)
//				.append("availabilityIntervals", availabilityIntervals)
//				.append("availabilities", availabilities)
//				.toString();
//	}


	// Auto-generated getters/setters, change if needed
	public int getId() {
		return identifier;
	}

	public void setId(int id) {
		this.identifier = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public Boolean getIsRecurring() {
		return isRecurring;
	}

	public void setIsRecurring(Boolean isRecurring) {
		this.isRecurring = isRecurring;
	}

	public Boolean getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
	}

	public String getUrl() {
		return URL;
	}

	public void setUrl(String url) {
		this.URL = url;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public List<String> getInvitedEmails() {
		return invitedEmails;
	}

	public void setInvitedEmails(List<String> invitedEmails) {
		this.invitedEmails = invitedEmails;
	}

	public List<User> getJoinedUsers() {
		return joinedUsers;
	}

	public void setJoinedUsers(List<User> joinedUsers) {
		this.joinedUsers = joinedUsers;
	}

	public List<Interval> getAvailabilityIntervals() {
		return availabilityIntervals;
	}

	public void setAvailabilityIntervals(List<Interval> availabilityIntervals) {
		this.availabilityIntervals = availabilityIntervals;
	}

	public List<Availability> getAvailabilities() {
		return availabilities;
	}

	public void setAvailabilities(List<Availability> availabilities) {
		this.availabilities = availabilities;
	}

//	private String creator;
//	private String name;
//	private String type;
//	private List<String> invitedEmails;
//	//private SimpleInterval<Date> eventInterval;
//	private SimpleInterval<Date> suggestedInterval;
//	private Map<String, SimpleInterval<Date>> availabilities;
//	private List<Date> duration;
//	private Map<String,Boolean> joinedEvent;
//	private int eventID;
//	
//	
//
//
//	/**
//	 * @return the joinedEvent
//	 */
//	public Map<String, Boolean> getJoinedEvent() {
//		return joinedEvent;
//	}
//
//	/**
//	 * @param joinedEvent the joinedEvent to set
//	 */
//	public void setJoinedEvent(Map<String, Boolean> joinedEvent) {
//		this.joinedEvent = joinedEvent;
//	}
//
//	public List<Date> getDuration()
//	{
//		return duration;
//	}
//
//	public Event() { }
//	
//	public Event(String creator, String name, String type, 
//			List<String> invitedEmails,
//			SimpleInterval<Date> eventInterval,
//			Map<String,Boolean> joinedEvent,
//			int eventID)
//	{
//		this.creator = creator;
//		this.name = name;
//		this.type = type;
//		this.invitedEmails = invitedEmails;
//		this.joinedEvent = joinedEvent;
//	//	this.eventInterval = eventInterval;
//		this.eventID=eventID;
//	}
//	
//	/*
//	 *  Getters
//	 */
//	public String getCreator()
//	{
//		return creator;
//	}
//	
//	public String getName()
//	{
//		return name;
//	}
//	
//	public String getType()
//	{
//		return type;
//	}
//	
//	public Map<String, SimpleInterval<Date>> getAvailabilities()
//	{
//		return availabilities;
//	}
//	
////	public SimpleInterval<Date> getEventInterval()
////	{
////		return eventInterval;
////	}
//	
//	public List<String> getInvitedEmails()
//	{
//		return invitedEmails;
//	}
//	
//	public SimpleInterval<Date> getSuggestedInterval()
//	{
//		return suggestedInterval;
//	}
//	
//	/*
//	 * Setters
//	 */
//	public void addInvitedEmail(String email)
//	{
//		invitedEmails.add(email);
//	}
//	
////	public void setEventInterval()
////	{
////		
////		eventInterval = SimpleInterval.between(duration.get(0), duration.get(1));
////	}
//	
//	public void setSuggestedInterval(SimpleInterval<Date> interval)
//	{
//		suggestedInterval = interval;
//	}
//	
//	
//	
//	public void updateAvailability(String email, SimpleInterval<Date> availability)
//	{
//		if (availabilities == null)
//		{
//			availabilities = new HashMap<String, SimpleInterval<Date>>();
//		}
//		availabilities.put(email, availability);
//	}
//
//	/**
//	 * @return the eventID
//	 */
//	public int getEventID() {
//		return eventID;
//	}
//
//	/**
//	 * @param eventID the eventID to set
//	 */
//	public void setEventID(int eventID) {
//		this.eventID = eventID;
//	}
	
//	public String getTestString()
//	{
//		String test = "";
//		test += creatorEmail + "\n";
//		test += eventName + "\n";
//		test += invitedEmails.get(0) + "\n";
//		test += invitedEmails.get(1) + "\n";
//		return test;
//	}
}
