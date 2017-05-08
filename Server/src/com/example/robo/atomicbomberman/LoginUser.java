package com.example.robo.atomicbomberman;

import java.io.Serializable;

/**
 * Created by Robo on 05.05.2017.
 */

public class LoginUser implements Serializable {
    String name;
    String password;
    boolean delete;
    String imei;
    static final long serialVersionUID = 3L;

    LoginUser(String name , String password, boolean delete, String imei){
        this.name = name;
        this.password = password;
        this.delete = delete;
        this.imei = imei;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public boolean getDelete(){
        return delete;
    }

    public String getImei(){
        return imei;
    }
}
