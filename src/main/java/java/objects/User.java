package java.objects;

import java.io.Serializable;

public class User implements Serializable{
	public static final long serialVersionUID = 1;
	private String email;
	private String password;
	private String name;
	
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
