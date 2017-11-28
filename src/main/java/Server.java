

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import org.json.JSONObject;

import objects.Event;
import objects.User;
/*
 * Server class to accept multithreaded sockets. Uses 
 * ClientHandler class within this file to communicate with 
 * each socket
 */
public class Server 
{
	private static final String DATABASE_URL = "https://planit-ea426.firebaseio.com/";
	private static final String ACCOUNT_JSON = "planitaccount.json";
	private ServerSocket ss;
	private Vector<ClientHandler> clientHandlers;
	private Connection conn = null;
	private Statement st = null;
	private ResultSet rs = null;

	/*
	 * Server constructor
	 */
	public Server() 
	{
		ss = null;
		try
		{
			ss = new ServerSocket(6789);
			clientHandlers = new Vector<ClientHandler>();
			// Initializing Firebase Admin SDK
			FileInputStream serviceAccount = new FileInputStream(ACCOUNT_JSON);
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost/planit?user=root&password=root&useSSL=false");
				st = conn.createStatement();
				while (true) 
				{
					Socket s = ss.accept();
					System.out.println("new client connected");
					ClientHandler ch = new ClientHandler(s, this);
					clientHandlers.add(ch);
				}
			}catch(SQLException sqle) {
				System.out.println("sqle: "+sqle.getMessage());
			}catch(ClassNotFoundException cnfe) {
				System.out.println("cnfe: "+ cnfe.getMessage());
			}finally {
				try {
					if (rs!=null) {
						rs.close();
					}
					if(st!=null) {
						st.close();
					}
					if(conn!=null) {
						conn.close();
					}
				}catch (SQLException sqle) {
					System.out.println("sqle: "+ sqle.getMessage());
				}
			}

		} 
		catch (IOException ioe)
		{
			System.out.println("in server constructor " + ioe.getMessage());
		}
		finally 
		{
			if (ss != null) 
			{
				try {
					ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
		}
	}
	public User verifyUser(String username, String password) {
		System.out.println("verifying user: " + username + " password: " + password);
		try {
			rs = st.executeQuery("SELECT * FROM Users WHERE username='"+username+"';");
			if (rs.next())
			{
				if (rs.getString("password").equals(password))
				{
					User u = new User(rs.getString("email"),rs.getString("password"),rs.getString("username"),rs.getInt("userID"));
					System.out.println("Verifying: " + username + " w/ given password: " + password);
					System.out.println("GOOD LOGIN INFO");
					return u;
				}
				else
				{
					System.out.println("BAD LOGIN INFO");
				}
			}
			else
			{
				System.out.println("USER DOESNT EXIST");
			}
		}catch(SQLException sqle) {
			System.out.println("sqle: "+sqle.getMessage());
		}
		return null;
	}
	public User createUser(String username, String password,String email) {
		User u=null;
		System.out.println("Creating: " + email + " " + username+" "+password);
		ResultSet rUser=null;
		ResultSet rEmail=null;
		ResultSet rID = null;
		Statement sUser = null;
		Statement sEmail =null;
		Statement sID = null;
		Statement s = null;
		try {
			sUser = conn.createStatement();
			sEmail = conn.createStatement();
			sID = conn.createStatement();
			s=conn.createStatement();
			System.out.println("HERE1");
			rUser = sUser.executeQuery("SELECT * FROM Users WHERE username='"+username+"';");
			System.out.println("HERE2");
			rEmail = sEmail.executeQuery("SELECT * FROM Users WHERE email='"+email+"';");
			System.out.println("HERE3");
			if(rUser.next()){
				System.out.println("HERE4");
				System.out.println("Already existing username");
			}
			else if(rEmail.next()) {
				System.out.println("HERE6");
				System.out.println("Already existing email");
			}
			else {
				System.out.println("HERE7");
				s.execute("INSERT INTO Users (username,password,email) VALUES ('"+username+"','"+password+"','"+email+"');");
				System.out.println("HERE8");
				rID = sID.executeQuery("SELECT * FROM Users WHERE username='"+username+"';");
				rID.next();
				System.out.println("HERE9");
				u = new User(email,password,username, rID.getInt("userID"));
				System.out.println("HERE10");
			}
		}catch(SQLException sqle) {
			System.out.println("sqle: "+sqle.getMessage());
		}finally {
			try {
				if (rUser!=null) {
					rUser.close();
				}
				if(rEmail!=null) {
					rEmail.close();
				}
				if(sUser!=null) {
					sUser.close();
				}
				if(sEmail!=null) {
					sEmail.close();
				}
				if(rID!=null) {
					rID.close();
				}
				if(sID!=null) {
					sID.close();
				}
				if(s!=null) {
					s.close();
				}
				
			}catch (SQLException sqle) {
				System.out.println("sqle: "+ sqle.getMessage());
			}
		}
		System.out.println("HERE11");
		return u;

		
	}
	public void createNewEvent(Event event) {
		
	}
	
//	public Event getEvent(int id) {
//		Event e=null;
//		ResultSet rEvent = null;
//		Statement sEvent = null;
//		try {
//			sEvent=conn.createStatement();
//			rEvent = sEvent.executeQuery("SELECT * FROM Events WHERE eventID='"+id+"';");
//			
//			
//		}catch(SQLException sqle) {
//			System.out.println("sqle: "+sqle.getMessage());
//		}finally {
//			try {
//				if (rEvent!=null) {
//					rEvent.close();
//				}
//				if(sEvent!=null) {
//					sEvent.close();
//				}
//			}catch (SQLException sqle) {
//				System.out.println("sqle: "+ sqle.getMessage());
//			}
//		}
//		return e;
//	}
	
	public User getUser(int id) {
		User u=null;
		ResultSet rUser = null;
		Statement sUser = null;
		try {
			sUser=conn.createStatement();
			rUser = sUser.executeQuery("SELECT * FROM Users WHERE userID='"+id+"';");
			u = new User(rUser.getString("email"),rUser.getString("password"),rUser.getString("username"),rUser.getInt("userID")); 
		}catch(SQLException sqle) {
			System.out.println("sqle: "+sqle.getMessage());
		}finally {
			try {
				if (rUser!=null) {
					rUser.close();
				}
				if(sUser!=null) {
					sUser.close();
				}
			}catch (SQLException sqle) {
				System.out.println("sqle: "+ sqle.getMessage());
			}
		}
		return u;
	}
	
	public static void main(String [] args) 
	{
		Server server = new Server();
	}
}

/*
 *  Client handler class to communicate with multiple client requests
 */
class ClientHandler extends Thread
{
	private Server server;
	private Socket socket;
	private DataOutputStream out;
	private DataInputStream in;
//	private BufferedReader in;
//	private PrintWriter out;
	public ClientHandler(Socket socket, Server server) 
	{
		out = null;
		in = null;
		this.server = server;
		this.socket = socket;
		try
		{
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			//out = new PrintWriter(socket.getOutputStream());
			//in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			this.start();
		}
		catch (IOException ioe) 
		{
			System.out.println("in clienthandler constructor " + ioe.getMessage());
		}
			
	}
	
