import com.example.robo.atomicbomberman.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.*;

/**
 * Created by Robo on 09.05.2017.
 */
public class QuestGenerator implements Runnable {

    Random rnd = new Random();

    @Override
    public void run() {
        while(true){
            generateQuests();

            try {
                Thread.sleep(70000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Server.db.deleteQuests();

            try {
                Thread.sleep(70000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void generateQuests(){
        //check

        Server.db.databaseReference.child(Constants.ACTIVE_USERS_TABLE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {


                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();

                    for (Object obj : objectMap.values()) {
                        if (obj instanceof Map) {
                            Map<String, Object> values = (Map<String, Object>) obj;
                            String name = values.get(Constants.ACTIVE_USERS_TABLE_NICKNAME).toString();

                            ArrayList<String> names = new ArrayList<>();
                            for (Object obj2 : objectMap.values()) {
                                if (obj2 instanceof Map) {
                                    Map<String, Object> values2 = (Map<String, Object>) obj2;

                                    String name2 = values2.get(Constants.ACTIVE_USERS_TABLE_NICKNAME).toString();

                                    if(!name.equals(name2)){
                                        double lat1 = (double) values.get(Constants.ACTIVE_BOMB_TABLE_LATI);
                                        double lat2 = (double) values2.get(Constants.ACTIVE_BOMB_TABLE_LATI);

                                        double long1 = (double) values.get(Constants.ACTIVE_BOMB_TABLE_LONGI);
                                        double long2 = (double) values2.get(Constants.ACTIVE_BOMB_TABLE_LONGI);

                                        double distance = PointsDistance.distFrom(lat1,long1,lat2,long2);

                                        if(distance > 50 && distance < 200){
                                            names.add(name2);
                                        }
                                    }
                                }
                            }

                            if(names.size() > 0){
                                int num = rnd.nextInt(names.size());
                                Server.db.addQuest(name,names.get(num));
                            }
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
