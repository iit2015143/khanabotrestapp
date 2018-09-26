package com.iam.root.khanabot;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MessageNumberChange extends AppCompatActivity {

    private EditText number;
    private Button change;
    private String setNumber;
    private Context context;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_number_change);

            number  = (EditText)findViewById(R.id.number);
            change = (Button)findViewById(R.id.change);

            context = this;

            change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setNumber = number.getText().toString();
                   // Toast.makeText(context,setNumber,Toast.LENGTH_LONG).show();
                    makerequest(setNumber);
                }
            });
        }

    public void makerequest(String setMessageNumber){
        RequestParams params = new RequestParams();
        params.put("msgnumber",setMessageNumber);
        RestClient.get("/setmsgnumber",params,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(response.has("status")){
                    try {
                        if(response.getString("status").equals("changed")){
                            Toast.makeText(context,"Message Number Updated",Toast.LENGTH_LONG).show();
                            Log.e("status update","response");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFinish() {
                //onLoginSuccess();
                Log.e("finish","finished");

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject errorResponse){
                Log.e("error failure","connection failed in orderhistory");
                Toast.makeText(context,"Internet connection failed",Toast.LENGTH_SHORT).show();
            }

        });
    }
}
