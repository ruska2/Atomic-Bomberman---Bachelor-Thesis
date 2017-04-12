package com.example.robo.atomicbomberman;

/**
 * Created by Robo on 30.03.2017.
 */

public class Constants {

    private static Constants instance = null;
    //ACTIVE_USER
    public final String ACTIVE_USERS_TABLE = "ACTIVE_USERS";
    public final String ACTIVE_USERS_TABLE_NICKNAME = "NICKNAME";
    public final String ACTIVE_USERS_TABLE_LONGI = "LONGITUDE";
    public final String ACTIVE_USERS_TABLE_LATI = "LATITUDE";

    //TOASTS
    public final String WRONG_PASSWORD = "Wrong Password!";
    public final String USERNAME_NOT_EXIST = "Username not Exist!";
    public final String SET_NICKNAME = "Set Nickname!";
    public final String FOUNDED_GPS = "GPS Signal Founded";
    public final String NOT_VALID_EMAIL = "Not valid E-mail!";
    public final String SHORT_PASSWORD = "Password too short!";
    public final String EMAIL_EXISTS= "E-mail exists!";
    public final String NICKNAME_EXISTS= "Nickname exists!";
    public final String WRONG_NAME = "Not valid Name";

    //REGISTRED_USER
    public final String REGISTRED_USERS_TABLE = "REGISTRED_USERS";
    public final String REGISTRED_USER_TABLE_PASSWORD = "PASSWORD";
    public final String REGISTRED_USERS_TABLE_NICNAKME = "NICKNAME";
    public final String REGISTRED_USERS_TABLE_MAIL = "EMAIL";
    public final String REGISTRED_USERS_TABLE_SCORE = "SCORE";

    public final String PREFERENCE_NAME = "User";
    public final String INTENT_NAME = "name";


    //ACTIVE_BOMBS

    public final String ACTIVE_BOMB_TABLE = "ACTIVE_BOMBS";
    public final String ACTIVE_BOMB_TABLE_DATETIME = "DATETIME";
    public final String ACTIVE_BOMB_TABLE_WHO = "WHO";
    public final String ACTIVE_BOMB_TABLE_REMAINING_TIME = "REMAINING_TIME";
    public final String ACTIVE_BOMB_TABLE_LONGI = "LONGITUDE";
    public final String ACTIVE_BOMB_TABLE_LATI = "LATITUDE";
    public final String ACTIVE_BOMB_TABLE_ID = "ID";

    private Constants(){

    }

    public static Constants getInstance(){
        if(instance == null){
            instance = new Constants();
        }
        return instance;
    }
}
