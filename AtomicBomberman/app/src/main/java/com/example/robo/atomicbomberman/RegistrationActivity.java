package com.example.robo.atomicbomberman;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    Database db = Database.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        TextView back_to_login = (TextView) findViewById(R.id.link_to_login);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button register = (Button) findViewById(R.id.btnRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText namet = (EditText) findViewById(R.id.reg_nickname);
                String name = namet.getText().toString();

                EditText password = (EditText) findViewById(R.id.reg_password);
                String pw = password.getText().toString();

                EditText email = (EditText) findViewById(R.id.reg_email);
                String mail = email.getText().toString();


                if(name.equals("") || name.length() < 4 ){
                    Toast.makeText(RegistrationActivity.this,Constants.SET_NICKNAME, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!validate_name(name) ){
                    Toast.makeText(RegistrationActivity.this,Constants.WRONG_NAME, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!validate_email(mail)){
                    Toast.makeText(RegistrationActivity.this, Constants.NOT_VALID_EMAIL, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(pw.equals("") || pw.length() < 4){
                    Toast.makeText(RegistrationActivity.this,Constants.SHORT_PASSWORD, Toast.LENGTH_SHORT).show();
                    return;
                }


                Query query = db.mDatabase.child(Constants.REGISTRED_USERS_TABLE);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null){

                            EditText namet = (EditText) findViewById(R.id.reg_nickname);
                            String name = namet.getText().toString();

                            EditText password = (EditText) findViewById(R.id.reg_password);
                            String pw = password.getText().toString();

                            EditText email = (EditText) findViewById(R.id.reg_email);
                            String mail = email.getText().toString();


                            Map<String, Object> objectMap = (HashMap<String, Object>)
                                    dataSnapshot.getValue();

                            Map<String, Object> values = null;
                            Boolean insert = true;

                            for (Object obj : objectMap.values()) {

                                if (obj instanceof Map) {
                                    values = (Map<String, Object>) obj;

                                    if(values.get(Constants.REGISTRED_USERS_TABLE_MAIL).equals(mail)){
                                        Toast.makeText(RegistrationActivity.this, Constants.EMAIL_EXISTS, Toast.LENGTH_SHORT).show();
                                        insert = false;
                                        break;
                                    }

                                    if(values.get(Constants.REGISTRED_USERS_TABLE_NICNAKME).equals(name)){
                                        insert = false;
                                        Toast.makeText(RegistrationActivity.this, Constants.NICKNAME_EXISTS, Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }
                            }

                            if(insert){
                                db.insert_registred_user(name,pw,mail);
                                finish();
                            }
                        }
                        else
                        {

                            EditText namet = (EditText) findViewById(R.id.reg_nickname);
                            String name = namet.getText().toString();

                            EditText password = (EditText) findViewById(R.id.reg_password);
                            String pw = password.getText().toString();

                            EditText email = (EditText) findViewById(R.id.reg_email);
                            String mail = email.getText().toString();


                            db.insert_registred_user(name,pw,mail);
                            finish();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        });

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
