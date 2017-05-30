import com.example.robo.atomicbomberman.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.*;

/**
 * Created by Robo on 14.05.2017.
 */
public class GetAllTracks {

    public void getAllTracks(){
        Server.db.databaseReference.child(Constants.TRACK_TABLE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
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

                    int M;

                    int size = objectMap.size();
                    if(size < 5000){
                        M = 5;
                    }else if(size < 10001){
                        M = 10;
                    }else{
                        M = 15;
                    }




                    RTree<Double> tree = new RTree<>(M,1,2);

                        for (Object obj : objectMap.values()) {
                            if (obj instanceof Map) {
                                Map<String, Object> values = (Map<String, Object>) obj;
                                double[] cords = new double[2];

                                cords[0] = (double) values.get(Constants.ACTIVE_BOMB_TABLE_LATI);
                                cords[1] = (double) values.get(Constants.ACTIVE_BOMB_TABLE_LONGI);

                                tree.insert(cords, 1.0);

                            }
                        }



                    tree.visualize();

                    for(List<Node> l : tree.getLists()){
                        new AddBonusToDatabase(PointsDistance.GetCentralGeoCoordinate(l)).check();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
