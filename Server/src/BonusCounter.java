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
        Thread.sleep(60000);

        Server.db.databaseReference.child(Constants.REGISTRED_USERS_TABLE).child(name).child(Constants.REGISTRED_USERS_TABLE_BONUS).setValue(false);
    }
}
