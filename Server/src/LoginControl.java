import com.example.robo.atomicbomberman.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Robo on 05.05.2017.
 */
public class LoginControl {
    String password;
    String name;
    String imei;

    LoginControl(String name, String pw, String imei){
        this.name = name;
        password = pw;
        this.imei = imei;
    }

    public void checkCorrectUser() {
        final DatabaseReference dbref = Server.db.databaseReference;

        dbref.child(Constants.REGISTRED_USERS_TABLE_PASSWORD).child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    String  pw = dataSnapshot.getValue().toString();
                    if(pw.equals(password)){
                        // add to logged users
                       new LoginImeiControl(name,imei).checkAddMethod();
                    }else {
                        Server.db.addDeleteMessage(name,Constants.WRONG_PASSWORD);
                    }
                }else{
                    Server.db.addDeleteMessage(name,Constants.USER_NOT_REGISTRED);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void deleteUser(){

        final DatabaseReference dbref = Server.db.databaseReference;
        dbref.child(Constants.LOGGED_USERS_IMEI).child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    String imeifromdb = dataSnapshot.getValue().toString();
                    if(imei.equals(imeifromdb)){
                        Server.db.deleteLoggedUser(name);
                        Server.db.deleteLoggedUserImei(name);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Server.db.deleteLoggedUser(name);

    }

}
