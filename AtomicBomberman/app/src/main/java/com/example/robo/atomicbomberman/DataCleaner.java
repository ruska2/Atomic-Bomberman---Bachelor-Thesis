package com.example.robo.atomicbomberman;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Robo on 23.04.2017.
 */

public class DataCleaner extends Thread {

    static long currentTime = 0;

    @Override
    public void start() {
        // do something in the actual (old) thread
        super.start();

    }

    @Override
    public void run() {
        // do something in a new thread if 'called' by super.start()
        new RetrieveFeedTask().execute();//getactualtime

        while(true){


            ///users

            final DatabaseReference dbref = Database.getInstance().mDatabase;
            dbref.child(Constants.ACTIVE_USERS_TABLE).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null) {
                        Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();

                        for (Object obj : objectMap.values()) {
                            if (obj instanceof Map) {
                                Map<String, Object> values = (Map<String, Object>) obj;


                                String username = values.get(Constants.ACTIVE_USERS_TABLE_NICKNAME).toString();
                                long oldtime = (long) values.get(Constants.ACTIVE_USERS_TABLE_DATETIME);
                                new RetrieveFeedTask().execute();//getactualtime
                                //Log.d("olduser", "onDataChange: "+username+" na mazanie " + (c.getTimeInMillis() - oldTEime));
                                if(oldtime != 0 && currentTime - oldtime > Constants.TEN_SECONDS_IN_MILIS){
                                    //self.delete
                                    Log.d("olduser", "onDataChange: "+username+" na mazanie " + (currentTime - oldtime));
                                    //deleting user
                                    //TODO SEND GPS
                                    //send toast GPS LOST
                                    Database.getInstance().delete_user(username);

                                    if(MapsActivity.user != null && MapsActivity.user.getName().equals(username)){
                                        MapsActivity.user = null;
                                    }
                                }


                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            //bombclean


            dbref.child(Constants.ACTIVE_BOMB_TABLE).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null) {
                        Map<String, Object> objectMap;
                        if(dataSnapshot.getValue() instanceof Map){
                            objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                        }else{
                            ArrayList<Object> x = (ArrayList<Object>) dataSnapshot.getValue();
                            objectMap = new HashMap<String, Object>();
                            for (Object k : x){
                                if(k != null){
                                    HashMap<String,Object> o = (HashMap<String, Object>) k;
                                    objectMap.put(o.get("ID").toString(),o);
                                }
                            }

                        }

                        for (Object obj : objectMap.values()) {
                            if (obj instanceof Map) {
                                Map<String, Object> values = (Map<String, Object>) obj;

                                /*

                                long datetime;
                                String who;
                                int remaining_time;
                                double lati;
                                double longi;
                                 */

                                long olddatetime = (long) values.get(Constants.ACTIVE_BOMB_TABLE_DATETIME);
                                new RetrieveFeedTask().execute();//getactualtime
                                //Log.d("bomb",(c.getTimeInMillis()-olddatetime) + "");
                                if(currentTime-olddatetime > Constants.TEN_SECONDS_IN_MILIS ){
                                    //Log.d("bomb",(c.getTimeInMillis()-olddatetime) + "");
                                    long id = (long) values.get(Constants.ACTIVE_BOMB_TABLE_ID);
                                    Bomb bomb = new Bomb(olddatetime,"",60,0.0,0.0);
                                    bomb.setId((int)id);
                                    Database.getInstance().delete_bomb(bomb);

                                }



                            }
                        }


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
