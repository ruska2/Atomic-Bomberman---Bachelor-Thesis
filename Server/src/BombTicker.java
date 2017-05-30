/**
 * Created by Robo on 03.05.2017.
 */
import com.example.robo.atomicbomberman.Bomb;
import com.example.robo.atomicbomberman.Constants;

public class BombTicker implements Runnable {

    Bomb bomb;

    BombTicker(Bomb bomb){
        this.bomb = bomb;
    }
    @Override
    public void run() {
        while(true){
            Runtime r = Runtime.getRuntime();
            Server.db.bombAddUpdate(bomb);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int remaining_time = bomb.getRemaining_time() -1;
            bomb.setRemaining_time(remaining_time);
            if(remaining_time == 0){
                Thread hit_checker = new Thread(new HitChecker(bomb));
                hit_checker.start();
                Server.db.deleteBomb(bomb);
                break;

            }

        }
    }

    public static boolean checkCorrectAdd(Bomb b){
        if(b.getRemaining_time() == Constants.ONE_MINUTE/1000) return true;
        return false;
    }
}
