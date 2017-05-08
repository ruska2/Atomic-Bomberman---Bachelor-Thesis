package com.example.robo.atomicbomberman;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

import static android.widget.Toast.makeText;

public class LoginActivity extends AppCompatActivity {


    Database db = Database.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                112);;


        if (getPref(Constants.PREFERENCE_NAME, getApplicationContext()) != null && !getPref(Constants.PREFERENCE_NAME, getApplicationContext()).equals("")) {
            String name = getPref(Constants.PREFERENCE_NAME, this);

            Intent myintent = new Intent(LoginActivity.this, MapsActivity.class);
            myintent.putExtra(Constants.INTENT_NAME, name);

            startActivity(myintent);

            finish();


        } else {
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
                    EditText password = (EditText) findViewById(R.id.logpassword);

                    if (name.getText().length() > 0 && password.getText().length() > 0) {

                        final String namestr = name.getText().toString();
                        final String passwordstr = password.getText().toString();

                        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();

                        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                        String imei = telephonyManager.getDeviceId();


                        new LoginSenderClient(new LoginUser(namestr,passwordstr,false,imei)).execute();

                        db.mDatabase.child(Constants.MESSAGES).child(namestr).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue()!= null){
                                    String mes = dataSnapshot.getValue().toString();
                                    final Toast t = Toast.makeText(LoginActivity.this, mes, Toast.LENGTH_SHORT);
                                    t.show();
                                    if(mes.equals(Constants.LOGIN_SUCCESFULL)){
                                        Intent myintent = new Intent(LoginActivity.this, MapsActivity.class);
                                        myintent.putExtra(Constants.INTENT_NAME, namestr);
                                        putPref(Constants.PREFERENCE_NAME, namestr, getApplicationContext());
                                        startActivity(myintent);

                                        finish();
                                    }

                                    new Timer().schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            // Your database code here
                                           t.cancel();
                                        }
                                    }, 2000);
                                    progressDialog.dismiss();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }else{
                        makeText(LoginActivity.this,Constants.WRONG_NAME + " or " + Constants.WRONG_PASSWORD, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

}
