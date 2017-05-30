package com.example.robo.atomicbomberman;

import android.widget.Button;

/**
 * Created by Robo on 06.05.2017.
 */

public class ButtonDecrement implements Runnable {

    Button putbomb;
    int counter = Constants.THIRTYSEC;
    ButtonDecrement(Button b){
        putbomb = b;
    }

    @Override
    public void run() {
        while (counter != -1){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter--;
            putbomb.post(new Runnable() {
                @Override
                public void run() {
                    putbomb.setText(Constants.PUT_BOMB_TEXT + " ("+ counter + ")");
                }
            });

        }
        putbomb.post(new Runnable() {
            @Override
            public void run() {
                putbomb.setClickable(true);
                putbomb.setEnabled(true);
                putbomb.setText(Constants.PUT_BOMB_TEXT);
            }
        });

    }
}
