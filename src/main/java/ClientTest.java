

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ClientTest extends Thread {

	//private ObjectInputStream ois;
	//private ObjectOutputStream oos;
	private DataOutputStream out;
	private DataInputStream in;
	
	public ClientTest(String hostname, int port) {
		try {
			Socket s = new Socket(hostname, port);
			//ois = new ObjectInputStream(s.getInputStream());
			//oos = new ObjectOutputStream(s.getOutputStream());
			out = new DataOutputStream(s.getOutputStream());
			in = new DataInputStream(s.getInputStream());
			this.start();
			
			Scanner scan = new Scanner(System.in);
			while(true) {
				/*
				 * Test user json
				 */
//				String userJsonString = userJSON(scan).toString();
//				out.writeUTF(userJsonString);
//				System.out.println("Created user object and sent");
				
				/*
				 * Test event json
				 */
				String eventJsonString = eventJSON(scan).toString();
				out.writeUTF(eventJsonString);
				System.out.println(eventJsonString);
//	
//				out.writeUTF("eventName");
			}
			
		} catch (IOException ioe) {
			System.out.println("ioe in ChatClient constructor: " + ioe.getMessage());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void run() {
//		try {
//			while(true) {
//				ChatMessage cm = (ChatMessage)ois.readObject();
//				System.out.println(cm.getUsername() + ": " + cm.getMessage());
//			}
//		} catch (IOException ioe) {
//			System.out.println("ioe in ChatClient.run(): " + ioe.getMessage());
//		} catch (ClassNotFoundException cnfe) {
//			System.out.println("cnfe: " + cnfe.getMessage());
//		}
	}
	public static void main(String [] args) {
		ClientTest cc = new ClientTest("localhost", 6789);
	}
	
	
	public JSONObject userJSON(Scanner scan)
	{
		System.out.println("email");
		String email = scan.nextLine();
		System.out.println("password");
		String password = scan.nextLine();
		System.out.println("username");
		String username = scan.nextLine();				
		/*
		 *  Test sending JSON object to server to create new user
		 */	
		JSONObject jo = new JSONObject();
		jo.put("email", email);
		jo.put("password", password);
		jo.put("name", username);
		
		return jo;
	}
	
	public JSONObject eventJSON(Scanner scan) throws ParseException
	{
		System.out.println("creator email");
		String email = scan.nextLine();
		System.out.println("event name");
		String name = scan.nextLine();
		System.out.println("type: pub/private");
		String type = scan.nextLine();
		
		List<String> invEmails = new ArrayList();
		Map<String,Boolean> joinedEvent = new HashMap<String,Boolean>();
		System.out.println("invite email1:");
		String invitedEmail1 = scan.nextLine();
		invEmails.add(invitedEmail1);
		joinedEvent.put(invitedEmail1.split("@")[0],false);
//		joinedEvent.put(invitedEmail1,false);
		System.out.println("invite email2:");
		String invitedEmail2 = scan.nextLine();
		invEmails.add(invitedEmail2);
		joinedEvent.put(invitedEmail2.split("@")[0], false);
//		joinedEvent.put(invitedEmail2, false);
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		String date1 = "11/20/2017 08:00";
		String date2 = "11/27/2017 15:00";
		Date d1 = sdf.parse(date1);
		Date d2 = sdf.parse(date2);

		JSONObject object = new JSONObject();
		object.put("creator", email);
		object.put("name", name);
		object.put("type", type);
		object.put("invitedEmails", invEmails);
//		List<String> joined = new ArrayList();
//		joined.add(null);
//		object.put("joinedEvent",joined);
		object.put("joinedEvent", joinedEvent);
		
		List<Date> dates = new ArrayList<>();
		dates.add(d1);
		dates.add(d2);
		object.put("duration", dates);

		return object;
	}
}