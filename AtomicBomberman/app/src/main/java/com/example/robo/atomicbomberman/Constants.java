package com.example.robo.atomicbomberman;

/**
 * Created by Robo on 30.03.2017.
 */

public class Constants {

    private static Constants instance = null;
    //ACTIVE_USER
    public static final String ACTIVE_USERS_TABLE = "ACTIVE_USERS";
    public static final String ACTIVE_USERS_TABLE_NICKNAME = "NICKNAME";
    public static final String ACTIVE_USERS_TABLE_LONGI = "LONGITUDE";
    public static final String ACTIVE_USERS_TABLE_LATI = "LATITUDE";

    //TOASTS
    public static final String WRONG_PASSWORD = "Wrong Password!";
    public static final String USERNAME_NOT_EXIST = "Username not Exist!";
    public static final String SET_NICKNAME = "Set Nickname!";
    public static final String FOUNDED_GPS = "GPS Signal Founded";
    public static final String NOT_VALID_EMAIL = "Not valid E-mail!";
    public static final String SHORT_PASSWORD = "Password too short!";
    public static final String EMAIL_EXISTS= "E-mail exists!";
    public static final String NICKNAME_EXISTS= "Nickname exists!";
    public static final String WRONG_NAME = "Not valid Name";

    //REGISTRED_USER
    public static final String REGISTRED_USERS_TABLE = "REGISTRED_USERS";
    public static final String REGISTRED_USER_TABLE_PASSWORD = "PASSWORD";
    public static final String REGISTRED_USERS_TABLE_NICNAKME = "NICKNAME";
    public static final String REGISTRED_USERS_TABLE_MAIL = "EMAIL";
    public static final String REGISTRED_USERS_TABLE_SCORE = "SCORE";

    public static final String PREFERENCE_NAME = "User";
    public static final String INTENT_NAME = "name";


    //ACTIVE_BOMBS

    public static final String ACTIVE_BOMB_TABLE = "ACTIVE_BOMBS";
    public static final String ACTIVE_BOMB_TABLE_DATETIME = "DATETIME";
    public static final String ACTIVE_BOMB_TABLE_WHO = "WHO";
    public static final String ACTIVE_BOMB_TABLE_REMAINING_TIME = "REMAINING_TIME";
    public static final String ACTIVE_BOMB_TABLE_LONGI = "LONGITUDE";
    public static final String ACTIVE_BOMB_TABLE_LATI = "LATITUDE";
    public static final String ACTIVE_BOMB_TABLE_ID = "ID";

    private Constants(){

    }

    public static Constants getInstance(){
        if(instance == null){
            instance = new Constants();
        }
        return instance;
    }
}
