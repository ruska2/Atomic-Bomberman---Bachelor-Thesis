package com.example.robo.atomicbomberman;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Robo on 08.01.2017.
 */

public class User implements Serializable {
    String nickname;
    double lati;
    double longi;
    long datetime;
    boolean delete = false;
    static final long serialVersionUID = 2L;


    public User(String name, double lati, double longi,long datetime){
        this.nickname = name;
        this.lati = lati;
        this.longi = longi;
        this.datetime = datetime;
    }

    public  User(){};
    public User(String name){
        this.nickname = name;
    }

    public String getName(){
        return nickname;
    }

    public  double getLati(){
        return lati;
    }

    public  double getLongi(){
        return  longi;
    }

    public boolean getDelete(){return delete;}

    public void setDelete(boolean delete){
        this.delete = delete;
    }

    public long getDatetime(){return datetime;}

    public void setName(String name){
        this.nickname = name;
    }

    public  void setLati(double lati){
        this.lati = lati;
    }

    public  void setLongi(double longi){
        this.longi = longi;
    }

    public void setDatetime(long datetime) {this.datetime = datetime;}


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put(Constants.ACTIVE_USERS_TABLE_NICKNAME, nickname);
        result.put(Constants.ACTIVE_USERS_TABLE_LATI, lati);
        result.put(Constants.ACTIVE_USERS_TABLE_LONGI, longi);
        result.put(Constants.ACTIVE_USERS_TABLE_DATETIME,datetime);


        return result;
    }

}