	public void run() 
	{

		while (true) 
		{
			try 
			{
						
//				GsonBuilder gsonBuilder = new GsonBuilder();
				//Gson gson = new GsonBuilder().setPrettyPrinting().create();
//				Gson gson = gsonBuilder.create();
//				JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
//				String obj =  reader.toString();
				
				// Right now, this just gets a JSON string
//				String fromClient = in.readUTF();
//				System.out.println("fromClient: "+fromClient);
//				JSONObject obj = new JSONObject(fromClient);
//				
				Integer dataLength = in.readInt();
				byte[] bytes = new byte[dataLength];
				in.readFully(bytes);
				String data = new String(bytes,"UTF-8");
				System.out.println(data);
				JSONObject obj = new JSONObject(data);
				
				

//				if (fromClient != null) {
//					System.out.println("FromClient: " + fromClient);
//				}
//				
				
				System.out.println("THIS IS FROM THE CLIENT:");
				System.out.println(obj);
				// Parse JSON string to object
//				GsonBuilder gsonBuilder = new GsonBuilder();
//				gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer());
				//Gson gson = new GsonBuilder().setPrettyPrinting().create();
//				Gson gson = gsonBuilder.create();

				
				String type = obj.getString("type");
				if(type.equals("login")) {
					System.out.println("IN THIS FUNCTION");
					User u = server.verifyUser(obj.getString("username"), obj.getString("password"));
					System.out.println("FINISHED FUNCTION");
					System.out.println(u.getName()+ " " + u.getEmail());
					//WE NEED TO SEND USER U
				}
				if(type.equals("signup")) {
					User u =server.createUser(obj.getString("username"),obj.getString("password"),obj.getString("email"));
					System.out.println(u.getName()+ " " + u.getEmail());
					//WE NEED TO SEND USER U
				}
				if(type.equals("getevents")) {
//					Event e = server.getEvents(obj.getInt("id"));
				}
				if(type.equals("getuser")) {
					User u = server.getUser(obj.getInt("id"));
				}
				if(type.equals("createevent")) {
					
				}
				if(type.equals("joinevent")) {
					
				}
//				
//				if (fromClient.contains("password"))
//				{
//					User user = gson.fromJson(fromClient, User.class);
//					// Test message
//					System.out.println("inside clienthandler run() " + 
//							user.getEmail() + " " + user.getPassword()
//							+ " " + user.getName());
//					
//					// Check if login credentials are right.
//					server.verifyUser(user);
//					// Create user object and send to server. Server will do the
//					// logic to create a user
//					server.createUser(user);	
//				}
//				
//				if (fromClient.contains("creator"))
//				{
//					// Test message
//					System.out.println("inside clienthandler run()" +
//							fromClient);
//					Event event = gson.fromJson(fromClient, Event.class);
//					//event.setEventInterval();
//					server.createNewEvent(event);
//				}
			} 
			catch (IOException ioe) 
			{
				System.out.println("in clienthandler.run() " + ioe.getMessage());
				ioe.printStackTrace();
			}
		}	
	}	
}
