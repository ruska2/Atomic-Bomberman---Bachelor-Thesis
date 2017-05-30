package com.example.robo.atomicbomberman;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.robo.atomicbomberman.R.id.map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String name = "";
    boolean inserted = false;
    Database db = Database.getInstance();
    static User user;
    boolean paused = false;
    ArrayList<Marker> markers = new ArrayList<>();
    ArrayList<Marker> bomb_markers = new ArrayList<>();
    ArrayList<Circle> bomb_circles = new ArrayList<>();
    ArrayList<Circle> bomb_circles_animations = new ArrayList<>();
    ArrayList<Circle> bonuse_circles = new ArrayList<>();
    ArrayList<Marker> bonuse_markers = new ArrayList<>();

    double lati;
    double longi;
    BroadcastReceiver receiver;
    LocationManager manager;
    LocationListener list;
    public static DangerChecker dangerChecker;
    static long currentTime = 0;
    Handler mHandler;
    String questtext = "";
    String bonustext = "";
    DatabaseReference dbref;
    UserBombSenderClient userBombSender;
    PowerManager.WakeLock wl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        Bundle bundle = getIntent().getExtras();

        name = bundle.getString(Constants.INTENT_NAME);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        wl.acquire();

        dbref = Database.getInstance().mDatabase;



        new RetrieveFeedTask().execute();


        //message hander

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // This is where you do your work in the UI thread.
                // Your worker tells you in the message what to do.
                String mes = (String) message.obj;
                TextView not = (TextView) findViewById(R.id.notfiy_text);
                not.setText(mes);
                if(bonustext.equals(Constants.BONUS_TEXT)){
                    not.setText(bonustext);
                }

                not.setTextColor(Color.RED);
                if(mes.equals(Constants.YOU_ARE_IN_SAFE)){
                    not.setTextColor(Color.GREEN);
                }

            }
        };


        dangerChecker.mHandler = mHandler;

        dbref.child(Constants.REGISTRED_USERS_TABLE).child(name).child(Constants.REGISTRED_USERS_TABLE_BONUS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    boolean hod = (boolean)dataSnapshot.getValue();
                    if(hod){
                        bonustext = Constants.BONUS_TEXT;
                    }
                    else{
                        bonustext = "";
                    }

                    TextView not = (TextView) findViewById(R.id.notfiy_text);
                    not.setText(not.getText().toString() + " " + bonustext);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        dbref.child(Constants.BONUSES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(Circle p : bonuse_circles){
                    p.remove();
                }

                for(Marker m : bonuse_markers){
                    m.remove();
                }
                bonuse_circles.clear();
                bonuse_markers.clear();

                if(dataSnapshot.getValue() != null) {
                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();

                    for (Object obj : objectMap.values()) {
                        if (obj instanceof Map) {
                            Map<String, Object> values = (Map<String, Object>) obj;
                            double la = (double) values.get(Constants.ACTIVE_USERS_TABLE_LATI);
                            double lo = (double) values.get(Constants.ACTIVE_USERS_TABLE_LONGI);

                            Circle circle = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(la, lo))
                                    .radius(2)
                                    .fillColor(Color.BLUE));

                            bonuse_circles.add(circle);

                            IconGenerator iconFactory = new IconGenerator(getApplicationContext());
                            Marker me = mMap.addMarker(new MarkerOptions().position(new LatLng(la, lo)));
                            me.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("BONUS")));

                            bonuse_markers.add(me);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // GET QUEST
        dbref.child(Constants.QUEST_TABLE).child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ImageView quest = (ImageView) findViewById(R.id.imageView6);
                if(dataSnapshot.getValue() == null){
                    quest.setVisibility(View.INVISIBLE);
                    questtext = "";
                }else{
                    quest.setVisibility(View.VISIBLE);

                    String who = dataSnapshot.getValue().toString();
                    questtext = "Explode player " + who + " and you will get extra 100 bonus score";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query query = dbref.child(Constants.REGISTRED_USERS_TABLE).orderByChild(Constants.REGISTRED_USERS_TABLE_NICNAKME).equalTo(name);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    for (Object obj : objectMap.values()) {
                        if (obj instanceof Map) {
                            Map<String, Object> values = (Map<String, Object>) obj;

                            TextView scoretv = (TextView) findViewById(R.id.score_text);
                            int newn = (int) (long)  values.get(Constants.REGISTRED_USERS_TABLE_SCORE);
                            int oldn = 0;
                            if(!scoretv.getText().toString().equals("")) {
                                oldn = Integer.parseInt(scoretv.getText().toString().substring(8));
                            }
                            startCountAnimation(scoretv,oldn,newn);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //GET ACTIVE USERS
        dbref.child(Constants.ACTIVE_USERS_TABLE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //CLEAR MARKERS AND MAP
                for (Marker me : markers) {
                    me.remove();
                }
                markers.clear();

                if (dataSnapshot.getValue() != null) {
                    //CONVERT REURNED OBJECT TO MAP
                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();

                    for (Object obj : objectMap.values()) {
                        if (obj instanceof Map) {
                            //VALUES FROM ONE USER
                            Map<String, Object> values = (Map<String, Object>) obj;
                            double lat = (double)  values.get(Constants.ACTIVE_USERS_TABLE_LATI);
                            double lon = (double)  values.get(Constants.ACTIVE_USERS_TABLE_LONGI);
                            String name = values.get(Constants.ACTIVE_USERS_TABLE_NICKNAME).toString();

                            IconGenerator iconFactory = new IconGenerator(getApplicationContext());
                            //ADD MARKER TO MAP

                            Marker me = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(lat, lon))
                                    .title(name)
                                    .visible(true));

                            me.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(name)));
                            //ADD MARKER TO MARKERS
                            markers.add(me);

                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //GET ALL BOMBS

        dbref.child(Constants.ACTIVE_BOMB_TABLE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (Marker me : bomb_markers) {
                    me.remove();
                }
                bomb_markers.clear();

                for (Circle c : bomb_circles) {
                    c.remove();
                }
                bomb_circles.clear();

                for (Circle c : bomb_circles_animations) {
                    c.remove();
                }

                bomb_circles_animations.clear();


                if (dataSnapshot.getValue() != null) {

                    Map<String, Object> objectMap;
                    if(dataSnapshot.getValue() instanceof Map){
                        objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    }else{
                        ArrayList<Object> x = (ArrayList<Object>) dataSnapshot.getValue();
                        objectMap = new HashMap<String, Object>();
                        for (Object k : x){
                            if(k != null){
                                HashMap<String,Object> o = (HashMap<String, Object>) k;
                                objectMap.put(o.get(Constants.ACTIVE_BOMB_TABLE_ID).toString(),o);
                            }
                        }

                    }


                    for (Object obj : objectMap.values()) {
                        if (obj instanceof Map) {
                            Map<String, Object> values = (Map<String, Object>) obj;

                            double lati = (double) values.get(Constants.ACTIVE_USERS_TABLE_LATI);
                            double longi = (double) values.get(Constants.ACTIVE_BOMB_TABLE_LONGI);


                            String idstr = values.get(Constants.ACTIVE_BOMB_TABLE_REMAINING_TIME).toString();

                            Circle circle = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(lati, longi))
                                    .radius(1)
                                    .fillColor(Color.BLACK));

                            bomb_circles.add(circle);

                            final Circle circle2 = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(lati, longi))
                                    .radius(1)
                                    .fillColor(0x22FF0000)
                                    .strokeColor(Color.RED));


                            startCircleAnimation(circle2);

                            bomb_circles_animations.add(circle2);


                            IconGenerator iconFactory = new IconGenerator(getApplicationContext());
                            Marker me = mMap.addMarker(new MarkerOptions().position(new LatLng(lati, longi)));
                            me.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(idstr)));

                            bomb_markers.add(me);


                        }
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        list = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                dangerChecker = new DangerChecker(name);
                dangerChecker.start();
                if (user == null || !user.getName().equals(name)) {

                    new RetrieveFeedTask().execute();//getactualtime

                    lati = location.getLatitude();
                    longi = location.getLongitude();
                    inserted = true;
                    user = new User(name, location.getLatitude(), location.getLongitude(),currentTime);
                    userBombSender = new UserBombSenderClient();
                    userBombSender.setUser(user);
                    userBombSender.execute();

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

                    Toast.makeText(getApplicationContext(), Constants.FOUNDED_GPS, Toast.LENGTH_SHORT).show();

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                            .zoom(15)                   // Sets the zoom
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                }
                else {

                    lati = location.getLatitude();
                    longi = location.getLongitude();
                    new RetrieveFeedTask().execute();
                    user.setLongi(longi);
                    user.setLati(lati);
                    user.setDatetime(currentTime);
                    try {
                        userBombSender.sendUser(user);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        ImageView iw = (ImageView) findViewById(R.id.imageView6);
        iw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setMessage(questtext)
                        .setTitle(Constants.QUEST_TEXT);

                builder.setPositiveButton(Constants.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        final Button putbomb = (Button) findViewById(R.id.button2);
        putbomb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (lati != 0) {
                    putbomb.setClickable(false);
                    putbomb.setEnabled(false);
                    new Thread(new ButtonDecrement(putbomb)).start();

                    Circle circle = mMap.addCircle(new CircleOptions()
                            .center(new LatLng(lati, longi))
                            .radius(1)
                            .fillColor(Color.BLACK));
                    bomb_circles.add(circle);

                    Circle circle2 = mMap.addCircle(new CircleOptions()
                            .center(new LatLng(lati, longi))
                            .radius(1)
                            .fillColor(0x22FF0000)
                            .strokeColor(Color.RED));

                    bomb_circles_animations.add(circle2);


                    IconGenerator iconFactory = new IconGenerator(getApplicationContext());
                    Marker me = mMap.addMarker(new MarkerOptions().position(new LatLng(lati, longi)));
                    me.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("60")));

                    bomb_markers.add(me);

                    Query query = db.mDatabase.child(Constants.ACTIVE_BOMB_TABLE);


                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            int id = 0;
                            if (dataSnapshot.getValue() != null) {
                                Map<String, Object> objectMap;
                                if (dataSnapshot.getValue() instanceof Map) {
                                    objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                                } else {
                                    ArrayList<Object> x = (ArrayList<Object>) dataSnapshot.getValue();
                                    objectMap = new HashMap<String, Object>();
                                    for (Object k : x) {
                                        if (k != null) {
                                            HashMap<String, Object> o = (HashMap<String, Object>) k;
                                            objectMap.put(o.get(Constants.ACTIVE_BOMB_TABLE_ID).toString(), o);
                                        }
                                    }

                                }

                                for (Object obj : objectMap.values()) {
                                    if (obj instanceof Map) {
                                        Map<String, Object> values = (Map<String, Object>) obj;
                                        String idstr = values.get(Constants.ACTIVE_BOMB_TABLE_ID).toString();
                                        int idint = Integer.parseInt(idstr);
                                        if (idint > id) {
                                            id = idint;
                                        }
                                    }
                                }
                                id += 1;

                                new RetrieveFeedTask().execute();

                                Bomb bomb = new Bomb( currentTime, name, 60, lati, longi);
                                bomb.setId(id);
                                try {
                                    userBombSender.sendBomb(bomb);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }else{

                                new RetrieveFeedTask().execute();//getactualtime

                                Bomb bomb = new Bomb( currentTime, name, 60, lati, longi);
                                bomb.setId(0);

                                try {
                                    userBombSender.sendBomb(bomb);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }


            }
        });

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,0 , list);


        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(Constants.WAITING_TIME);
                if(s.equals("0")){
                    putbomb.setText(Constants.PUT_BOMB_TEXT);
                    putbomb.setEnabled(true);
                }else{
                    putbomb.setText(Constants.PUT_BOMB_AVAILABLE + s);
                    putbomb.setEnabled(false);
                }
            }
        };

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menus, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection


        switch (item.getItemId()) {

            case android.R.id.home:
            case R.id.logout:
                TextView not = (TextView) findViewById(R.id.notfiy_text);

                if(not.getText().toString().equals(Constants.YOU_ARE_IN_DANGER)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(Constants.ALERT_MESSAGE)
                            .setTitle(Constants.ALERT);

                    builder.setPositiveButton(Constants.EXIT, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            LoginActivity.putPref(Constants.PREFERENCE_NAME, "", getApplicationContext());

                            TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                            String imei = telephonyManager.getDeviceId();

                            try {
                                userBombSender.sendLogout(new LoginUser(name,"",true,imei));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                userBombSender.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Intent myintent = new Intent(MapsActivity.this, LoginActivity.class);
                            startActivity(myintent);

                            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                    ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            }else{
                                manager.removeUpdates(list);
                            }

                            manager= null;
                            dangerChecker.interrupt();
                            finish();
                            onStop();
                        }
                    });
                    builder.setNegativeButton(Constants.CANCEL, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }else {

                    LoginActivity.putPref(Constants.PREFERENCE_NAME, "", this);

                    TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                    String imei = telephonyManager.getDeviceId();

                    new LoginSenderClient(new LoginUser(name,"",true,imei)).execute();

                    Intent myintent = new Intent(MapsActivity.this, LoginActivity.class);
                    startActivity(myintent);

                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }else{
                        manager.removeUpdates(list);
                    }

                    manager= null;
                    try{
                        dangerChecker.interrupt();
                    }catch (Exception e){};

                    finish();
                    super.onStop();
                    return true;

                }

            case R.id.menu_main_setting:
                dbref.child(Constants.REGISTRED_USERS_TABLE).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null){
                            ArrayList<String> names = new ArrayList<String>();
                            ArrayList<Integer> scores = new ArrayList<Integer>();
                            String end = "";
                            Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();

                            for (Object obj : objectMap.values()) {
                                if (obj instanceof Map) {
                                    Map<String, Object> values = (Map<String, Object>) obj;
                                    int score = (int) (long)  values.get(Constants.REGISTRED_USERS_TABLE_SCORE);
                                    String n = values.get(Constants.REGISTRED_USERS_TABLE_NICNAKME).toString();
                                    names.add(n);
                                    scores.add(score);
                                }
                            }

                            int c = 1;
                            while(c < 11){
                                if(names.size() > 0) {
                                    int maks = scores.get(0);
                                    int cord = 0;
                                    for (int i = 1; i < scores.size(); i++) {
                                        if (scores.get(i) > maks) {
                                            maks = scores.get(i);
                                            cord = i;
                                        }
                                    }
                                    end += c + ". " + names.get(cord) + " : " + scores.get(cord) + "\n";
                                    scores.remove(cord);
                                    names.remove(cord);
                                }
                                c++;
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                            builder.setMessage(end)
                                    .setTitle(Constants.TOPTEN);
                            builder.setPositiveButton(Constants.OK, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                            builder.show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {

            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        TextView not = (TextView) findViewById(R.id.notfiy_text);

        if(not.getText().toString().equals(Constants.YOU_ARE_IN_DANGER)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(Constants.ALERT_MESSAGE)
                    .setTitle(Constants.ALERT);

            builder.setPositiveButton(Constants.EXIT, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    } else {
                        manager.removeUpdates(list);
                    }

                    if (dangerChecker != null) dangerChecker.interrupt();

                    manager = null;

                    try {
                        if(userBombSender != null) {
                            userBombSender.sendUser(new User(name, lati, longi, 0));
                            userBombSender.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    user = null;


                    Intent setIntent = new Intent(Intent.ACTION_MAIN);
                    setIntent.addCategory(Intent.CATEGORY_HOME);
                    setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(setIntent);
                    finish();
                }
            });
            builder.setNegativeButton(Constants.CANCEL, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }else {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            } else {
                manager.removeUpdates(list);
            }

            if (dangerChecker != null) dangerChecker.interrupt();

            manager = null;

            try {
                if(userBombSender != null) {
                    userBombSender.sendUser(new User(name, lati, longi, 0));
                    userBombSender.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            user = null;


            Intent setIntent = new Intent(Intent.ACTION_MAIN);
            setIntent.addCategory(Intent.CATEGORY_HOME);
            setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(setIntent);
            finish();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

        manager= null;
        try {
            if(userBombSender != null) {
                userBombSender.sendUser(new User(name, lati, longi, 0));
                userBombSender.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        user = null;

        finish();
        super.onStop();
    }

    @Override
    protected void onPause() {

        paused = true;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wl.release();
    }

    @Override
    protected void onStart(){
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(Constants.PUT_BOMB_INTENT));
    }

    private void startCountAnimation(final TextView v, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                v.setText(Constants.SCORE + animation.getAnimatedValue().toString());
            }
        });
        animator.start();
    }

    private void startCircleAnimation(final Circle circle) {
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setIntValues(0, 100);
        valueAnimator.setDuration(1000);
        valueAnimator.setEvaluator(new IntEvaluator());
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                circle.setRadius(animatedFraction * Constants.EIGHTYMETERS);
            }
        });

        valueAnimator.start();
    }
}
