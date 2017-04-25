package com.example.robo.atomicbomberman;

import android.location.Location;
import android.os.Handler;
import android.os.Message;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DangerChecker extends Thread {

    private String name = null;
    double[] cords = new double[2];
    Database db = Database.getInstance();
    static Handler mHandler;


    DangerChecker(String name){
        this.name = name;
    }

    @Override
    public void start() {
        // do something in the actual (old) thread
        super.start();
    }

    @Override
    public void run() {
        // do something in a new thread if 'called' by super.start()

        getLatLong();

        final DatabaseReference dbref = Database.getInstance().mDatabase;

        //hitcontrol



        dbref.child(Constants.ACTIVE_BOMB_TABLE).addListenerForSingleValueEvent(new ValueEventListener() {
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


                    getLatLong();
                    for (Object obj : objectMap.values()) {
                        if (obj instanceof Map) {
                            Map<String, Object> values = (Map<String, Object>) obj;

                            double lat = (double) values.get(Constants.ACTIVE_USERS_TABLE_LATI);
                            double lom = (double) values.get(Constants.ACTIVE_USERS_TABLE_LONGI);


                            float[] results = new float[1];
                            Location.distanceBetween(cords[0], cords[1], lat, lom, results);

                          //  Log.d("bomb",lat + ","+ lom);
                           // Log.d("user",cords[0] + ","+ cords[1]);



                            float distanceInMeters = results[0];

                            if(distanceInMeters < 80.01){
                                //TODO SEND notific
                                Message message = mHandler.obtainMessage();

                                message.obj = "YOU ARE IN DANGER MOVE AWAY!!!";
                                mHandler.sendMessage(message);
                                break;

                            }else{
                                Message message = mHandler.obtainMessage();

                                message.obj = "YOU ARE IN SAFE";
                                mHandler.sendMessage(message);
                            }



                        }
                    }




                    //Log.d("hod",cords[0]+ ","+cords[1]+ ","+ name);


                }
                else{
                    Message message = mHandler.obtainMessage();

                    message.obj = "YOU ARE IN SAFE";
                    mHandler.sendMessage(message);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void getLatLong(){

        Query query = db.mDatabase.child(Constants.ACTIVE_USERS_TABLE).orderByChild(Constants.REGISTRED_USERS_TABLE_NICNAKME).equalTo(name);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Map<String, Object> objectMap = (HashMap<String, Object>)
                            dataSnapshot.getValue();

                    Map<String, Object> values = null;

                    for (Object obj : objectMap.values()) {
                        if (obj instanceof Map) {
                            values = (Map<String, Object>) obj;
                        }
                    }

                    cords[0] = (double) values.get(Constants.ACTIVE_USERS_TABLE_LATI);
                    cords[1] = (double) values.get(Constants.ACTIVE_USERS_TABLE_LONGI);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
