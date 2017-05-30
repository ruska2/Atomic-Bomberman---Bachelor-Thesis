import com.example.robo.atomicbomberman.Constants;

/**
 * Created by Robo on 15.05.2017.
 */
public class BonusGenerator implements Runnable {
    @Override
    public void run() {
        while(true){
            Server.db.removeBonuses();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new GetAllTracks().getAllTracks();

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
