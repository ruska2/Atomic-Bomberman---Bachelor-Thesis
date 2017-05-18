import com.example.robo.atomicbomberman.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Robo on 15.05.2017.
 */
public class GetAllBonuses {

    String name;
    double lat;
    double lon;

    GetAllBonuses(String n, double la , double lo){
        name = n;
        lat = la;
        lon = lo;
    }

    public void getAll(){
        Server.db.databaseReference.child(Constants.BONUSES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();

                    for (String key : objectMap.keySet()) {
                            Map<String, Object> values = (Map<String, Object>) objectMap.get(key);
                            double meters = PointsDistance.distFrom(lat,lon,(double) values.get(Constants.ACTIVE_BOMB_TABLE_LATI),(double) values.get(Constants.ACTIVE_BOMB_TABLE_LONGI));
                            System.out.println(meters);
                            if(meters < 10.01){

                                Server.db.databaseReference.child(Constants.BONUSES).child(key).removeValue();
                                Server.db.databaseReference.child(Constants.REGISTRED_USERS_TABLE).child(name).child(Constants.REGISTRED_USERS_TABLE_BONUS).setValue(true);
                                try{
                                    new BonusCounter(name).make();
                                }catch (Exception e){}
                            }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
