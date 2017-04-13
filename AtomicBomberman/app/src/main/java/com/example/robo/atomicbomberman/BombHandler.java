package com.example.robo.atomicbomberman;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BombHandler extends Service {

    ArrayList<Bomb> bombs = new ArrayList<>();
    Constants constants = Constants.getInstance();
    Database db = Database.getInstance();
    int counter = 0;

    public BombHandler() {

        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                Log.d("name",bombs.toString());
                counter++;
                update_bombs();
            }
        }, 0, 1, TimeUnit.SECONDS);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Bomb n = (Bomb) intent.getExtras().getSerializable("BOMB");

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
                removed.add(bomb);
            }
            db.update_active_bomb(bomb);
        }

        for(Bomb bomb: removed){
            bombs.remove(bomb);
            db.delete_bomb(bomb);
        }
    }

}
