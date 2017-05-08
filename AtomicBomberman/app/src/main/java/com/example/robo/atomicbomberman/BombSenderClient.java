package com.example.robo.atomicbomberman;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class BombSenderClient extends AsyncTask<Object, Object, String> {

    String dstAddress = Constants.HOST;
    int dstPort = 1342;
    Bomb bomb;

    BombSenderClient(Bomb b){
        bomb = b;
    }

    @Override
    protected String doInBackground(Object... arg0) {

        Socket socket = null;

        try {
            socket = new Socket(dstAddress, dstPort);

            InputStream inputStream = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            PrintStream p = new PrintStream(out);
            ObjectOutputStream objectOutput = new ObjectOutputStream(out);
            objectOutput.writeObject(bomb);

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