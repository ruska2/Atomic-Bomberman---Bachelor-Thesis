import com.example.robo.atomicbomberman.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.*;
import java.util.concurrent.CountDownLatch;


public class Database {
	
	public static FirebaseDatabase firebaseDatabase;
	public static DatabaseReference databaseReference;
	ArrayList<User> active_users;
	
	Database(){
		initFirebase();
		active_users = new ArrayList<User>();
		
		
	}
	
	private void initFirebase() {
        try {
            FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                    .setDatabaseUrl("https://atomicbomberman-8e29a.firebaseio.com")
                    .setServiceAccount(new FileInputStream(new File("src/AtomicBomberman-6ed4c7c8e433.json")))
                    .build();

            FirebaseApp.initializeApp(firebaseOptions);
            firebaseDatabase = FirebaseDatabase.getInstance();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        
        databaseReference = firebaseDatabase.getReference("/");
    }
	
	public void bombAddUpdate(Bomb b) {
        if (b != null) {

            DatabaseReference childReference = databaseReference.child(Constants.ACTIVE_BOMB_TABLE);

            final CountDownLatch countDownLatch = new CountDownLatch(1);

            childReference.child(b.getId()+"").setValue(b.toMap(), new DatabaseReference.CompletionListener() {

                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                    countDownLatch.countDown();
                }
            });
          
        }
    }

	
	public void userAddUpdate(final User user){
		DatabaseReference childReference = databaseReference.child(Constants.ACTIVE_USERS_TABLE);

			childReference.child(user.getName()).setValue(user.toMap(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                }
            });
			

	}

	
	public void deleteUser(User user) {
        databaseReference.child(Constants.ACTIVE_USERS_TABLE).child(user.getName()).removeValue();

    }

    public void deleteUser(String user) {
        databaseReference.child(Constants.ACTIVE_USERS_TABLE).child(user).removeValue();

    }

    public void deleteBomb(Bomb bomb) {
        databaseReference.child(Constants.ACTIVE_BOMB_TABLE).child(bomb.getId()+"").removeValue();

    }

    public void deleteBomb(String bomb) {
        databaseReference.child(Constants.ACTIVE_BOMB_TABLE).child(bomb).removeValue();

    }

    public void updateActualTime(){
	    databaseReference.child(Constants.TIME).setValue(ServerValue.TIMESTAMP);
    }


    public void insertLoggedUser(LoginUser user){
        databaseReference.child(Constants.LOGGED_USERS).child(user.getName()).setValue(true);
    }

    public void insertLoggedUser(String user){
        databaseReference.child(Constants.LOGGED_USERS).child(user).setValue(true);
    }

    public void deleteLoggedUser(LoginUser user){
        databaseReference.child(Constants.LOGGED_USERS).child(user.getName()).removeValue();
    }

    public void deleteLoggedUser(String user){
        databaseReference.child(Constants.LOGGED_USERS).child(user).removeValue();
    }

    public void addLoggedUserImei(String name,String mac){
        databaseReference.child(Constants.LOGGED_USERS_IMEI).child(name).setValue(mac);
    }
    public void deleteLoggedUserImei(String name){
        databaseReference.child(Constants.LOGGED_USERS_IMEI).child(name).removeValue();
    }

    public void addDeleteMessage(final String name,  final String message){

        databaseReference.child(Constants.MESSAGES).child(name).setValue(message);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // Your database code here
                databaseReference.child(Constants.MESSAGES).child(name).removeValue();
            }
        }, 200);
    }

    public void addRegistredUser(RegistredUser ru){
        Map<String,Object> x = ru.toMap();
        x.remove(Constants.REGISTRED_USER_TABLE_PASSWORD);
        databaseReference.child(Constants.REGISTRED_USERS_TABLE).child(ru.getName()).setValue(x);
    }

    public void addRegistredUser(String name, String pw){
        databaseReference.child(Constants.REGISTRED_USERS_TABLE_PASSWORD).child(name).setValue(pw);
    }

    public void addTrack(User user){
        databaseReference.child(Constants.TRACK_TABLE).push().setValue(user.toMap());
    }

}
