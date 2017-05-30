import com.example.robo.atomicbomberman.Constants;
import com.example.robo.atomicbomberman.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.tasks.Task;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Robo on 09.05.2017.
 */
public class BonusChecker {

    public void checkBonus(final String who, final String whom, final double distance){
        Server.db.databaseReference.child(Constants.REGISTRED_USERS_TABLE).child(whom).child(Constants.REGISTRED_USERS_TABLE_BONUS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    boolean bonus = (boolean) dataSnapshot.getValue();
                    if(!bonus){
                        if(distance < 80.01){

                            if(!who.equals(whom)){
                                updateUserScore(whom,- (80 - (int)distance));
                                updateUserScore(who,80-((int)distance));

                            }else{
                                updateUserScore(whom,- (80 - (int)distance));

                            }

                            new QuestChecker(who,whom).check();

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void updateUserScore(final String nick , final int change){


        DatabaseReference childReference = Server.db.databaseReference.child(Constants.REGISTRED_USERS_TABLE).child(nick);
        childReference.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // TODO Auto-generated method stub
                if(dataSnapshot.getValue() != null) {
                    Map<String, Object> values = (HashMap<String, Object>) dataSnapshot.getValue();

                    int score = (int)(long) values.get(Constants.REGISTRED_USERS_TABLE_SCORE);

                    Server.db.updateScore(nick,score,change);


                }

            }
        });
    }

}
