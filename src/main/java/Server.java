

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import objects.Event;
import objects.User;

public class Server 
{
	private static final String DATABASE_URL = "https://planit-ea426.firebaseio.com/";
	private static final String ACCOUNT_JSON = "planitaccount.json";
	private ServerSocket ss;
	private Vector<ClientHandler> clientHandlers;
	private FirebaseApp fbApp;
	//private FirebaseAuth fbAuth;
	private FirebaseDatabase fbDatabase;
	private DatabaseReference emailsRef;
	private DatabaseReference usersRef;
	private DataSnapshot emails;
	private DataSnapshot users;
	
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
			// fbAuth = FirebaseAuth.getInstance(fbApp);
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
	}
	
	/*
	 *  Create a user function. Sends to Realtime Database
	 */
	public void createUser(User user) 
	{
		String email = encodeUserEmail(user.getEmail());
		String username = user.getUserName();
		System.out.println("Creating: " + email + " " + username);
		
		if (!emailExists(email) && !userExists(username))
		{
			emailsRef.child(email).setValueAsync(email);
			usersRef.child(username).setValueAsync(user);
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
	static String encodeUserEmail(String userEmail) {
	    return userEmail.replace(".", ",");
	}
	/*
	 *  Decode the ',' from email string
	 */
	static String decodeUserEmail(String userEmail) {
	    return userEmail.replace(",", ".");
	}
	
	/*
	 *  Get user data from Firebase Database
	 */
	public User getUser(User user)
	{
		return null;

	}
		
	/*
	 *  Create a new event in Firebase Database
	 */
	public void createNewEvent(Event event)
	{
		
	}
	
	public static void main(String [] args) 
	{
		Server server = new Server();
	}
}

class ClientHandler extends Thread
{
	private Server server;
	private Socket socket;
	private DataOutputStream out;
	private DataInputStream in;
	
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
				// Parse JSON string to object
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				User user = gson.fromJson(fromClient, User.class);
				// test message
				System.out.println("inside clienthandler run() " + 
						user.getEmail() + " " + user.getPassword()
						+ " " + user.getUserName());
				
				// Create user object and send to server. Server will do the
				// logic to create a user
				server.createUser(user);
				//server.createUser(user.getEmail(), user.getPassword(), user.getName());
				
				// TODO : Need a way to differentiate between when client
				// sends create user info
				if (fromClient.contains("username"))
				{
					
				}
				
				// TODO : Need a way to differentiate between when client
				// sends create event info
				
				
				// TODO : Need a way to differentiate between when client
				// sends update event info
				
				
				// TODO : Need a way to differentiate between when client
				// sends any other kind of json info
				
				
				//Event event = gson.fromJson(fromClient, Event.class);
			} 
			catch (IOException ioe) 
			{
				System.out.println("in clienthandler.run() " + ioe.getMessage());
			}
			// Json object
			// jsondata = new JSONObject()
			// somestring = jsondata.getString()
			// somestring.equals("request_connect") 
			// logic here
		}
		
	}

	/* 
	 *  Send message to client function
	 */
	
}
