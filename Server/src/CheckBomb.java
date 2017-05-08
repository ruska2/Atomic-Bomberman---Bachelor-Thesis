import com.example.robo.atomicbomberman.Bomb;
import com.example.robo.atomicbomberman.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.*;

/**
 * Created by Robo on 06.05.2017.
 */
public class CheckBomb {

    Bomb b;

    CheckBomb(Bomb b){
        this.b = b;
    }

    public void checkCorrectness(){

        Server.db.databaseReference.child(Constants.ACTIVE_BOMB_TABLE).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    Map<String, Object> objectMap;
                    if (dataSnapshot.getValue() instanceof Map) {
                        objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    } else {
                        ArrayList<Object> x = (ArrayList<Object>) dataSnapshot.getValue();
                        objectMap = new HashMap<String, Object>();
                        for (Object k : x) {
                            if (k != null) {
                                HashMap<String, Object> o = (HashMap<String, Object>) k;
                                objectMap.put(o.get(Constants.ACTIVE_BOMB_TABLE_ID).toString(), o);
                            }
                        }

                    }


                    for (Object obj : objectMap.values()) {
                        if (obj instanceof Map) {
                            Map<String, Object> values = (Map<String, Object>) obj;

                            String who = values.get(Constants.ACTIVE_BOMB_TABLE_WHO).toString();
                            long time = (long) values.get(Constants.ACTIVE_BOMB_TABLE_DATETIME);

                            boolean check = true;
                            if (who.equals(b.getWho())) {
                                System.out.print(b.getDatetime()-time);
                                if (b.getDatetime() - time < 30000) {
                                    check = false;
                                }
                            }

                            if (check) {
                                Thread t = new Thread(new BombTicker(b));
                                t.start();
                            }

                        }
                    }
                }
                else {
                    Thread t = new Thread(new BombTicker(b));
                    t.start();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
