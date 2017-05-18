import com.example.robo.atomicbomberman.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Robo on 09.05.2017.
 */
public class QuestChecker {

    String who;
    String whom;

    QuestChecker(String who , String whom ){
        this.who = who;
        this.whom = whom;
    }

    public void check(){
        Server.db.databaseReference.child(Constants.QUEST_TABLE).child(who).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    if(dataSnapshot.getValue().toString().equals(whom)){
                        BonusChecker.updateUserScore(who,100);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
