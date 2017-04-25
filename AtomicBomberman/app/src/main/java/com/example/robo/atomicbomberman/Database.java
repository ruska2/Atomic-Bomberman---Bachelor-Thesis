/**
 * Created by Robo on 08.01.2017.
 */

package com.example.robo.atomicbomberman;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


public class Database {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();
    private static Database instance = null;


    private Database(){

    }

    public static Database getInstance() {
        if(instance == null) {
            instance = new Database();
        }
        return instance;
    }


    // insert registracie

    public void insert_registred_user(String nickname, String password, String email){

        RegistredUser ruser = new RegistredUser(nickname,password,email);
        mDatabase.child(Constants.REGISTRED_USERS_TABLE).child(nickname).setValue(ruser.toMap());

    }


    //insert aktivneho uzivatela

    public void insert_user(String name, double lati, double longi,long time) {
        User user = new User(name, lati, longi,time);
        mDatabase.child(Constants.ACTIVE_USERS_TABLE).child(name).setValue(user.toMap());
    }


    // update usera
    public void update_active_user(final User user, double lati, double longi,long datetime) {
        user.setLati(lati);
        user.setLongi(longi);
        user.setDatetime(datetime);
        Query query = mDatabase.child(Constants.ACTIVE_USERS_TABLE).orderByChild(Constants.ACTIVE_USERS_TABLE_NICKNAME).equalTo(user.getName());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                    String key = nodeDataSnapshot.getKey(); //
                    String path = "/" + dataSnapshot.getKey() + "/" + key;
                    Map<String, Object> result;
                    result = user.toMap();
                    mDatabase.child(path).updateChildren(result);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    // mazanie neaktivneho uzivatela
    public void delete_user(String nickname) {
        Query query = mDatabase.child(Constants.ACTIVE_USERS_TABLE).orderByChild(Constants.ACTIVE_USERS_TABLE_NICKNAME).equalTo(nickname);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                    String key = nodeDataSnapshot.getKey(); //
                    String path = "/" + dataSnapshot.getKey() + "/" + key;
                    mDatabase.child(path).removeValue();
                }catch (Exception e){}

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void insert_bomb(String id ,long datetime, String who , int remaining_time, double lati , double longi ){

        Bomb bomb = new Bomb(datetime,who,remaining_time,lati,longi);
        bomb.setId(Integer.parseInt(id));
        mDatabase.child(Constants.ACTIVE_BOMB_TABLE).child(id).setValue(bomb.toMap());

    }

    public void insert_bomb(Bomb bomb ){

        mDatabase.child(Constants.ACTIVE_BOMB_TABLE).child(String.valueOf(bomb.id)).setValue(bomb.toMap());

    }

    //update bomb

    public void update_active_bomb(final Bomb bomb) {

        Query query = mDatabase.child(Constants.ACTIVE_BOMB_TABLE).orderByChild(Constants.ACTIVE_BOMB_TABLE_ID).equalTo(bomb.id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                    String key = nodeDataSnapshot.getKey(); //
                    String path = "/" + dataSnapshot.getKey() + "/" + key;
                    Map<String, Object> result;
                    result = bomb.toMap();
                    mDatabase.child(path).updateChildren(result);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void delete_bomb(final Bomb bomb) {
        Query query = mDatabase.child(Constants.ACTIVE_BOMB_TABLE).orderByChild(Constants.ACTIVE_BOMB_TABLE_ID).equalTo(bomb.id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                    String key = nodeDataSnapshot.getKey(); //
                    String path = "/" + dataSnapshot.getKey() + "/" + key;
                    mDatabase.child(path).removeValue();
                }catch (Exception e){}

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void update_registred_user(final RegistredUser registredUser) {

        Query query = mDatabase.child(Constants.REGISTRED_USERS_TABLE).orderByChild(Constants.REGISTRED_USERS_TABLE_NICNAKME).equalTo(registredUser.getName());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                    String key = nodeDataSnapshot.getKey(); //
                    String path = "/" + dataSnapshot.getKey() + "/" + key;
                    Map<String, Object> result;
                    result = registredUser.toMap();
                    mDatabase.child(path).updateChildren(result);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }





}
