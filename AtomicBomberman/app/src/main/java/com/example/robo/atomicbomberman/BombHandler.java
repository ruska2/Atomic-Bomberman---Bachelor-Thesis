package com.example.robo.atomicbomberman;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BombHandler extends Service {

    ArrayList<Bomb> bombs = new ArrayList<>();
    Database db = Database.getInstance();
    int counter = 10;
    LocalBroadcastManager broadcaster;
    boolean enabled_put = false;
    public static long currentTime = 0;

    public BombHandler() {

        broadcaster = LocalBroadcastManager.getInstance(this);
        new RetrieveFeedTask().execute();//getactualtime

        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {

                //Log.d("test",bombs.toString());
                update_bombs();

                if(counter < 0){
                    counter = 0;
                    enabled_put = true;
                }

                if(counter > -1 && !enabled_put){
                    sendResult(String.valueOf(counter));
                    counter--;
                }

            }
        }, 0, 1, TimeUnit.SECONDS);

    }


    public void sendResult(String message) {
        Intent intent = new Intent(Constants.PUT_BOMB_INTENT);
        if(message != null)
            intent.putExtra(Constants.WAITING_TIME, message);
        broadcaster.sendBroadcast(intent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        Bomb n = (Bomb) intent.getExtras().getSerializable("BOMB");
        counter = 10;
        enabled_put = false;
        if(!bombs.contains(n))bombs.add(n);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public void update_bombs(){
        ArrayList<Bomb> removed = new ArrayList<>();
        for(Bomb bomb : bombs){
            if(bomb.getRemaining_time() != 0){
                bomb.setRemaining_time(bomb.getRemaining_time()-1);
            }else{
                //bomb is 0 check if someone hitted or avoided update registred users score
                //need to get all activ users and check if they are in circle range if they then if score is highger
                //then 0 - if they avoidied bomb which is near then get score and if this bomb hitted someone add score to bomb user

                removed.add(bomb);
            }
            new RetrieveFeedTask().execute();//getactualtime
            bomb.setDatetime(currentTime);
            db.update_active_bomb(bomb);
        }

        for(Bomb bomb: removed){
            bombs.remove(bomb);
            db.delete_bomb(bomb);
        }
    }

    public void set_counter(int c){
        counter = c;
    }

}
