package com.iam.root.khanabot;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class DeliveryNumberChange extends AppCompatActivity {

    private EditText number;
    private Button change;
    private String setNumber;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_number_change);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        number  = (EditText)findViewById(R.id.number);
        change = (Button)findViewById(R.id.change);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNumber = number.getText().toString();
                makerequest(setNumber);
            }
        });


    }

    public void makerequest(String setMessageNumber){
        RequestParams params = new RequestParams();
        params.put("callnumber",setMessageNumber);
        RestClient.get("/setcallnumber",params,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(response.has("status")){
                    try {
                        if(response.getString("status").equals("changed")){
                            Toast.makeText(context,"Delivery Number Updated",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFinish() {
                //onLoginSuccess();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject errorResponse){
                Log.e("error failure","connection failed in orderhistory");
                Toast.makeText(context,"Internet connection failed",Toast.LENGTH_SHORT).show();
            }

        });
    }

}
