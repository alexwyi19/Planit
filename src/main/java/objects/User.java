package objects;

import java.io.Serializable;
import java.util.Set;

public class User implements Serializable{
	public static final long serialVersionUID = 1;
	private String email;
	private String password;
	private String name;
	private Set<Event> events;
	private int userID;
	
	public User() { }
	
//	public User()
//	{
//		email = null;
//		password = null;
//		userName = null;
//	}
//	public User(String email, String password) {
//		this.email = email;
//		this.password = password;
//	}
//	
	public User(String email, String password, String name,int userID) 
	{
		this.email = email;
		this.password = password;
		this.name = name;
		this.userID=userID;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public String getName()
	{
		return name;
	}

	/**
	 * @return the userID
	 */
	public int getUserID() {
		return userID;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}
	
}
