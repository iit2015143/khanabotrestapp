package com.iam.root.khanabot;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
    }

    public void checkuuidandnumber(){
        String number = getprefsvalue("number");
        String uuid = getprefsvalue("uuid");
        if(number.equals("") || uuid.equals("")){
            Intent intent = new Intent(context,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else
            dologin(number,uuid);
    }

    public void dologin(String number, String uuid){

        RequestParams params = new RequestParams();
        params.put("number",number);
        params.put("uuid",uuid);

        RestClient.setCookieStore(new PersistentCookieStore(getApplicationContext()));
        RestClient.post("/loginrest", params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
                try {
                    if(response.has("loggedin")){
                        if(response.getBoolean("loggedin")){
                            checkLocation();
//                            Intent intent = new Intent(context,HomePage.class);
//                            startActivity(intent);
//                            finish();
                        }
                        else{
                            Toast.makeText(context,"This account is logged in on other device, log in again",Toast.LENGTH_LONG).show();
                            ((MainActivity)context).addtosharedpreferences("notificationstatus","notupdated");
                            Intent intent = new Intent(context,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                //onLoginSuccess();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject errorResponse){
                Toast.makeText(context,"Internet connection failed",Toast.LENGTH_SHORT).show();
            }

        });

    }

    public void checkLocation(){
        if(getprefsvalue("location").equals("")){
            Intent intent = new Intent(this,AllowPermission.class);
            startActivity(intent);
            finish();
        }
        else{
            String address = getprefsvalue("gLocation");
            HomePage.address = address;
            Intent intent = new Intent(this,HomePage.class);
            startActivity(intent);
            finish();
        }
    }

    public void show(String showme){
        Toast.makeText(getBaseContext(), showme, Toast.LENGTH_LONG).show();
    }

    public String getprefsvalue(String key){
        return getSharedPreferences("com.example.root.khanabot",Context.MODE_PRIVATE).getString(key,"");
    }



    public void addtosharedpreferences(String key,String value){
        SharedPreferences prefs = getSharedPreferences("com.example.root.khanabot",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }

    @Override
    public void onResume(){
        super.onResume();
        checkuuidandnumber();
    }
}
