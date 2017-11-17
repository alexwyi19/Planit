package java;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.objects.Event;
import java.objects.User;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.database.FirebaseDatabase;

public class Server 
{
	private static final String DATABASE_URL = "https://planit-ea426.firebaseio.com/";
	private static final String ACCOUNT_JSON = "planitaccount.json";
	private ServerSocket ss;
	private Vector<ClientHandler> clientHandlers;
	private FirebaseApp fbApp;
	private FirebaseAuth fbAuth;
	private FirebaseDatabase fbDatabase;
	
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
			InetAddress ip;
			  try {

				ip = InetAddress.getLocalHost();
				System.out.println("Current IP address : " + ip.getHostAddress());

			  } catch (UnknownHostException e) {

				e.printStackTrace();

			  }
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
	 *  Create a user in Firebase Auth 
	 */
	public void createUser(String email,String password, String username) 
	{
		System.out.println(email + " " + password + " " + username);
		CreateRequest request = new CreateRequest()
				.setEmail(email)
				.setPassword(password)
				.setDisplayName(username)
				.setDisabled(false);
		
		UserRecord userRecord = null;
		try {
			userRecord = fbAuth.createUserAsync(request).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		System.out.println("Successfully created new user: " + userRecord.getUid());
	}
	
	/*
	 *  Get user data from Firebase Auth
	 */
	public UserRecord getUser(String email) 
	{
		UserRecord userRecord = null;
		try {
			userRecord = fbAuth.getUserByEmailAsync(email).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		// See the UserRecord reference doc for the contents of userRecord.
		System.out.println("Successfully fetched user data: " + userRecord.getEmail());
		return userRecord;
	}
	
	/*
	 *  Update user data in Firebase Auth
	 */
	public void updateUser()
	{
		
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

//class ClientHandler extends Thread
//{
//	private Server server;
//	private Socket socket;
//	private DataOutputStream out;
//	private DataInputStream in;
//	
//	public ClientHandler(Socket socket, Server server) 
//	{
//		out = null;
//		in = null;
//		this.server = server;
//		this.socket = socket;
//		try
//		{
//			out = new DataOutputStream(socket.getOutputStream());
//			in = new DataInputStream(socket.getInputStream());
//			this.start();
//		}
//		catch (IOException ioe) 
//		{
//			System.out.println("in clienthandler constructor " + ioe.getMessage());
//		}
//			
//	}
//	
//	public void run() 
//	{
//		while (true) 
//		{
//			try 
//			{
//				// Get JSON string from client
//				String fromClient = in.readUTF();
//				// Parse JSON string to object
//				Gson gson = new GsonBuilder().setPrettyPrinting().create();
//				Event event = gson.fromJson(fromClient, Event.class);
//			} 
//			catch (IOException ioe) 
//			{
//				System.out.println("in clienthandler.run() " + ioe.getMessage());
//			}
//			// Json object
//			// jsondata = new JSONObject()
//			// somestring = jsondata.getString()
//			// somestring.equals("request_connect") 
//			// logic here
//		}
//		
//	}
//
//	/* 
//	 *  Send message to client function
//	 */
//	
//}


class ClientHandler extends Thread {

	//private PrintWriter pw;
	//private BufferedReader br;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private Server server;
	public ClientHandler(Socket s, Server server) {
		try {
			this.server = server;
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			this.start();
		} catch (IOException ioe) {
			System.out.println("ioe in ServerThread constructor: " + ioe.getMessage());
		}
	}

	//public void sendMessage(String message) {
//	public void sendMessage(ChatMessage cm) {
//		try {
//			oos.writeObject(cm);
//			oos.flush();
//		} catch (IOException ioe) {
//			System.out.println("ioe: " + ioe.getMessage());
//		}
//	}
	
	public void run() {
		try {
			while(true) {
				//String line = br.readLine();
				//cr.broadcast(line, this);
				//ChatMessage cm = (ChatMessage)ois.readObject();
				User user = (User)ois.readObject();
				System.out.println("inside clienthandler run() " + 
				user.getEmail() + " " + user.getPassword()
				+ " " + user.getName());
				server.createUser(user.getEmail(), user.getPassword(), user.getName());
			}
		} catch (IOException ioe) {
			System.out.println("ioe in ServerThread.run(): " + ioe.getMessage());
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe: " + cnfe.getMessage());
		}
	}
}