import com.example.robo.atomicbomberman.Constants;
import com.example.robo.atomicbomberman.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Robo on 05.05.2017.
 */
public class CheckUserLogin {
    User user;

    CheckUserLogin(User user){
        this.user = user;
    }

    public void checkAddMethod() {
        Server.db.databaseReference.child(Constants.LOGGED_USERS).child(user.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Server.db.userAddUpdate(user);
                }else{

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
}
