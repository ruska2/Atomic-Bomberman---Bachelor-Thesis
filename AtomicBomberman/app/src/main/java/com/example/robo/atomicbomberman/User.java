package com.example.robo.atomicbomberman;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Robo on 08.01.2017.
 */

public class User {
    static String nickname;
    double lati;
    double longi;

    public User(String name, double lati, double longi){
        this.nickname = name;
        this.lati = lati;
        this.longi = longi;
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

    public void setName(String name){
        this.nickname = name;
    }

    public  void setLati(double lati){
        this.lati = lati;
    }

    public  void setLongi(double longi){
        this.longi = longi;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put(Constants.getInstance().ACTIVE_USERS_TABLE_NICKNAME, nickname);
        result.put(Constants.getInstance().ACTIVE_USERS_TABLE_LATI, lati);
        result.put(Constants.getInstance().ACTIVE_USERS_TABLE_LONGI, longi);


        return result;
    }

}
