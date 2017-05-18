
package com.example.robo.atomicbomberman;

import javax.print.DocFlavor;

/**
 * Created by Robo on 30.03.2017.
 */

public class Constants {

    //ACTIVE_USER
    public static final String ACTIVE_USERS_TABLE = "ACTIVE_USERS";
    public static final String ACTIVE_USERS_TABLE_NICKNAME = "NICKNAME";
    public static final String ACTIVE_USERS_TABLE_LONGI = "LONGITUDE";
    public static final String ACTIVE_USERS_TABLE_LATI = "LATITUDE";
    public static final String ACTIVE_USERS_TABLE_DATETIME = "DATETIME";


    //REGISTRED_USER
    public static final String REGISTRED_USERS_TABLE = "REGISTRED_USERS";
    public static final String REGISTRED_USER_TABLE_PASSWORD = "PASSWORD";
    public static final String REGISTRED_USERS_TABLE_NICNAKME = "NICKNAME";
    public static final String REGISTRED_USERS_TABLE_MAIL = "EMAIL";
    public static final String REGISTRED_USERS_TABLE_SCORE = "SCORE";
    public static final String REGISTRED_USERS_TABLE_BONUS = "BONUS";

    //ACTIVE_BOMBS
    public static final String ACTIVE_BOMB_TABLE = "ACTIVE_BOMBS";
    public static final String ACTIVE_BOMB_TABLE_DATETIME = "DATETIME";
    public static final String ACTIVE_BOMB_TABLE_WHO = "WHO";
    public static final String ACTIVE_BOMB_TABLE_REMAINING_TIME = "REMAINING_TIME";
    public static final String ACTIVE_BOMB_TABLE_LONGI = "LONGITUDE";
    public static final String ACTIVE_BOMB_TABLE_LATI = "LATITUDE";
    public static final String ACTIVE_BOMB_TABLE_ID = "ID";

    //SERVER MESSAGE

    public static final String SERVER_MESSAGE_JUMP = "TOO MUCH DISTANCE WHILE CHANGE : JUMPING";
    public static final String SERVER_MESSAGE_USED = "Already logged from another device!";


    //PORT
    public static final int PORT = 1342;

    //OTHER
    public static final String TIME = "TIME";
    public static final String MESSAGES = "MESSAGES";
    public static final String LOGGED_USERS = "LOGGED_USERS";
    public static final String LOGGED_USERS_IMEI = "LOGGED_USERS_IMEI";
    public static final String REGISTRED_USERS_TABLE_PASSWORD = "REGISTRED_USERS_TABLE_PASSWORD";
    public static final String USER_REGISTRED = "User Registred";
    public static final String LOGIN_SUCCESFULL = "Login Succesfull";
    public static final String WRONG_PASSWORD = "Wrong password";
    public static final String USER_NOT_REGISTRED = "User not registred";

    //TRACK

    public static final String TRACK_TABLE = "USER_TRACKS";
    public static final String QUEST_TABLE = "QUESTS";
    public static final String BONUSES = "BONUSES";

    private Constants(){

    }

}
