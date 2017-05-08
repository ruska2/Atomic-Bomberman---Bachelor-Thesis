import com.example.robo.atomicbomberman.Constants;
import com.example.robo.atomicbomberman.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

/**
 * Created by Robo on 07.05.2017.
 */
public class LogoutChecker {

    User user;

    LogoutChecker(User user){
        this.user = user;
    }

    public void checkMethod(){
        DatabaseReference childReference = Server.db.databaseReference.child(Constants.ACTIVE_BOMB_TABLE);

        childReference.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onCancelled(DatabaseError arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // TODO Auto-generated method stub
                if (dataSnapshot.getValue() != null) {

                    int counter = 0;
                    Map<String, Object> objectMap;
                    if(dataSnapshot.getValue() instanceof Map){
                        objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                        counter = 0;
                    }else{
                        ArrayList<Object> x = (ArrayList<Object>) dataSnapshot.getValue();
                        objectMap = new HashMap<String, Object>();
                        for (Object k : x){
                            if(k != null){
                                HashMap<String,Object> o = (HashMap<String, Object>) k;
                                objectMap.put(o.get(Constants.ACTIVE_BOMB_TABLE_ID).toString(),o);
                            }
                        }

                    }


                    for (Object obj : objectMap.values()) {
                        if (obj instanceof Map) {
                            Map<String, Object> values = (Map<String, Object>) obj;

                            double lat = (double) values.get(Constants.ACTIVE_BOMB_TABLE_LATI);
                            double longi = (double) values.get(Constants.ACTIVE_BOMB_TABLE_LONGI);

                            double distance = PointsDistance.distFrom(lat, longi , user.getLati(),user.getLongi());
                            System.out.println(distance);

                            if(distance < 80.01){
                            }else{
                                counter++;
                            }

                        }

                    }
                    if(counter == objectMap.size()){
                        Server.db.deleteUser(user);
                    }
                }else{
                    Server.db.deleteUser(user);
                }
            }
        });
    }
}
