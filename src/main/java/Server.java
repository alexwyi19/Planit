

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
	private FirebaseApp fbApp;
	private FirebaseAuth fbAuth;
	private FirebaseDatabase fbDatabase;
	private DatabaseReference emailsRef;
	private DatabaseReference usersRef;
	private DatabaseReference eventsRef;
	private DataSnapshot emails;
	private DataSnapshot users;
	private DataSnapshot events;
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
	
			FirebaseOptions options = new FirebaseOptions.Builder()
			  .setCredentials(GoogleCredentials.fromStream(serviceAccount))
			  .setDatabaseUrl(DATABASE_URL)
			  .build();
	
			fbApp = FirebaseApp.initializeApp(options);
			// Get access to Firebase Auth for users
			fbAuth = FirebaseAuth.getInstance(fbApp);
			// Get access to Firebase Realtime Database
			fbDatabase = FirebaseDatabase.getInstance(fbApp);

			initializeListeners();
			
			while (true) 
			{
				Socket s = ss.accept();
				System.out.println("new client connected");
				ClientHandler ch = new ClientHandler(s, this);
				clientHandlers.add(ch);
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
	
	/*
	 *  Initializes listeners for data trees to check for updates in real time
	 */
	private void initializeListeners() 
	{
		emailsRef = fbDatabase.getReference("emails");
		emailsRef.addValueEventListener(new ValueEventListener() {
		    @Override
		    public void onDataChange(DataSnapshot dataSnapshot) {
		        emails = dataSnapshot;
		    }

		    @Override
		    public void onCancelled(DatabaseError databaseError) {
		        System.out.println("The read failed: " + databaseError.getCode());
		    }
		});
		
		usersRef = fbDatabase.getReference("users");
		usersRef.addValueEventListener(new ValueEventListener() {
		    @Override
		    public void onDataChange(DataSnapshot dataSnapshot) {
		        users = dataSnapshot;
		    }

		    @Override
		    public void onCancelled(DatabaseError databaseError) {
		        System.out.println("The read failed: " + databaseError.getCode());
		    }
		});
		
		eventsRef = fbDatabase.getReference("events");
		eventsRef.addValueEventListener(new ValueEventListener() {
		    @Override
		    public void onDataChange(DataSnapshot dataSnapshot) {
		        events = dataSnapshot;
		    }

		    @Override
		    public void onCancelled(DatabaseError databaseError) {
		        System.out.println("The read failed: " + databaseError.getCode());
		    }
		});
	}
	
	/*
	 *  Verify user login credentials
	 */
	public void verifyUser(User user)
	{
		String username = user.getName();
		String password = user.getPassword();
		System.out.println("verifying user: " + username + " password: " + password);
		if (userExists(username))
		{
			User u = getUser(user);
			if (u.getPassword().equals(password))
			{
				System.out.println("Verifying: " + username + " w/ given password: " + password);
				System.out.println("GOOD LOGIN INFO");
			}
			else if (!(u.getPassword().equals(password)))
			{
				System.out.println("BAD LOGIN INFO");
			}
		}
		else if (!userExists(username))
		{
			System.out.println("USER DOESNT EXIST");
		}
	}
	
	/*
	 *  Create a user function. Sends to Realtime Database
	 */
	public void createUser(User user) 
	{
		String email = encodeUserEmail(user.getEmail());
		String username = user.getName();
		System.out.println("Creating: " + email + " " + username);
		
		if (!emailExists(email) && !userExists(username))
		{
			emailsRef.child(email).setValueAsync(email);
			usersRef.child(username).setValueAsync(user);
			createUserFbAuth(user);
		}
		else 
		{
			System.out.println("EMAIL OR USERNAME EXISTS");
		}
		
		// Test values for data snapshots
//		Iterable<DataSnapshot> parent = users.getChildren();
//		for (DataSnapshot child : parent)
//		{
//			System.out.println("IM IN HERE" + child.getKey());
//			User u = child.getValue(User.class);
//			System.out.println(u.getPassword());
//		}		
	}
	
	/*
	 *  Check if user name already exists
	 */
	public Boolean userExists(String username)
	{
		return users.hasChild(username);
	}
	
	/*
	 *  Check if email already exists
	 */
	public Boolean emailExists(String email)
	{
		return emails.hasChild(email);
	}
	
	/*
	 *  Firebase doesn't allow adding '.' in keys so encode the '.' 
	 */
	static String encodeUserEmail(String email) {
	    return email.replace(".", ",");
	}
	/*
	 *  Decode the ',' from email string
	 */
	static String decodeUserEmail(String email) {
	    return email.replace(",", ".");
	}
	
	/*
	 *  Get user data from Firebase Database
	 */
	public User getUser(User user)
	{
		String username = user.getName();
		User u = null;
		if (userExists(username))
		{
			u = users.child(username).getValue(User.class);
		}
		return u;
	}
	/*
	 * Create user in FB AUTH database, TESTING THIS
	 */
	public void createUserFbAuth(User user)
	{
		String email = user.getEmail();
		String password = user.getPassword();
		String username = user.getName();
		System.out.println("Creating user in FBAUTH: " + 
				email + " w/ password: " + password);
		CreateRequest request = new CreateRequest()
				.setEmail(email)
				.setPassword(password)
				.setDisplayName(username)
				.setDisabled(false);
		
		UserRecord userRecord = null;
		try {
			userRecord = fbAuth.createUserAsync(request).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		System.out.println("Successfully created new user in FBAUTH: "
				+ userRecord.getUid());
	}
	
	/*
	 *  Create a new event in Firebase Database
	 */
	public void createNewEvent(Event event)
	{
		Map<String, Event> e = new HashMap<>();
		e.put(encodeUserEmail(event.getCreator()), event);
		eventsRef.push().setValueAsync(e);
		//eventsRef.child(event.getCreator()).setValueAsync(e);
		
		// Test values for data snapshots
		Iterable<DataSnapshot> parent = events.getChildren();
		for (DataSnapshot mid : parent)
		{
			Iterable<DataSnapshot> temp = ((DataSnapshot) mid).getChildren();
			for(DataSnapshot child: temp) {
				System.out.println("IM IN CREATENEWEVENT" + child.getKey());
				Event ev = child.getValue(Event.class);
				//SimpleInterval<Date> si = ev.getEventInterval();
				System.out.println(ev.getCreator());
				System.out.println(ev.getName());
				System.out.println(ev.getType());
				List<String> inv = ev.getInvitedEmails();
				System.out.println("RIGHT BEFORE THE ERROR");
				System.out.println(inv.size());
				for (String s: inv)
				{
					System.out.println(s);
				}
				System.out.println("AFTER ERROR");
				List<Date> dur = ev.getDuration();
				for (Date d : dur)
				{
					System.out.println(d.toString());
				}
				//System.out.println(si.toString());
			}
		}		
	}
	
	//will change the boolean from false to true if someone joins
	public boolean joinEvent(Event event,String name) {
		Iterable<DataSnapshot> parent = events.getChildren();
		for (DataSnapshot mid : parent)
		{
			Iterable<DataSnapshot> temp = ((DataSnapshot) mid).getChildren();
			for(DataSnapshot child: temp) {
				Event ev = child.getValue(Event.class);
				if(event == ev) {
					Map<String,Boolean> joined = ev.getJoinedEvent();
					if(joined.get(name.split("@")[0])!=null){
						joined.put(name.split("@")[0], true);
						return true;
					}
				}
			}
		}		
		return false;
	}
	
	//given an event name it will return the event
	public Event findEvent(String name) {
		Event e=null;
		Iterable<DataSnapshot> parent = events.getChildren();
		for (DataSnapshot mid : parent)
		{
			Iterable<DataSnapshot> temp = ((DataSnapshot) mid).getChildren();
			for(DataSnapshot child: temp) {
				Event ev = child.getValue(Event.class);
				if(name.equals(ev.getName())) {
					e=ev;
				}
			}
		}
		return e;
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
				// Right now, this just gets a JSON string
				String fromClient = in.readUTF();
//				String fromClient = in.readLine();
//				if (fromClient != null) {
//					System.out.println("FromClient: " + fromClient);
//				}
//				
				
				System.out.println("THIS IS FROM THE CLIENT:");
				System.out.println(fromClient);
				// Parse JSON string to object
				GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer());
				//Gson gson = new GsonBuilder().setPrettyPrinting().create();
				Gson gson = gsonBuilder.create();
				if (fromClient.contains("password"))
				{
					User user = gson.fromJson(fromClient, User.class);
					// Test message
					System.out.println("inside clienthandler run() " + 
							user.getEmail() + " " + user.getPassword()
							+ " " + user.getName());
					
					// Check if login credentials are right.
					server.verifyUser(user);
					// Create user object and send to server. Server will do the
					// logic to create a user
					server.createUser(user);	
				}
				
				if (fromClient.contains("creator"))
				{
					// Test message
					System.out.println("inside clienthandler run()" +
							fromClient);
					Event event = gson.fromJson(fromClient, Event.class);
					//event.setEventInterval();
					server.createNewEvent(event);
				}
			} 
			catch (IOException ioe) 
			{
				System.out.println("in clienthandler.run() " + ioe.getMessage());
				ioe.printStackTrace();
			}
		}	
	}	
}
