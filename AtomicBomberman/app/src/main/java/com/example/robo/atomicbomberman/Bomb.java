package com.example.robo.atomicbomberman;

import java.util.HashMap;
import java.util.Map;


public class Bomb {
    int id;
    long datetime;
    String who;
    int remaining_time;
    double lati;
    double longi;

    public Bomb(long datetime,String who, int remaining_time, double lati, double longi){
        this.datetime = datetime;
        this.who = who;
        this.remaining_time = remaining_time;
        this.lati = lati;
        this.longi = longi;
    }

    public  Bomb(){};

    public long getDatetime(){
        return datetime;
    }

    public String getWho(){return who;}

    public  int getRemaining_time(){
        return remaining_time;
    }

    public double getLatitude(){
        return lati;
    }

    public double getLongitude(){
        return longi;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setDatetime(long datetime){
        this.datetime = datetime;
    }

    public  void setWho(String who){
        this.who = who;
    }

    public  void setRemaining_time(int remaining_time){
        this.remaining_time = remaining_time;
    }

    public void setLatitude(double latitude){this.lati = latitude;}

    public void setLongitude(double longitude){this.longi = longitude;}

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put(Constants.getInstance().ACTIVE_BOMB_TABLE_ID,id);
        result.put(Constants.getInstance().ACTIVE_BOMB_TABLE_DATETIME, datetime);
        result.put(Constants.getInstance().ACTIVE_BOMB_TABLE_WHO, who);
        result.put(Constants.getInstance().ACTIVE_BOMB_TABLE_REMAINING_TIME, remaining_time);
        result.put(Constants.getInstance().ACTIVE_BOMB_TABLE_LATI, lati);
        result.put(Constants.getInstance().ACTIVE_BOMB_TABLE_LONGI, longi);



        return result;
    }

}
