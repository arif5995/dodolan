package com.arif.gedor.Maps;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arif.gedor.Adapter.ImagesActivity;
import com.arif.gedor.Common.Common;
import com.arif.gedor.DB.UserPbl;
import com.arif.gedor.DB.UserPdg;
import com.arif.gedor.InfoPdgActivity;
import com.arif.gedor.MenuActivity;
import com.arif.gedor.R;
import com.arif.gedor.TambahFotoActivity;
import com.arif.gedor.menu_PedagangActivity;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.github.glomadrian.materialanimatedswitch.MaterialAnimatedSwitch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapPembeliActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    //play service
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_RES_REQUEST = 7001;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL = 5000;
    private static int FATEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;

    //firebase
    FirebaseAuth auth, authPdg;
    DatabaseReference userPembeli, pedagangAvailable, infoPembeli;
    FirebaseDatabase firebaseDatabase;
    GeoFire geoFire;

    Marker mCurrentPbl, mCurrentPdg;
    private Marker pdgMarker;

    MaterialAnimatedSwitch location_switch;
    SupportMapFragment mapFragment;

    //material layout
    Button btnOnline, btnLogOut;
    TextView txtNama, txtTelpon;


    boolean isPedagangFound = false;
    String pedagangId = "";
    int radius = 1; //1km
    int distance = 1; //3km
    private static final int LIMIT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_pembeli);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        setupLocation();

        btnOnline = (Button) findViewById(R.id.btnOnline);
        txtNama = (TextView) findViewById(R.id.txtNama);
        txtTelpon = (TextView) findViewById(R.id.txtTelpon);
        btnLogOut = findViewById(R.id.btnLogOut);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MapPembeliActivity.this)
                        .setMessage("Apa Anda Ingin Keluar ?")
                        .setCancelable(false)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(MapPembeliActivity.this, MenuActivity.class));
                                auth.signOut();
                                finish();
                            }
                        })
                        .setNegativeButton("Tidak", null)
                        .show();
            }
        });



        //Button Online
        btnOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestpickHire(FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        });
        //CALL DATABASE USER PEMBELI
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(Common.UserPbl_tbl)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Profil");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserPbl userPbl = dataSnapshot.getValue(UserPbl.class);
                txtNama.setText(userPbl.getFullname());
                txtTelpon.setText(userPbl.getTelepon());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(MapPembeliActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        DatabaseReference dbRequestPbl = FirebaseDatabase.getInstance().getReference(Common.LokasiPbl_tbl);
        new AlertDialog.Builder(this)
                .setMessage("Apa Anda Ingin Keluar ?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(MapPembeliActivity.this, MenuActivity.class));
                        auth.signOut();
                        finish();
                    }
                })
                .setNegativeButton("Tidak", null)
                .show();
        dbRequestPbl.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dbLokasiPbl : dataSnapshot.getChildren()) {
                    dbLokasiPbl.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void requestpickHire(String uid) {
        DatabaseReference dbRequest = FirebaseDatabase.getInstance().getReference(Common.LokasiPbl_tbl);
        GeoFire mGeofire = new GeoFire(dbRequest);
        mGeofire.setLocation(uid, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

        if (mCurrentPbl.isVisible())
            mCurrentPbl.remove();
        //add new marker
        mCurrentPbl = mMap.addMarker(new MarkerOptions()
                .title("Kamu Disini")
                .snippet("")
                .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));
        mCurrentPbl.showInfoWindow();

        btnOnline.setText("Mencari Dodolan");

        findPedagang();
    }

    private void findPedagang() {
        final DatabaseReference pedagang = FirebaseDatabase.getInstance().getReference(Common.lokasiPdg_tbl);
        GeoFire gfPedagang = new GeoFire(pedagang);

        GeoQuery geoQuery = gfPedagang.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //if found pedagang
                if (isPedagangFound)
                {
                    isPedagangFound = true;
                    pedagangId = key;
                    btnOnline.setText("Memanggil Pedagang");
                    Toast.makeText(MapPembeliActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                //IF NOT FOUND PEDAGANG
                if (isPedagangFound) {
                    radius++;
                    findPedagang();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void setupLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //request permission
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            pedagangAvailable = firebaseDatabase.getReference(Common.lokasiPdg_tbl);
            pedagangAvailable.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    loadAvailablePedagang();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();

            if (mCurrentPbl != null)
                mCurrentPbl.remove();//remover already marker
            mCurrentPbl = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title("YOU")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mCurrentPbl.showInfoWindow();
            //MOVE CAMERA POSITION
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));
            //draw animation rotate
            loadAvailablePedagang();
            pickPdgMarker();
            Log.d("ERROR", "CANNOT GET LOCATION");
        } else {

        }
    }

    private void pickPdgMarker() {
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                final DatabaseReference pedagang = FirebaseDatabase.getInstance().getReference(Common.lokasiPdg_tbl);
                GeoFire gfPdg = new GeoFire(pedagang);

                GeoQuery geoQuery = gfPdg.queryAtLocation(new GeoLocation(mLastLocation.getLatitude()
                        , mLastLocation.getLongitude()), distance);
                geoQuery.removeAllListeners();
                geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                    @Override
                    public void onKeyEntered(final String key, GeoLocation location) {
                        FirebaseDatabase.getInstance().getReference(Common.UserPdg_tbl)
                                .child(key).child("Profil").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UserPdg userPdg = dataSnapshot.getValue(UserPdg.class);
                                String pRODUK = userPdg.getProduk();
                                String eMAIL = userPdg.getEmail();
                                String pAssword = userPdg.getPassword();

                                if(mCurrentPdg.getTitle().equals(pRODUK)){
                                    authPdg = FirebaseAuth.getInstance();
                                    authPdg.signInWithEmailAndPassword(eMAIL,pAssword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            Intent intent = new Intent(MapPembeliActivity.this,InfoPdgActivity.class);
                                            startActivity(intent);
                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onKeyExited(String key) {

                    }

                    @Override
                    public void onKeyMoved(String key, GeoLocation location) {

                    }

                    @Override
                    public void onGeoQueryReady() {

                    }

                    @Override
                    public void onGeoQueryError(DatabaseError error) {

                    }
                });


            }
        });

    }

    private void loadAvailablePedagang() {
        mMap.clear();

        mMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()))
                    .title(""));
        final DatabaseReference pedagang = FirebaseDatabase.getInstance().getReference(Common.lokasiPdg_tbl);
        GeoFire gfPdg = new GeoFire(pedagang);

        GeoQuery geoQuery = gfPdg.queryAtLocation(new GeoLocation(mLastLocation.getLatitude()
                , mLastLocation.getLongitude()), distance);
        geoQuery.removeAllListeners();


        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {
                FirebaseDatabase.getInstance().getReference(Common.UserPdg_tbl)
                        .child(key).child("Profil")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                UserPdg userPdg = dataSnapshot.getValue(UserPdg.class);
                                String pRODUK = userPdg.getProduk();
                                String tELEPON = userPdg.getTelepon();

                                    mCurrentPdg = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(location.latitude, location.longitude))
                                            .flat(true)
                                            .title(pRODUK)
                                            .snippet(tELEPON)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pdg)));

                                    Log.i("pRODUK",userPdg.getProduk());


                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (distance <= LIMIT)//JUST FIND 3KM
                {
                    distance++;
                    loadAvailablePedagang();
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }


    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICE_RES_REQUEST).show();
            else {
                Toast.makeText(this, "device is not support", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdate();
    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        displayLocation();

    }

}
