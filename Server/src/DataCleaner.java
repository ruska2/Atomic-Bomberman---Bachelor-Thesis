
import com.example.robo.atomicbomberman.Constants;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Robo on 23.04.2017.
 */

public class DataCleaner extends Thread {

    static long currentTime = 0;
    int time = 0;

    @Override
    public void start() {
        // do something in the actual (old) thread
        super.start();

    }



    @Override
    public void run() {
        // do something in a new thread if 'called' by super.start()

        while (true) {


            final DatabaseReference dbref = Server.db.databaseReference;

            dbref.child(Constants.TIME).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    currentTime = (long) dataSnapshot.getValue();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            dbref.child(Constants.ACTIVE_USERS_TABLE).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();

                        for (Object obj : objectMap.values()) {
                            if (obj instanceof Map) {
                                Map<String, Object> values = (Map<String, Object>) obj;


                                String username = values.get(Constants.ACTIVE_USERS_TABLE_NICKNAME).toString();
                                long oldtime = (long) values.get(Constants.ACTIVE_USERS_TABLE_DATETIME);

                                if (oldtime != 0 && currentTime - oldtime > Constants.ONE_MINUTE) {
                                    Server.db.deleteUser(username);
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

                                long olddatetime = (long) values.get(Constants.ACTIVE_BOMB_TABLE_DATETIME);

                                if (currentTime - olddatetime > 65000) {
                                    long id = (long) values.get(Constants.ACTIVE_BOMB_TABLE_ID);
                                    Server.db.deleteBomb(id + "");

                                }


                            }
                        }


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            Server.db.updateActualTime();

            if (time == 0) {
                Server.db.databaseReference.child(Constants.TRACK_TABLE).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();


                            for (String key : objectMap.keySet()) {
                                    long x = currentTime - (long) ((Map) objectMap.get(key)).get(Constants.ACTIVE_USERS_TABLE_DATETIME);
                                    if ( x > Constants.TWO_DAYS) {
                                        Server.db.removeTrack(key);
                                    }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            if (time >= Constants.TWO_DAYS) {
                time = 0;
            }

            System.gc();

            try {
                sleep(500);
                time += 500;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}
