package com.example.robo.atomicbomberman;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class UserSenderClient extends AsyncTask<Object, Object, String> {

    String dstAddress = Constants.HOST;
    int dstPort = 1342;
    User user;

    UserSenderClient(User u){
        user = u;
    }

    @Override
    protected String doInBackground(Object... arg0) {

        Socket socket = null;

        try {
            socket = new Socket(dstAddress, dstPort);

            OutputStream out = socket.getOutputStream();

            ObjectOutputStream objectOutput = new ObjectOutputStream(out);
            objectOutput.writeObject(user);


        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

}