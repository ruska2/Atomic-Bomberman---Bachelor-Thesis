/**
 * Created by Robo on 04.05.2017.
 */

import com.example.robo.atomicbomberman.Bomb;
import com.example.robo.atomicbomberman.Constants;
import com.example.robo.atomicbomberman.User;
import com.google.firebase.database.*;
import com.google.firebase.tasks.Task;

import javax.management.Query;
import javax.tools.JavaFileManager;
import javax.xml.stream.Location;
import java.util.*;

public class HitChecker implements Runnable {

    Bomb bomb;
    boolean wait;

    HitChecker(Bomb b){
        bomb = b;
    }

    @Override
    public void run() {
        final double bomblat = bomb.getLatitude();
        final double bomblong = bomb.getLongitude();


        DatabaseReference childReference = Server.db.databaseReference.child(Constants.ACTIVE_USERS_TABLE);

        childReference.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onCancelled(DatabaseError arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // TODO Auto-generated method stub
                if(dataSnapshot.getValue() != null) {
                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();

                    for (Object obj : objectMap.values()) {
                        if (obj instanceof Map) {

                            Map<String, Object> values = (Map<String, Object>) obj;

                            String nickname = values.get(Constants.ACTIVE_USERS_TABLE_NICKNAME).toString();
                            double lat = (double) values.get(Constants.ACTIVE_USERS_TABLE_LATI);
                            double longi = (double) values.get(Constants.ACTIVE_USERS_TABLE_LONGI);


                                double distance = PointsDistance.distFrom(lat, longi, bomblat, bomblong);
                                System.out.println(distance);

                                if(distance < 80.01){

                                    if(!bomb.getWho().equals(nickname)){
                                        updateUserScore(nickname,- (80 - (int)-distance));
                                        updateUserScore(bomb.getWho(),80-((int)distance));
                                    }else{
                                        updateUserScore(nickname,- (80 - (int)distance));

                                    }
                                }

                            }

                        }
                    }
                }
        });

    }


    public void updateUserScore(final String nick , final int change){


        DatabaseReference childReference = Server.db.databaseReference.child(Constants.REGISTRED_USERS_TABLE).child(nick);
        childReference.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // TODO Auto-generated method stub
                if(dataSnapshot.getValue() != null) {
                    Map<String, Object> values = (HashMap<String, Object>) dataSnapshot.getValue();

                            int score = (int)(long) values.get(Constants.REGISTRED_USERS_TABLE_SCORE);

                            Task<Void> childReference = Server.db.databaseReference.child(Constants.REGISTRED_USERS_TABLE).child(nick).child(Constants.REGISTRED_USERS_TABLE_SCORE).setValue(score+change);


                        }

            }
        });
    }

}
