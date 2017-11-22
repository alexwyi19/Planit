package objects;

import java.io.Serializable;
import java.util.Set;

public class User implements Serializable{
	public static final long serialVersionUID = 1;
	private String email;
	private String password;
	private String name;
	private Set<Event> events;
	
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
	public User(String email, String password, String name) 
	{
		this.email = email;
		this.password = password;
		this.name = name;
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
}
