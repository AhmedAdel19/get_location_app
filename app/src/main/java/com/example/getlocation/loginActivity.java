package com.example.getlocation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class loginActivity extends AppCompatActivity
{
    private EditText usernameText , passwordText;
    static String username , password;
    private Button login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameText = findViewById(R.id.username_view);
        passwordText = findViewById(R.id.password_view);
        login_btn = findViewById(R.id.login_btn);


        login_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                username = usernameText.getText().toString();
                password = passwordText.getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(username) )
                {
                    Toast.makeText(loginActivity.this, "please fill the empty fields !", Toast.LENGTH_SHORT).show();
                }
//                else
//                {
//
//                    //downloadJSON("http://www.chapeautravel.com/contest2.php?username="+username+"&password="+password+"");
//                    MyLocationService.username_loc = username;
//                    MyLocationService.password_loc = password;
//
                    Intent go_map = new Intent(getApplicationContext() , getLocationActivity.class);
                    startActivity(go_map);
//
//                }



            }
        });
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

                        s = s + "info : " + json.getString("id") + " " + json.getString("username")+ " " + json.getString("password");
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
}
