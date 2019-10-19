package com.example.getlocation;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.android.gms.location.LocationResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MyLocationService extends BroadcastReceiver
{
    public static final String ACTION_PROCESS_UPDATE = "com.example.getlocation.UPDATE_LOCATION";
    String username_loc , password_loc;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent != null)
        {
            final String action = intent.getAction();

            if(ACTION_PROCESS_UPDATE.equals(action))
            {
                LocationResult result = LocationResult.extractResult(intent);

                if(result != null)
                {
                    Location location = result.getLastLocation();
                    username_loc = loginActivity.username;
                    password_loc = loginActivity.password;
                    //StringBuilder location_string = new StringBuilder(location.getLatitude())
                    Double latitude = location.getLatitude();
                    Double longitude = location.getLongitude();

                    try
                    {
                        getLocationActivity.getInstance().getloaction(location);
//                        getLocationActivity.latitude_s = latitude;
//                        getLocationActivity.longitude_s = longitude;
                        //new MyAsyncTask().execute("http://www.chapeautravel.com/demo.php?latitude="+latitude+"&longitude="+longitude+"");

                        downloadJSON("http://www.chapeautravel.com/contest2.php?latitude="+latitude+"&longitude="+longitude+"&username="+username_loc+"&password="+password_loc+"");


                        Toast.makeText(context, "lat :"+latitude+" lng :"+longitude, Toast.LENGTH_SHORT).show();

                        //Toast.makeText(context, "username : " + username_loc + " password : "+password_loc, Toast.LENGTH_SHORT).show();

                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(context, "Error occuer : can not access this location", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        }

    }

    //-------------------------------------------------------------------------------------------------------------

    private void downloadJSON(final String urlWebService) {

        class DownloadJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
//parse json data

                try {

                    String s = "";

                    JSONArray jArray = new JSONArray(result);

                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject json = jArray.getJSONObject(i);

                        s = s + "info : " + json.getString("id") + " " + json.getString("latitude")+ " " + json.getString("longitude");
                    }


                } catch (Exception e) {

// TODO: handle exception

                    Log.e("log_tag", "Error Parsing Data " + e.toString());

                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        DownloadJSON getJSON = new DownloadJSON();
        getJSON.execute();
    }



    //-------------------------------------------------------------------------------------------------------------

//
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
}
