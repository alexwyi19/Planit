import java.io.FileInputStream;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Server {
//	private static final String DATABASE_URL = "https://firstfirebase-f9ec6.firebaseio.com/";
	private static final String DATABASE_URL = "https://planit-ea426.firebaseio.com/";
    private static DatabaseReference database;
    
	public static void main (String[] args) throws IOException {
		FileInputStream serviceAccount = new FileInputStream("planitaccount.json");

		FirebaseOptions options = new FirebaseOptions.Builder()
		  .setCredentials(GoogleCredentials.fromStream(serviceAccount))
		  .setDatabaseUrl(DATABASE_URL)
		  .build();

		FirebaseApp.initializeApp(options);
		
		// Shared Database reference
        database = FirebaseDatabase.getInstance().getReference();
		//System.out.println("hello");
		
		// Start listening to the Database
        startListeners();
        while (true) {
        	
        }
	}
	
	/**
     * Start global listener for all Posts.
     */
    public static void startListeners() {
        database.child("events").addChildEventListener(new ChildEventListener() {

            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildName) {
                final String postId = dataSnapshot.getKey();
                System.out.println(postId);
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildName) {}

            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildName) {}

            public void onCancelled(DatabaseError databaseError) {
                System.out.println("startListeners: unable to attach listener to posts");
                System.out.println("startListeners: " + databaseError.getMessage());
            }
        });
    }
}
