package com.example.robo.atomicbomberman;

/**
 * Created by Robo on 30.03.2017.
 */

public class Constants {

    public static final String HOST = "194.87.102.198";

    //ACTIVE_USER
    public static final String ACTIVE_USERS_TABLE = "ACTIVE_USERS";
    public static final String ACTIVE_USERS_TABLE_NICKNAME = "NICKNAME";
    public static final String ACTIVE_USERS_TABLE_LONGI = "LONGITUDE";
    public static final String ACTIVE_USERS_TABLE_LATI = "LATITUDE";
    public static final String ACTIVE_USERS_TABLE_DATETIME = "DATETIME";

    //TOASTS
    public static final String WRONG_PASSWORD = "Wrong Password!";

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
    public static final String REGISTRED_USERS_TABLE_BONUS = "BONUS";

    public static final String PREFERENCE_NAME = "User";
    public static final String INTENT_NAME = "name";
    public static final String PUT_BOMB_INTENT = "PUT_BOMB_INTENT";
    public static final String WAITING_TIME = "WAITING_TIME";

    public static final String PUT_BOMB_TEXT = "PUT BOMB!";
    public static final String PUT_BOMB_AVAILABLE = "PUT BOMB AVAILABLE on ";


    //ACTIVE_BOMBS

    public static final String ACTIVE_BOMB_TABLE = "ACTIVE_BOMBS";
    public static final String ACTIVE_BOMB_TABLE_DATETIME = "DATETIME";
    public static final String ACTIVE_BOMB_TABLE_WHO = "WHO";
    public static final String ACTIVE_BOMB_TABLE_REMAINING_TIME = "REMAINING_TIME";
    public static final String ACTIVE_BOMB_TABLE_LONGI = "LONGITUDE";
    public static final String ACTIVE_BOMB_TABLE_LATI = "LATITUDE";
    public static final String ACTIVE_BOMB_TABLE_ID = "ID";


    //ALERT_TOOLBAR STRINGS

    public static final String YOU_ARE_IN_SAFE = "YOU ARE IN SAFE";
    public static final String YOU_ARE_IN_DANGER = "YOU ARE IN DANGER , MOVE AWAY !!!";
    public static final String SCORE = "SCORE : ";
    public static final String LOGIN_SUCCESFULL = "Login Succesfull";
    public static final String MESSAGES = "MESSAGES";
    public static final String ALERT = "ALERT!!!";
    public static final String EXIT = "Exit";
    public static final String CANCEL = "Cancel";
    public static final String ALERT_MESSAGE = "If you loging out now you may be hited by bomb";


    private Constants(){

    }

}
