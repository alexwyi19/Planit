package objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.time4j.range.SimpleInterval;

public class Event implements Serializable {
	public static final long serialVersionUID = 1;
	private String creator;
	private String name;
	private String type;
	private List<String> invitedEmails;
	//private SimpleInterval<Date> eventInterval;
	private SimpleInterval<Date> suggestedInterval;
	private Map<String, SimpleInterval<Date>> availabilities;
	private List<Date> duration;
	public List<Date> getDuration()
	{
		return duration;
	}

	public Event() { }
	
	public Event(String creator, String name, String type, 
			List<String> invitedEmails,
			SimpleInterval<Date> eventInterval)
	{
		this.creator = creator;
		this.name = name;
		this.type = type;
		this.invitedEmails = invitedEmails;
	//	this.eventInterval = eventInterval;
	}
	
	/*
	 *  Getters
	 */
	public String getCreator()
	{
		return creator;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getType()
	{
		return type;
	}
	
	public Map<String, SimpleInterval<Date>> getAvailabilities()
	{
		return availabilities;
	}
	
//	public SimpleInterval<Date> getEventInterval()
//	{
//		return eventInterval;
//	}
	
	public List<String> getInvitedEmails()
	{
		return invitedEmails;
	}
	
	public SimpleInterval<Date> getSuggestedInterval()
	{
		return suggestedInterval;
	}
	
	/*
	 * Setters
	 */
	public void addInvitedEmail(String email)
	{
		invitedEmails.add(email);
	}
	
//	public void setEventInterval()
//	{
//		
//		eventInterval = SimpleInterval.between(duration.get(0), duration.get(1));
//	}
	
	public void setSuggestedInterval(SimpleInterval<Date> interval)
	{
		suggestedInterval = interval;
	}
	
	
	
	public void updateAvailability(String email, SimpleInterval<Date> availability)
	{
		if (availabilities == null)
		{
			availabilities = new HashMap<String, SimpleInterval<Date>>();
		}
		availabilities.put(email, availability);
	}
	
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
