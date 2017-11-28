

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONObject;

import com.google.gson.Gson;

import objects.Availability;
import objects.Event;
import objects.EventList;
import objects.Interval;
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
	public String createNewEvent(Event event) {
		String res = "true";
		Statement sEvent = null;
		Statement sJoin = null;
//		Statement sInvite = null;
//		Statement sInterval = null;
//		Statement sAvail = null;

		try {
			sEvent=conn.createStatement();
			sJoin = conn.createStatement();
//			sInvite = conn.createStatement();
//			sInterval = conn.createStatement();
//			sAvail = conn.createStatement();

			String name = event.getName();
			int duration = event.getDuration();
			boolean isRecurring = event.getIsRecurring();
			boolean isPublic = event.getIsPublic();
			String URL = event.getUrl();
			User c = event.getCreator();
			int userID = c.getUserID();
			ArrayList<String> invitedEmails = (ArrayList<String>) event.getInvitedEmails();
			ArrayList<User> joinedUsers = (ArrayList<User>) event.getJoinedUsers();
			ArrayList<Interval>  availabilityIntervals = (ArrayList<Interval>) event.getAvailabilityIntervals();
			ArrayList<Availability> availabilities = (ArrayList<Availability>) event.getAvailabilities();
			String insertEvent = "INSERT INTO Events (name, creator, userID, url, duration,isRecurring,isPublic) VALUES('"+name+"','"+c.getName()+"','"+userID+"','"+URL+"','"+duration+"','"+isRecurring+"','"+isPublic+"');";


	        sEvent.executeUpdate(insertEvent,Statement.RETURN_GENERATED_KEYS);
	        rs = sEvent.getGeneratedKeys();
	        rs.next();
	        int key = rs.getInt(1);
	        System.out.println("THIS IS THE KEY " + key);

			sJoin.executeUpdate("INSERT INTO joinedEvent (userID, eventID) VALUES"
					+ "('"+userID+"','"+key+"');");

			if(invitedEmails!=null) {
				for(int i=0;i<invitedEmails.size();i++) {
					Statement sInvite = null;
					try {
						sInvite = conn.createStatement();
						sInvite.executeUpdate("INSERT INTO invitedEmails (emails,eventID) VALUES"
								+"('"+invitedEmails.get(i)+"','"+key+"');");

					}catch(SQLException sqle) {
						System.out.println("sqle: "+sqle.getMessage());
					}finally {
						try {
							if (sInvite!=null) sInvite.close();
						}catch (SQLException sqle) {
							System.out.println("sqle: "+ sqle.getMessage());
						}
					}
				}
			}

			if(availabilityIntervals!=null) {
				for(int i=0;i<availabilityIntervals.size();i++) {
					Statement sInterval = null;
					try {
						sInterval = conn.createStatement();
						sInterval.executeUpdate("INSERT INTO availabilityIntervals (start,end,eventID) VALUES"
								+"('"+availabilityIntervals.get(i).getStart()+"','"+availabilityIntervals.get(i).getDuration()+"','"+key+"');");

					}catch(SQLException sqle) {
						System.out.println("sqle: "+sqle.getMessage());
					}finally {
						try {
							if (sInterval!=null) sInterval.close();
						}catch (SQLException sqle) {
							System.out.println("sqle: "+ sqle.getMessage());
						}
					}
				}
			}
			if(availabilities!=null) {
				for(int i=0;i<availabilities.size();i++) {
					Statement sAvail = null;
					try {
						sAvail = conn.createStatement();
						sAvail.executeUpdate("INSERT INTO availabilities (userID,start,end,eventID) VALUES"
								+"('"+availabilities.get(i).getUser().getUserID()+"','"+availabilities.get(i).getInterval().getStart()+"','"+availabilities.get(i).getInterval().getDuration()+"','"+key+"');");

					}catch(SQLException sqle) {
						System.out.println("sqle: "+sqle.getMessage());
					}finally {
						try {
							if (sAvail!=null) sAvail.close();
						}catch (SQLException sqle) {
							System.out.println("sqle: "+ sqle.getMessage());
						}
					}
				}
			}


		}catch(SQLException sqle) {
			System.out.println("sqle: "+sqle.getMessage());
		}finally {
			try {
				if (sEvent!=null) sEvent.close();
				if (sJoin!=null) sJoin.close();
//				if (sInvite!=null) sInvite.close();
//				if (sInterval!=null) sInterval.close();
//				if (sAvail!=null) sAvail.close();
			}catch (SQLException sqle) {
				System.out.println("sqle: "+ sqle.getMessage());
			}
		}

		return res;
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
	
	public Event updateEvent(Event event, ClientHandler ch)
	{
		Event e = null;
		ResultSet rEvent = null;
		Statement sEvent = null;
		try {
			sEvent=conn.createStatement();
			sEvent.execute("INSERT INTO joinedEvent (userID,eventID) VALUES ('"+ch.uid + "','"+event.getId()+"');");
		}catch(SQLException sqle) {
			System.out.println("sqle: "+sqle.getMessage());
		}finally {
			try {
				if (rEvent!=null) {
					rEvent.close();
				}
				if(sEvent!=null) {
					sEvent.close();
				}
			}catch (SQLException sqle) {
				System.out.println("sqle: "+ sqle.getMessage());
			}
		}
		return e;
	}
	
//	public EventList getEventsList(int uid)
//	{
//		EventList el = null;
//		Event e = new Event(1,
//				"alex",
//				100,
//				true,
//				true,
//				"url",
//				new User("str","str","str",1),
//				new ArrayList(),
//				new ArrayList(),
//				new ArrayList(),
//				new ArrayList());
//		
//
//		List<Event> temp1 = new ArrayList();
//		temp1.add(e);
//		List<Event> temp2 = new ArrayList();
//		temp2.add(e);
//		List<Event> temp3 = new ArrayList();
//		temp3.add(e);
//	
//		el = new EventList();
//		el.setCreatedEvents(temp1);
//		el.setInvitedEvents(temp2);
//		el.setJoinedEvents(temp3);
//		
//		return el;
//	}
	public EventList getEventsList(int userId) throws SQLException {
	    //Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/Planit?user=root&password=mysqlCG2017&useSSL=false");
	    //For use of invited emails
	    User u = this.getUser(userId);
		ResultSet rs=null;
		Statement statement = null;
		EventList eventList = null;
		List<Event> joinedEvents = new ArrayList<>();
		List<Event> invitedEvents = new ArrayList<>();
		List<Event> createdEvents = new ArrayList<>();
		//Get CreatedEvent first
		try {
			Gson gson = new Gson();
			String userName = u.getName();
			int userID = u.getUserID();
			statement = conn.createStatement();
			ResultSet rs0 = statement.executeQuery("SELECT * FROM Events WHERE creator='"+userName+"' AND userID='" + userID + ";");
			//String oneEvent = null;
			while(rs0.next()) {
				int eventId = rs0.getInt("eventID");
				String eventName  = rs0.getString("name");
				//String creatorName  = rs.getString("creator");
				int userIdRs = rs0.getInt("userID");
				String url = rs0.getString("url");
				int duration = rs0.getInt("duration");
				boolean isRecurring = Boolean.parseBoolean(rs0.getString("isRecurring"));
				boolean isPublic = Boolean.parseBoolean(rs0.getString("isPublic"));
				User creator = this.getUser(userIdRs);
				//Event event = gson.fromJson(fromClient, Event.class);
				//Get invited emails list for this event
				ResultSet rsInvitedEmails = statement.executeQuery("SELECT emails FROM invitedEmails WHERE eventID='" + eventId + "';");
				List<String> invitedEmails = new ArrayList<>();
				while(rsInvitedEmails.next()) {
					invitedEmails.add(rsInvitedEmails.getString("emails"));
				}
				ResultSet rsJoinedUsers = statement.executeQuery("SELECT userID FROM Events WHERE eventID='" + eventId + "';");
				List<User> joinedUsers = new ArrayList<>();
				while (rsJoinedUsers.next()) {
					joinedUsers.add(this.getUser(rsJoinedUsers.getInt("userID")));
				}
				//Get availability intervals
				ResultSet rsAvailabilityIntervals = statement.executeQuery("SELECT * FROM availabilityIntervals a, Events e WHERE a.eventID = e.eventID AND eventID='" + eventId + "';");
				List<Interval> availabilityIntervals = new ArrayList<>();
				while (rsAvailabilityIntervals.next()) {
					int start = rsAvailabilityIntervals.getInt("start");
					int end = rsAvailabilityIntervals.getInt("end");
					String jsonStringInterval = " {'Interval': {'start':" +  start + "'end':" + end + "} }";
					Interval interval = gson.fromJson(jsonStringInterval, Interval.class);	
					availabilityIntervals.add(interval);
				}
				
				//Get availabilities of joined users
				List<Availability> availabilities = new ArrayList<>();
				for (User user : joinedUsers) {
					int userIdAva = user.getUserID();
					ResultSet rsAvailabilities = statement.executeQuery("SELECT * FROM availabilities WHERE " + "userID='" + userIdAva + "';");
					while (rsAvailabilities.next()) {
						User userAva = this.getUser(rsAvailabilities.getInt("userID"));
						String start = rsAvailabilities.getString("start");
						String end = rsAvailabilities.getString("end");
						String jsonStringInterval = " {'Interval': {'start':" +  start + "'end':" + end + "} }";
						Interval interval = gson.fromJson(jsonStringInterval, Interval.class);	
						availabilities.add(new Availability(userAva, interval));
					}
				}	
				createdEvents
				.add(new Event(eventId, eventName, duration, isRecurring, isPublic, url, creator, invitedEmails, joinedUsers, availabilityIntervals, availabilities));
			}
		}
		finally {
		}
		//Get invited events
		try {
			User user = this.getUser(userId);
			String email = user.getEmail();//Email used to identify events the user is invited to attend
			Gson gson = new Gson();
			statement = conn.createStatement();
			//join and choose those event whose email is this user's email address
			ResultSet rs1 = statement.executeQuery("SELECT * FROM invitedEmails INNER JOIN Events "
					+ "ON invitedEmails.eventID = Events.eventID WHERE invitedEmails.emails=" + email);
			while (rs1.next()) {
				int eventId = rs1.getInt("eventID");
				String eventName  = rs1.getString("name");
				//String creatorName  = rs.getString("creator");
				int userIdRs = rs1.getInt("userID");
				String url = rs1.getString("url");
				int duration = rs1.getInt("duration");
				boolean isRecurring = Boolean.parseBoolean(rs1.getString("isRecurring"));
				boolean isPublic = Boolean.parseBoolean(rs1.getString("isPublic"));
				User creator = this.getUser(userIdRs);
				ResultSet rsInvitedEmails = statement.executeQuery("SELECT emails FROM invitedEmails WHERE eventID='" + eventId + "';");
				List<String> invitedEmails = new ArrayList<>();
				while(rsInvitedEmails.next()) {
					invitedEmails.add(rsInvitedEmails.getString("emails"));
				}
				ResultSet rsJoinedUsers = statement.executeQuery("SELECT userID FROM Events WHERE eventID='" + eventId + "';");
				List<User> joinedUsers = new ArrayList<>();
				while (rsJoinedUsers.next()) {
					joinedUsers.add(this.getUser(rsJoinedUsers.getInt("userID")));
				}
				//Get availability intervals
				ResultSet rsAvailabilityIntervals = statement.executeQuery("SELECT * FROM availabilityIntervals a, Events e WHERE a.eventID = e.eventID AND eventID='" + eventId + "';");
				List<Interval> availabilityIntervals = new ArrayList<>();
				while (rsAvailabilityIntervals.next()) {
					int start = rsAvailabilityIntervals.getInt("start");
					int end = rsAvailabilityIntervals.getInt("end");
					String jsonStringInterval = " {'Interval': {'start':" +  start + "'end':" + end + "} }";
					Interval interval = gson.fromJson(jsonStringInterval, Interval.class);	
					availabilityIntervals.add(interval);
				}
				
				//Get availabilities of joined users
				List<Availability> availabilities = new ArrayList<>();
				for (User oneUser : joinedUsers) {
					int userIdAva = oneUser.getUserID();
					ResultSet rsAvailabilities = statement.executeQuery("SELECT * FROM availabilities WHERE " + "userID='" + userIdAva + "';");
					while (rsAvailabilities.next()) {
						User userAva = this.getUser(rsAvailabilities.getInt("userID"));
						String start = rsAvailabilities.getString("start");
						String end = rsAvailabilities.getString("end");
						String jsonStringInterval = " {'Interval': {'start':" +  start + "'end':" + end + "} }";
						Interval interval = gson.fromJson(jsonStringInterval, Interval.class);	
						availabilities.add(new Availability(userAva, interval));
					}
				}
				invitedEvents
				.add(new Event(eventId, eventName, duration, isRecurring, isPublic, url, creator, invitedEmails, joinedUsers, availabilityIntervals, availabilities));
			}
		}
		finally {
			
		}
		//Get joined events
		try {
			User user = this.getUser(userId);
			Gson gson = new Gson();
			statement = conn.createStatement();
			//join and choose those event whose email is this user's email address
			ResultSet rs2 = statement.executeQuery("SELECT * FROM joinedEvent "
					+ "WHERE userID=" + userId);
			while (rs2.next()) {
				int eventId = rs2.getInt("eventID");
				String eventName  = rs2.getString("name");
				//String creatorName  = rs.getString("creator");
				int userIdRs = rs2.getInt("userID");
				String url = rs2.getString("url");
				int duration = rs2.getInt("duration");
				boolean isRecurring = Boolean.parseBoolean(rs2.getString("isRecurring"));
				boolean isPublic = Boolean.parseBoolean(rs2.getString("isPublic"));
				User creator = this.getUser(userIdRs);
				ResultSet rsInvitedEmails = statement.executeQuery("SELECT emails FROM invitedEmails WHERE eventID='" + eventId + "';");
				List<String> invitedEmails = new ArrayList<>();
				while(rsInvitedEmails.next()) {
					invitedEmails.add(rsInvitedEmails.getString("emails"));
				}
				ResultSet rsJoinedUsers = statement.executeQuery("SELECT userID FROM Events WHERE eventID='" + eventId + "';");
				List<User> joinedUsers = new ArrayList<>();
				while (rsJoinedUsers.next()) {
					joinedUsers.add(this.getUser(rsJoinedUsers.getInt("userID")));
				}
				//Get availability intervals
				ResultSet rsAvailabilityIntervals = statement.executeQuery("SELECT * FROM availabilityIntervals a, Events e WHERE a.eventID = e.eventID AND eventID='" + eventId + "';");
				List<Interval> availabilityIntervals = new ArrayList<>();
				while (rsAvailabilityIntervals.next()) {
					int start = rsAvailabilityIntervals.getInt("start");
					int end = rsAvailabilityIntervals.getInt("end");
					String jsonStringInterval = " {'Interval': {'start':" +  start + "'end':" + end + "} }";
					Interval interval = gson.fromJson(jsonStringInterval, Interval.class);	
					availabilityIntervals.add(interval);
				}
				
				//Get availabilities of joined users
				List<Availability> availabilities = new ArrayList<>();
				for (User oneUser : joinedUsers) {
					int userIdAva = oneUser.getUserID();
					ResultSet rsAvailabilities = statement.executeQuery("SELECT * FROM availabilities WHERE " + "userID='" + userIdAva + "';");
					while (rsAvailabilities.next()) {
						User userAva = this.getUser(rsAvailabilities.getInt("userID"));
						String start = rsAvailabilities.getString("start");
						String end = rsAvailabilities.getString("end");
						String jsonStringInterval = " {'Interval': {'start':" +  start + "'end':" + end + "} }";
						Interval interval = gson.fromJson(jsonStringInterval, Interval.class);	
						availabilities.add(new Availability(userAva, interval));
					}
				}
				joinedEvents
				.add(new Event(eventId, eventName, duration, isRecurring, isPublic, url, creator, invitedEmails, joinedUsers, availabilityIntervals, availabilities));
			}
		}
		finally {
			
		}
		eventList = new EventList(createdEvents, invitedEvents, joinedEvents);
		return eventList;
	}
	public void broadcast(int uid, Event event)
	{
		for(ClientHandler ch : clientHandlers) 
		{
			System.out.println("CH id: " + ch.uid);
			if(ch.uid != uid)
			{
				ch.sendUpdatedEvent(event);
			}
			else
			{
				ch.sendUpdatedText("success");
			}
		}
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
	public int uid;
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
				

				
				
				Integer dataLength = in.readInt();
				System.out.println(dataLength);
				byte[] bytes = new byte[dataLength];
//			    BufferedReader br = new BufferedReader(new InputStreamReader(in));
//			    String str="";
//			    int length=0;
//			    while ((str = br.readLine()) != null) {
//			    		str+=str;
////			    		bytes[length] = str.getBytes();
////			    		length = length + str.getBytes().length;
//			    }
//			    System.out.println(str);
			    
			    
				System.out.println("after bytes");
				in.readFully(bytes);
				System.out.println("after read fully");
				String data = new String(bytes,"UTF-8");
				System.out.println(data);
				JSONObject obj = new JSONObject(data);
				
				
				System.out.println("THIS IS FROM THE CLIENT:");
				System.out.println(obj);
				
				// Parse JSON string to object
				//GsonBuilder gsonBuilder = new GsonBuilder();
//				gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer());
				//Gson gson = new GsonBuilder().setPrettyPrinting().create();
				//Gson gson = gsonBuilder.create();

				Gson gson = new Gson();
				String type = obj.getString("type");
				if(type.equals("login")) {
					System.out.println("IN THIS FUNCTION");
					User u = server.verifyUser(obj.getString("username"), obj.getString("password"));
					System.out.println("FINISHED FUNCTION");
//					System.out.println(u.getName()+ " " + u.getEmail());
//					
					// Set ClientHandler user id # 
					uid = u.getUserID();
					System.out.println("User id: " + uid);
					//WE NEED TO SEND USER U
					String response = gson.toJson(u);
					System.out.println("Response: " + response);
					byte [] b = response.getBytes("UTF8");
					int len = b.length;
					System.out.println("Len: " + len);
					out.writeInt(len);
					out.flush();
					out.write(b);
					out.flush();
				}
				if(type.equals("signup")) {
					User u =server.createUser(obj.getString("username"),obj.getString("password"),obj.getString("email"));
					//System.out.println(u.getName()+ " " + u.getEmail());
					
					// Set ClientHandler user id # 
					if(u == null)
					{
						out.write(null);
						out.flush();
					}
					else {
						uid = u.getUserID();
						//WE NEED TO SEND USER U
						String response = gson.toJson(u);
						System.out.println("Response: " + response);
						byte [] b = response.getBytes("UTF8");
						int len = b.length;
						System.out.println("Len: " + len);
						out.writeInt(len);
						out.flush();
						out.write(b);
						out.flush();
					}
					
					//WE NEED TO SEND USER U
//					String response = gson.toJson(u);
//					System.out.println("Response: " + response);
//					byte [] b = response.getBytes("UTF8");
//					int len = b.length;
//					System.out.println("Len: " + len);
//					out.writeInt(len);
//					out.flush();
//					out.write(b);
//					out.flush();
				}
				if(type.equals("getevents")) {
					//Event e = server.getEvents(obj.getInt("id"));
					
					int uid = obj.getInt("userID");
					//int uid = Integer.parseInt(temp);
					EventList el = null;
					try {
						el = server.getEventsList(uid);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
					
					}
					
					String response = gson.toJson(el);
					System.out.println("Response: " + response);
					byte [] b = response.getBytes("UTF8");
					int len = b.length;
					System.out.println("Len: " + len);
					out.writeInt(len);
					out.flush();
					out.write(b);
					out.flush();
				}
				if(type.equals("getuser")) {
					User u = server.getUser(obj.getInt("id"));
				}
				if(type.equals("createevent")) {
					System.out.println(obj.toString());
					Event e = gson.fromJson(obj.get("event").toString(), Event.class);
					//System.out.println(obj.toString());
					System.out.println(e.getId());
					System.out.println(e.getName());
					System.out.println(e.getUrl());
					
					String status = server.createNewEvent(e);
					//String status = "true";
					byte [] b = status.getBytes("UTF8");
					int len = b.length;
					System.out.println("Len: " + len);
					out.writeInt(len);
					out.flush();
					out.write(b);
					out.flush();
				}
				if(type.equals("updateevent")) {
					Event e = gson.fromJson(obj.get("event").toString(), Event.class);
					//Event result = server.updateEvent(e, this);
					server.broadcast(this.uid, e);
				}

			} 
			catch (IOException ioe) 
			{
				System.out.println("in clienthandler.run() " + ioe.getMessage());
				ioe.printStackTrace();
			}
		}	
	}	
	
	public void sendUpdatedEvent(Event event)
	{
		Gson gson = new Gson();
		String response = gson.toJson(event);
		System.out.println("Response: " + response);
		byte[] b;
		try {
			b = response.getBytes("UTF8");
			int len = b.length;
			System.out.println("Len: " + len);
			out.writeInt(len);
			out.flush();
			out.write(b);
			out.flush();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void sendUpdatedText(String text)
	{
//		Gson gson = new Gson();
//		String response = gson.toJson(event);
//		System.out.println("Response: " + response);
		byte[] b;
		try {
			b = text.getBytes("UTF8");
			int len = b.length;
			System.out.println("Len: " + len);
			out.writeInt(len);
			out.flush();
			out.write(b);
			out.flush();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
