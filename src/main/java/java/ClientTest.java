import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.objects.User;
import java.util.Scanner;

public class ClientTest extends Thread {

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	public ClientTest(String hostname, int port) {
		try {
			Socket s = new Socket(hostname, port);
			ois = new ObjectInputStream(s.getInputStream());
			oos = new ObjectOutputStream(s.getOutputStream());
			this.start();
			Scanner scan = new Scanner(System.in);
			while(true) {
				System.out.println("email");
				String email = scan.nextLine();
				System.out.println("password");
				String password = scan.nextLine();
				System.out.println("username");
				String username = scan.nextLine();
				User user = new User(email, password, username);
			
				oos.writeObject(user);
				oos.flush();
				System.out.println("Created user object and sent");
			}
			
		} catch (IOException ioe) {
			System.out.println("ioe in ChatClient constructor: " + ioe.getMessage());
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
}