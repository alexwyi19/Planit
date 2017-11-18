package objects;

import java.io.Serializable;

public class User implements Serializable{
	public static final long serialVersionUID = 1;
	private String email;
	private String password;
	private String userName;
	
	public User()
	{
		email = null;
		password = null;
		userName = null;
	}
	public User(String email, String password) {
		this.email = email;
		this.password = password;
	}
	
	public User(String email, String password, String username) 
	{
		this.email = email;
		this.password = password;
		this.userName = username;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public String getUserName()
	{
		return userName;
	}
}
