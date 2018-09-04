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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import cz.msebera.android.httpclient.Header;

public class AllowPermission extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks ,
GoogleApiClient.OnConnectionFailedListener{

    private Location mCurrentLocation;
    private GoogleApiClient mGoogleApiClient;
    private Context context;
    public LocationRequest locationRequest;
    public static final int REQUEST_CHECK_SETTINGS = 0x1;
    public LocationCallback mLocationCallback;
    public int flag = 0;


    final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allow_permission);
        context=this;
        if(checkpermission()){
            permissionGranted();
        }
    }

    public void allowPermission(View view){
        if(checkpermission()) {
            permissionGranted();
        }
    }

    public boolean checkpermission(){
        boolean result = false;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_ACCESS_FINE_LOCATION);
        }
        else
            result = true;
        return result;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted();
                }
                else {
                    Toast.makeText(this, "We Need your location to serve you better!", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }
    public void permissionGranted(){
        checklocationservices();
    }

    public void checklocationservices(){
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(context).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                Log.e("error","task completed");
                try {
                    LocationSettingsResponse response =
                            task.getResult(ApiException.class);
                    connectgoogleclient();
                } catch (ApiException ex) {
                    switch (ex.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                Log.e("error","resolution required");
                                ResolvableApiException resolvableApiException =
                                        (ResolvableApiException) ex;
                                resolvableApiException
                                        .startResolutionForResult(AllowPermission.this,
                                                REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            break;
                    }
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e("permissionresult", "User agreed to make required location settings changes.");
                        connectgoogleclient();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context,"We need location service to serve you better.",Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                }
                break;
        }
    }

    public void gotocurrentlocation()throws JSONException {
        mCurrentLocation = LocationServices
                .FusedLocationApi
                .getLastLocation( mGoogleApiClient );

        //Log.e("Errorcurrentlocation","ingotocurrentlocation");
        if(mCurrentLocation!=null) {
            //Log.e("Errorcurrentlocation", mCurrentLocation.toString());
            JSONObject location = new JSONObject();
            location.put("lat", mCurrentLocation.getLatitude() + "");
            location.put("long", mCurrentLocation.getLongitude() + "");
            addtosharedpref("location", location.toString());

            if(flag==1)
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,mLocationCallback);
            String address = getAddressFromLatLng(new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()));
            addtosharedpref("gLocation",address);
            HomePage.address = address;
            updatelocation(location.toString());
        }
        else{
//            Intent intent = new Intent(this,OnLocation.class);
//            startActivity(intent);
//            finish();

            mLocationCallback=new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult result) {
                    Log.e("location",result.getLastLocation().toString());
                    try {
                        gotocurrentlocation();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) { }
            };

            flag=1;

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, mLocationCallback, null);

        }

    }

    public void updatelocation(String location){

        RequestParams params = new RequestParams();
        params.put("location",location);
        RestClient.post("/locationrest", params, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
                try {
                    if(response.has("location")){
                        if(response.getString("location").equals("updated")){
                            Intent intent = new Intent(context,HomePage.class);
                            intent.putExtra("address","");
                            startActivity(intent);
                            finish();
                        }
                        else{

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

    public void connectgoogleclient(){
        mGoogleApiClient=new GoogleApiClient.Builder(this, this,this).addApi(LocationServices.API).build();
        if(mGoogleApiClient!=null){
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            gotocurrentlocation();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.e("Error connected","connected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private String getAddressFromLatLng( LatLng latLng ) {
        Geocoder geocoder = new Geocoder( context );

        String address = "";
        try {
            address = geocoder
                    .getFromLocation( latLng.latitude, latLng.longitude, 1 )
                    .get( 0 ).getAddressLine( 0 );
        } catch (IOException e ) {
        }

        return address;
    }
    public void addtosharedpref(String key,String value){
        SharedPreferences prefs = getSharedPreferences("com.example.root.khanabot",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key,value);
        editor.commit();
    }
}
