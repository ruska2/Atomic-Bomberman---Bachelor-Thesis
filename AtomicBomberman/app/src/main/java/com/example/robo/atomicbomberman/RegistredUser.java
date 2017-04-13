package com.example.robo.atomicbomberman;

import java.util.HashMap;
import java.util.Map;


public class RegistredUser {
    static String nickname;
    String password;
    String email;
    int score;

    public RegistredUser(String name, String password, String email){
        this.nickname = name;
        this.password = password;
        this.email = email;
        score = 0;
    }

    public  RegistredUser(){};
    public RegistredUser(String name){
        this.nickname = name;
    }

    public String getName(){
        return nickname;
    }

    public int getScore(){return score;}

    public  String getMail(){
        return email;
    }

    public  String getPassword(){
        return  password;
    }

    public void setName(String name){
        this.nickname = name;
    }

    public  void setPassword(String password){
        this.password= password;
    }

    public  void setMail(String email){
        this.email = email;
    }

    public void setScore(int score){this.score = score;}

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put(Constants.REGISTRED_USERS_TABLE_NICNAKME, nickname);
        result.put(Constants.REGISTRED_USER_TABLE_PASSWORD, password);
        result.put(Constants.REGISTRED_USERS_TABLE_MAIL, email);
        result.put(Constants.REGISTRED_USERS_TABLE_SCORE,score);



        return result;
    }

}
