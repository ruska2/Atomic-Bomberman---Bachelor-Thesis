import com.example.robo.atomicbomberman.Constants;
import com.example.robo.atomicbomberman.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import org.omg.PortableServer.SERVANT_RETENTION_POLICY_ID;


/**
 * Created by Robo on 05.05.2017.
 */
public class LoginImeiControl {

    String name;
    String imei;

    LoginImeiControl(String name, String imei){
        this.name = name;
        this.imei = imei;
    }

    public void checkAddMethod() {
        Server.db.databaseReference.child(Constants.LOGGED_USERS_IMEI).child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Server.db.addDeleteMessage(name,Constants.SERVER_MESSAGE_USED);
                }else{
                    // user not logged nothing
                    Server.db.insertLoggedUser(name);
                    Server.db.addLoggedUserImei(name,imei);
                    Server.db.addDeleteMessage(name,Constants.LOGIN_SUCCESFULL);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}