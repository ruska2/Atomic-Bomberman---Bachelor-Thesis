package com.example.robo.atomicbomberman;

import android.os.AsyncTask;
import android.os.SystemClock;

/**
 * Created by Robo on 25.04.2017.
 */

class RetrieveFeedTask extends AsyncTask<String, Void, Long> {

    protected Long doInBackground(String... urls) {

        try {

            long now = 0;
            SntpClient client = new SntpClient();
            if (client.requestTime("time.google.com",5000)) {
                now = client.getNtpTime() + SystemClock.elapsedRealtime() - client.getNtpTimeReference();
            }
            MapsActivity.currentTime = now;
            BombHandler.currentTime = now;
            DataCleaner.currentTime = now;
            return now;


        } catch (Exception e) {
            return null;
        }

    }
}