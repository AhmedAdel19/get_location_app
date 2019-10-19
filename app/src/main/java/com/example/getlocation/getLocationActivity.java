package com.example.getlocation;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.getlocation.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static android.location.LocationManager.*;

public class getLocationActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    static getLocationActivity instance;

    LocationRequest locationRequest;

    FusedLocationProviderClient fusedLocationProviderClient;

    public static getLocationActivity getInstance() {
        return instance;
    }



    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private Marker marker;
    private static final int Request_User_Location_Code = 101;
    private double latitude, longitude;
    public double latitude_s, longitude_s;

    private Button close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);

        instance = this;

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        UpdateLocation();

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getLocationActivity.this, "You must accept this permission!", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();

        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

       // fetchLastLocation();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(getLocationActivity.this);

        StrictMode.enableDefaults(); //STRICT MODE ENABLED


    }


    private void UpdateLocation() {

        buildLocationRequest();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());

    }

    private void buildLocationRequest()
    {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);
    }

    public PendingIntent getPendingIntent()
    {
        Intent intent = new Intent(this , MyLocationService.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(this , 0 ,intent ,PendingIntent.FLAG_UPDATE_CURRENT);
    }

////


    @Override
    public void onMapReady(GoogleMap googleMap) {
       /* mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            // TODO: Consider calling
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);

//            new MyAsyncTask().execute("http://www.chapeautravel.com/demo.php?latitude="+latitude+"&longitude="+longitude);

        }

//        Toast.makeText(getLocationActivity.this,latitude+" && "+
//                longitude, Toast.LENGTH_LONG).show();
        if(marker !=null)
        {
            marker.remove();
        }
        LatLng latLng = new LatLng(latitude_s,longitude_s);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("You Are Here");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        marker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(14));
    }

//    @Override
//    protected void onResume()
//    {
//        super.onResume();
//    }

    public boolean checkUserLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))

            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            return false;
        }
        else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case Request_User_Location_Code:
                if (grantResults.length >0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
                    {
                        if (googleApiClient==null){
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else {
                    Toast.makeText(this,"Permission Denied...", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }
    @Override
    public void onLocationChanged(Location location)
    {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastLocation = location;

        Toast.makeText(getLocationActivity.this,latitude+" && "+
                longitude, Toast.LENGTH_LONG).show();
        if(marker !=null)
        {
            marker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("You Are Here");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        marker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(14));

        if(googleApiClient != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
//            new MyAsyncTask().execute("http://www.chapeautravel.com/demo.php?latitude="+latitude+"&longitude="+longitude+"");

        }
//        new MyAsyncTask().execute("http://www.chapeautravel.com/demo.php?latitude="+latitude+"&longitude="+longitude+"");

    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            // TODO: Consider calling
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,this);
        }

        // gotoSetupActivity();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

//    String result = "";
//
//    class MyAsyncTask extends AsyncTask<String, Integer, String>
//    {
//
//        @Override
//        protected String doInBackground(String... params)
//        {
//            InputStream isr = null;
//
//            try {
//                String URL = params[0];
//                java.net.URL url = new URL(URL);
//                URLConnection urlConnection = url.openConnection();
//                isr = new BufferedInputStream(urlConnection.getInputStream());
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            try {
//
//                BufferedReader reader = new BufferedReader(new InputStreamReader(isr, "iso-8859-1"), 8);
//
//                StringBuilder sb = new StringBuilder();
//
//                String line = null;
//
//                while ((line = reader.readLine()) != null) {
//
//                    sb.append(line + "\n");
//
//                }
//
//                isr.close();
//
//                result = sb.toString();
//
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//
//                Log.e("log_tag", "Error  converting result " + e.toString());
//
//            }
//
//            return result;
//
//        }
//
//        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//        protected void onPostExecute(Double result) {
//
////parse json data
//
//            try {
//
//                String s = "";
//
//                JSONArray jArray = new JSONArray(result);
//
//                for (int i = 0; i < jArray.length(); i++) {
//
//                    JSONObject json = jArray.getJSONObject(i);
//
//                    s = s + "info : " + json.getString("id") + " " + json.getString("latitude")+ " " + json.getString("longitude");
//                }
//
//
//            } catch (Exception e) {
//
//// TODO: handle exception
//
//                Log.e("log_tag", "Error Parsing Data " + e.toString());
//
//            }
//        }
//    }

//    //-------------------------------------------------------
//    public class FindLocation {
//        private LocationManager locManager;
//        private LocationListener locListener;
//        private Location mobileLocation;
//        private String provider;
//
//        public FindLocation(Context ctx){
//            locManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
//            locListener = new LocationListener() {
//                @Override
//                public void onStatusChanged(String provider, int status,
//                                            Bundle extras) {
//                }
//                @Override
//                public void onProviderEnabled(String provider) {
//                }
//                @Override
//                public void onProviderDisabled(String provider) {
//                }
//                @Override
//                public void onLocationChanged(Location location) {
//                    System.out.println("mobile location is in listener="+location);
//                    mobileLocation = location;
//                }
//            };
//            locManager.requestLocationUpdates(GPS_PROVIDER, 1000, 1, locListener);
//            if (mobileLocation != null) {
//                locManager.removeUpdates(locListener);
//                String londitude = "Londitude: " + mobileLocation.getLongitude();
//                String latitude = "Latitude: " + mobileLocation.getLatitude();
//                String altitiude = "Altitiude: " + mobileLocation.getAltitude();
//                String accuracy = "Accuracy: " + mobileLocation.getAccuracy();
//                String time = "Time: " + mobileLocation.getTime();
//                Toast.makeText(ctx, "Latitude is = "+latitude +"Longitude is ="+londitude, Toast.LENGTH_LONG).show();
//            } else {
//                System.out.println("in find location 4");
//                Toast.makeText(ctx, "Sorry location is not determined", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//    //-------------------------------------------------------

    public void getloaction(final Location l)
    {
        getLocationActivity.this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                latitude_s = l.getLatitude();
                longitude_s = l.getLongitude();

            }
        });
    }
}
