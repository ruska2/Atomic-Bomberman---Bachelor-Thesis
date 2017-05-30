import com.example.robo.atomicbomberman.*;
import com.google.firebase.*;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.FirebaseOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.*;



public class Database {
	public static FirebaseDatabase firebaseDatabase;
	public static DatabaseReference databaseReference;
	ArrayList<User> active_users;
	
	Database(){
        try {
            initFirebase();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
	
	private void initFirebase() throws IOException {
        try {

            File file = new File("src/atomicbomberman-8e29a-firebase-adminsdk-3ud96-8ef0e44a62.json");
            FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                    .setCredential(FirebaseCredentials.fromCertificate(new FileInputStream(file)))
                    .setDatabaseUrl("https://atomicbomberman-8e29a.firebaseio.com")
                    .build();

            FirebaseApp defaultApp = FirebaseApp.initializeApp(firebaseOptions);

            firebaseDatabase = FirebaseDatabase.getInstance(defaultApp);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        databaseReference = firebaseDatabase.getReference();
    }

    public static DatabaseReference getDatabaseReference(){
	    return databaseReference;
    }
	
	public void bombAddUpdate(Bomb b) {
        if (b != null) {

            DatabaseReference childReference = databaseReference.child(Constants.ACTIVE_BOMB_TABLE);

            childReference.child(b.getId()+"").setValue(b.toMap());
          
        }
    }

	
	public void userAddUpdate(final User user){
		DatabaseReference childReference = databaseReference.child(Constants.ACTIVE_USERS_TABLE);

			childReference.child(user.getName()).setValue(user.toMap());

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

    public void addBonus(Map m){
        databaseReference.child(Constants.BONUSES).push().setValue(m);
    }

    public void updateScore(String nick,int score,int change){
        databaseReference.child(Constants.REGISTRED_USERS_TABLE).child(nick).child(Constants.REGISTRED_USERS_TABLE_SCORE).setValue(score+change);
    }

    public void removeBonus(String name){
        databaseReference.child(Constants.REGISTRED_USERS_TABLE).child(name).child(Constants.REGISTRED_USERS_TABLE_BONUS).setValue(false);
    }

    public void removeBonuses(){
        databaseReference.child(Constants.BONUSES).removeValue();
    }

    public void removeTrack(String key){
        databaseReference.child(Constants.TRACK_TABLE).child(key).removeValue();
    }

    public void removeSingleBonus(String key){
        databaseReference.child(Constants.BONUSES).child(key).removeValue();
    }

    public void addBonus(String name){
        databaseReference.child(Constants.REGISTRED_USERS_TABLE).child(name).child(Constants.REGISTRED_USERS_TABLE_BONUS).setValue(true);
    }
    public void addQuest(String name, String target){
        databaseReference.child(Constants.QUEST_TABLE).child(name).setValue(target);
    }

    public void deleteQuests(){
        databaseReference.child(Constants.QUEST_TABLE).removeValue();
    }
}
