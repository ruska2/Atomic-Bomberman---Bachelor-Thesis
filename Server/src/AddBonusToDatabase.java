import com.example.robo.atomicbomberman.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Robo on 27.05.2017.
 */
public class AddBonusToDatabase {

    double[] cor;
    public AddBonusToDatabase(double[] p){
        cor = p;
    }

    public void check(){
        Server.db.databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    int con = 0;
                    for (Object obj : objectMap.values()) {
                        if (obj instanceof Map) {

                            Map<String, Object> values = (Map<String, Object>) obj;

                            double lat = (double) values.get(Constants.ACTIVE_USERS_TABLE_LATI);
                            double longi = (double) values.get(Constants.ACTIVE_USERS_TABLE_LONGI);


                            double distance = PointsDistance.distFrom(lat, longi, cor[0], cor[1]);
                            System.out.println(distance);
                            if(distance < 251){
                                con++;
                            }

                        }

                    }
                    if(con > 0){
                        Map<String,Double> m = new HashMap<>();
                        m.put(Constants.ACTIVE_BOMB_TABLE_LATI,cor[0]);
                        m.put(Constants.ACTIVE_BOMB_TABLE_LONGI,cor[1]);
                        Server.db.addBonus(m);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
