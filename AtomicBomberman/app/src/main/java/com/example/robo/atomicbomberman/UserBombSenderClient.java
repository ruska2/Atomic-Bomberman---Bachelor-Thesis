package com.example.robo.atomicbomberman;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Robo on 30.05.2017.
 */

public class UserBombSenderClient extends AsyncTask<Object, Object, String> {
    static String dstAddress = Constants.HOST;
    static int dstPort = 1342;
    static Socket socket;
    User u;
    static ObjectOutputStream objectOutput;
    static OutputStream out;


    @Override
    protected String doInBackground(Object... arg0) {

        try {
            socket = new Socket(dstAddress, dstPort);

            out = socket.getOutputStream();

            objectOutput = new ObjectOutputStream(out);

            sendUser(u);

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public void sendUser(User u) throws IOException {
        objectOutput.writeUnshared(u);
    }

    public void close() throws IOException {
        objectOutput.close();
        out.close();
    }

    public void  sendBomb(Bomb b) throws IOException {
        objectOutput.writeUnshared(b);
    }

    public void sendLogout(LoginUser l) throws  IOException{
        objectOutput.writeUnshared(l);
    }

    public void setUser(User u){
        this.u = u;
    }

}