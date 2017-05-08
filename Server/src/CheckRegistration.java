import com.example.robo.atomicbomberman.Constants;
import com.example.robo.atomicbomberman.RegistredUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Robo on 05.05.2017.
 */
public class CheckRegistration {

    RegistredUser ru;

    CheckRegistration(RegistredUser ru){
        this.ru = ru;
        System.out.println(ru.getName());
    }

    public void checkCorretness(){


        if(ru.getName().equals("") || ru.getName().length() < 4 ){
            return;
        }

        if(!validate_name(ru.getName()) ){
            return;
        }

        if(!validate_email(ru.getMail())){
            return;
        }

        if(ru.getPassword().equals("") || ru.getPassword().length() < 4){
            return;
        }

        Server.db.databaseReference.child(ru.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){

                }else{
                    Server.db.addRegistredUser(ru);
                    Server.db.addRegistredUser(ru.getName(),ru.getPassword());
                    Server.db.addDeleteMessage(ru.getName(), Constants.USER_REGISTRED);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //pozrieme ci je in ked nie pozrieme correctnost hodnot ked je korektny pridame do registred a pasword do 'REGISTRED_USERS_PASSWORDS'
        //ked nie je in pozrieme korekcie a pridame  pass do RRISTRED USERS PASSWORD
        //POTOM PRENASTAVIT PRI LOGINU KONTROL PASSWORDU NA TABULKU REGISTRED USESRS PASSWORDS
    }


    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static final Pattern VALID_NAME_REGEX =
            Pattern.compile("^[A-Za-z0-9]+$", Pattern.CASE_INSENSITIVE);


    public static boolean validate_email(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    public static boolean validate_name(String nameStr) {
        Matcher matcher = VALID_NAME_REGEX .matcher(nameStr);
        return matcher.find();
    }
}




