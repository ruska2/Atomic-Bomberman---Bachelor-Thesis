import com.example.robo.atomicbomberman.Constants;

/**
 * Created by Robo on 15.05.2017.
 */
public class BonusCounter {

    String name;

    BonusCounter(String n){
        name = n;
    }

    public void make() throws InterruptedException {
        Thread.sleep(Constants.ONE_MINUTE);

        Server.db.removeBonus(name);
    }
}
