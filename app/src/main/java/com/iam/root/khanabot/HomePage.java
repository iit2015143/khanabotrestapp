package com.iam.root.khanabot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class HomePage extends AppCompatActivity {

    public BottomNavigationView bnv;
    public Context context;
    public ViewPager viewPager;
    public RecyclerView hotdeal;
    public RecyclerView toprated;
    public RecyclerView category;
    public RecyclerView restaurants;
    public JSONArray responseArray=new JSONArray();
    public static JSONArray mycart;
    public static String address;
    public Switch switchme;
    public TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        context = this;

        String location = getprefsvalue("location");
        if(location.equals("")){
            Log.e("Fatal error","location not found");
        }
        ((TextView)findViewById(R.id.location)).setText(address);

        status = (TextView)findViewById(R.id.status);

        switchme = (Switch)findViewById(R.id.switchme);
        switchme.setEnabled(false);
        switchme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchme.setEnabled(false);
                Log.e("checked",isChecked+"");
                if(isChecked)
                    changestatus("on");
                else
                    changestatus("off");
            }
        });
        initializestatus();
        checknotificationstatus();
    }

    public void initializestatus(){
        RestClient.get("/getstatus",null,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
                try {
                    if(response.has("status")) {
                        String value = response.getString("status");
                        changebuttonui(value);
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
                Log.e("error failure","connection failed");
            }

        });
    }

    public void changebuttonui(String value){
        if(value.equals("on")){
            switchme.setChecked(true);
            status.setText("Open");
        }
        else if(value.equals("off")){
            switchme.setChecked(false);
            status.setText("Closed");
        }
        switchme.setEnabled(true);
    }

    public void changestatus(String status){
        RequestParams params = new RequestParams();
        params.put("status",status);
        RestClient.get("/setstatus",params,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
                try {
                    if(response.has("status")) {
                        String value = response.getString("status");
                        changebuttonui(value);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFinish() {
                //

                //onLoginSuccess();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject errorResponse){
                Log.e("error failure","connection failed");
            }

        });
    }

    public void locationBarClicked(View view){
        Intent intent = new Intent(this,MapFragment.class);
        startActivity(intent);
    }

    public void addtosharedpreferences(String key,String cartstring){
        SharedPreferences prefs = getSharedPreferences("com.example.root.khanabot",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,cartstring);
        editor.commit();
        Log.e("preferences",key+cartstring);
    }

    public void checknotificationstatus(){
        if(!notifstatusset()){
            addtoserver(getSharedPreferences("com.example.root.khanabot",Context.MODE_PRIVATE).
                    getString("notificationid",""));
        }
    }

    public boolean notifstatusset(){
        SharedPreferences prefs = getSharedPreferences("com.example.root.khanabot",Context.MODE_PRIVATE);
        String uuid = prefs.getString("notificationstatus","");
        if(uuid.equals("updated"))
            return true;
        return false;
    }
    public void addtoserver(String token){
        RequestParams params = new RequestParams();
        params.put("notificationid",token);
        RestClient.get("/savenotificationidrest",params,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
                try {
                    if(response.has("notificationid")) {
                        String value = response.getString("notificationid");
                        if (value.equals("updated")) {
                            ((HomePage)context).addtosharedpreferences("notificationstatus","updated");
                            Log.e("Sent ","notificationid sent to server");
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
                Log.e("error failure","connection failed in notificationid updation");
            }

        });
    }

    public void gotoorderpage(View view){
        Intent intent = new Intent(context,RestaurantOrderPage.class);
        startActivity(intent);
    }

    public String getprefsvalue(String key){
        return getSharedPreferences("com.example.root.khanabot",Context.MODE_PRIVATE).getString(key,"");
    }

    public void changeNumber(View view) {
        Intent numberChange  = new Intent(this,NumberChange.class);
        startActivity(numberChange);
    }
}
