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
public class CheckCorrectLatLong extends Thread{
    User user;
    public boolean check = false;
    CheckCorrectLatLong(User user){
        this.user = user;
    }

    public void checkMethod() {
        Server.db.databaseReference.child(Constants.ACTIVE_USERS_TABLE).child(user.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {

                    Map<String, Object> values = (HashMap<String, Object>) dataSnapshot.getValue();

                    double lat = (double) values.get(Constants.ACTIVE_USERS_TABLE_LATI);
                    double longi = (double) values.get(Constants.ACTIVE_USERS_TABLE_LONGI);

                    //CONTROL DISTANCE WHILE CHANGE
                    if (Math.abs(user.getLati() - lat) < 0.1 && Math.abs(user.getLongi() - longi) < 0.1) {
                        Server.db.userAddUpdate(user);
                    } else {
                        System.out.println(Constants.SERVER_MESSAGE_JUMP );
                    }
                }else{
                    new CheckUserLogin(user).checkAddMethod();
                }

                check = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    @Override
    public void run(){
        checkMethod();
    }
}
