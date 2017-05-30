import com.example.robo.atomicbomberman.Constants;
import com.example.robo.atomicbomberman.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.*;

/**
 * Created by Robo on 08.05.2017.
 */
public class AddToTrackTree {
    User user;

    AddToTrackTree(User u ){
        user = u;
    }

    public void addToTrack(){
        Server.db.databaseReference.child(Constants.TRACK_TABLE).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    int alluserswithsamename = 0;
                    int usersolderthanoneminue = 0;
                    for (Object obj : objectMap.values()) {
                        if (obj instanceof Map) {
                            Map<String, Object> values = (Map<String, Object>) obj;

                            String name = values.get(Constants.ACTIVE_USERS_TABLE_NICKNAME).toString();
                            long time = (long) values.get(Constants.ACTIVE_USERS_TABLE_DATETIME);

                            if(name.equals(user.getName())){
                                alluserswithsamename++;
                                if(user.getDatetime() - time > Constants.ONE_MINUTE){
                                    usersolderthanoneminue++;
                                }
                            }


                        }
                    }

                    if(alluserswithsamename == usersolderthanoneminue){
                        Server.db.addTrack(user);
                    }
                }else{
                    Server.db.addTrack(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
