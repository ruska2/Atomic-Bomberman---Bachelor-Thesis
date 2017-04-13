package com.example.robo.atomicbomberman;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    Database db = Database.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if(getPref(Constants.PREFERENCE_NAME,getApplicationContext()) != null && !getPref(Constants.PREFERENCE_NAME,getApplicationContext()).equals(""))
        {
            String name = getPref(Constants.PREFERENCE_NAME,this);

            Intent myintent = new Intent(LoginActivity.this, MapsActivity.class);
            myintent.putExtra(Constants.INTENT_NAME, name);

            startActivity(myintent);

            finish();


        }
        else
        {
            // Access the default SharedPreferences
            Button reg = (Button) findViewById(R.id.reg_btn);
            reg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myintent = new Intent(LoginActivity.this, RegistrationActivity.class);
                    startActivity(myintent);
                }
            });



            final Button log = (Button) findViewById(R.id.button);
            log.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText name = (EditText) findViewById(R.id.editText);

                    if (name.getText().length() > 0) {

                        String namestr = name.getText().toString();

                        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();


                        Query query = db.mDatabase.child(Constants.REGISTRED_USERS_TABLE).orderByChild(Constants.REGISTRED_USERS_TABLE_NICNAKME).equalTo(namestr);
                        if(RegistrationActivity.validate_email(namestr)){
                            query = db.mDatabase.child(Constants.REGISTRED_USERS_TABLE).orderByChild(Constants.REGISTRED_USERS_TABLE_MAIL).equalTo(namestr);
                        }
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(dataSnapshot.getValue() != null){

                                    EditText name = (EditText) findViewById(R.id.editText);
                                    EditText password = (EditText) findViewById(R.id.logpassword);

                                    String pw = password.getText().toString();
                                    String namestr = name.getText().toString();

                                    Map<String, Object> objectMap = (HashMap<String, Object>)
                                            dataSnapshot.getValue();

                                    Map<String, Object> values = null;

                                    for (Object obj : objectMap.values()) {
                                        if (obj instanceof Map) {
                                            values = (Map<String, Object>) obj;
                                        }
                                    }

                                    if(values.get(Constants.REGISTRED_USER_TABLE_PASSWORD).equals(pw))
                                    {
                                        Intent myintent = new Intent(LoginActivity.this, MapsActivity.class);
                                        myintent.putExtra(Constants.INTENT_NAME, values.get(Constants.REGISTRED_USERS_TABLE_NICNAKME).toString());
                                        putPref(Constants.PREFERENCE_NAME, namestr, getApplicationContext());
                                        startActivity(myintent);

                                        finish();

                                    }
                                    else{

                                        Toast.makeText(getApplicationContext(), Constants.WRONG_PASSWORD, Toast.LENGTH_SHORT).show();
                                    }

                                }else{
                                    Toast.makeText(getApplicationContext(),Constants.USERNAME_NOT_EXIST, Toast.LENGTH_SHORT).show();
                                }

                                progressDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }

                        });

                    } else {
                        Toast.makeText(getApplicationContext(), Constants.SET_NICKNAME, Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }
    }


    public static void putPref(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getPref(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
