package com.example.robo.atomicbomberman;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.robo.atomicbomberman.R.id.map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String name = "";
    boolean inserted = false;
    Database db = Database.getInstance();
    Constants constants = Constants.getInstance();
    User user;
    boolean paused = false;
    ArrayList<Marker> markers = new ArrayList<>();
    ArrayList<Marker> bomb_markers = new ArrayList<>();
    ArrayList<Circle> bomb_circles = new ArrayList<>();
    double lati;
    double longi;


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

        name = bundle.getString(constants.INTENT_NAME);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //GET _ ALL ACTIVE_USERS

        final DatabaseReference dbref = Database.getInstance().mDatabase;
        dbref.child(constants.ACTIVE_USERS_TABLE).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();

                    for (Marker me : markers) {
                        me.remove();
                    }
                    markers.clear();

                    for (Object obj : objectMap.values()) {
                        if (obj instanceof Map) {
                            Map<String, Object> values = (Map<String, Object>) obj;


                            IconGenerator iconFactory = new IconGenerator(getApplicationContext());
                            Marker me = mMap.addMarker(new MarkerOptions().position(new LatLng((double) values.get(constants.ACTIVE_USERS_TABLE_LATI), (double) values.get(constants.ACTIVE_USERS_TABLE_LONGI))).title(values.get(constants.ACTIVE_USERS_TABLE_NICKNAME).toString()).visible(true));
                            me.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon((String) values.get(constants.ACTIVE_USERS_TABLE_NICKNAME))));
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
        
        dbref.child(constants.ACTIVE_BOMB_TABLE).addValueEventListener(new ValueEventListener() {
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

                if (dataSnapshot.getValue() != null) {

                    Log.d("error",dataSnapshot.getValue().toString());
                    Map<String, Object> objectMap;
                    if(dataSnapshot.getValue() instanceof Map){
                        objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    }else{
                        ArrayList<Object> x = (ArrayList<Object>) dataSnapshot.getValue();
                        objectMap = new HashMap<String, Object>();
                        for (Object k : x){
                            if(k != null){
                                HashMap<String,Object> o = (HashMap<String, Object>) k;
                                objectMap.put(o.get("ID").toString(),o);
                            }
                        }

                    }


                    for (Object obj : objectMap.values()) {
                        if (obj instanceof Map) {
                            Map<String, Object> values = (Map<String, Object>) obj;

                            double lati = (double) values.get("LATITUDE");
                            double longi = (double) values.get("LONGITUDE");
                            String idstr = values.get("REMAINING_TIME").toString();

                            Circle circle = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(lati, longi))
                                    .radius(5)
                                    .strokeColor(Color.RED)
                                    .fillColor(Color.BLUE));

                            bomb_circles.add(circle);

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


        LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener list = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (user == null) {

                    Database.getInstance().insert_user(name, location.getLatitude(), location.getLongitude());
                    lati = location.getLatitude();
                    longi = location.getLongitude();
                    inserted = true;
                    user = new User(name, location.getLatitude(), location.getLongitude());

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

                    Toast.makeText(getApplicationContext(), constants.FOUNDED_GPS, Toast.LENGTH_SHORT).show();

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                            .zoom(22)                   // Sets the zoom
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                } else {
                    if (!paused) {
                        lati = location.getLatitude();
                        longi = location.getLongitude();
                        Database.getInstance().update_active_user(user, location.getLatitude(), location.getLongitude());
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


        Button putbomb = (Button) findViewById(R.id.button2);
        putbomb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (lati != 0) {
                    Circle circle = mMap.addCircle(new CircleOptions()
                            .center(new LatLng(lati, longi))
                            .radius(10)
                            .strokeColor(Color.RED)
                            .fillColor(Color.BLUE));

                    bomb_circles.add(circle);

                    IconGenerator iconFactory = new IconGenerator(getApplicationContext());
                    Marker me = mMap.addMarker(new MarkerOptions().position(new LatLng(lati, longi)));
                    me.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("60")));

                    bomb_markers.add(me);

                    Query query = db.mDatabase.child(constants.ACTIVE_BOMB_TABLE);


                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int id = 0;
                            if (dataSnapshot.getValue() != null) {
                                ArrayList<Object> bombs = new ArrayList<Object>();
                                try {
                                    bombs = (ArrayList<Object>) dataSnapshot.getValue();
                                } catch (Exception e) {
                                    bombs.add(dataSnapshot.getValue());
                                }

                                for (Object bomb : bombs) {
                                    if (bomb == null) {
                                        continue;
                                    }


                                    Map<String, Object> objectMap = (HashMap<String, Object>)
                                            bomb;

                                    if(bombs.size() == 1){
                                        for(String key : objectMap.keySet()){
                                            objectMap = (HashMap<String, Object>) objectMap.get(key);
                                        }
                                    }


                                    String idstr = objectMap.get("ID").toString();
                                    int idint = Integer.parseInt(idstr);
                                    if (idint > id) {
                                        id = idint;
                                    }
                                }

                                id += 1;
                            }

                            db.insert_bomb(Integer.toString(id), Calendar.getInstance().getTimeInMillis(), name, 60, lati, longi);
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

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, list);

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
            case R.id.logout:
                super.onStop();
                db.delete_user(name);

                LoginActivity.putPref(constants.PREFERENCE_NAME, "", this);

                Intent myintent = new Intent(MapsActivity.this, LoginActivity.class);
                startActivity(myintent);


                finish();

                return true;
            case R.id.menu_main_setting:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
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
        super.onStop();
        db.delete_user(name);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
        db.delete_user(name);
        onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.delete_user(name);
    }
}
