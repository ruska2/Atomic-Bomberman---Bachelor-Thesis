import com.example.robo.atomicbomberman.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Robo on 15.05.2017.
 */
public class HitBonusChecker implements Runnable {
    @Override
    public void run() {
        while (true){

            Server.db.databaseReference.child(Constants.ACTIVE_USERS_TABLE).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue()!= null){

                            Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();

                            for (Object obj : objectMap.values()) {
                                if (obj instanceof Map) {
                                    Map<String, Object> values = (Map<String, Object>) obj;
                                    new GetAllBonuses(values.get(Constants.ACTIVE_USERS_TABLE_NICKNAME).toString(),(double) values.get(Constants.ACTIVE_BOMB_TABLE_LATI), (double) values.get(Constants.ACTIVE_BOMB_TABLE_LONGI)).getAll();
                                }
                            }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
