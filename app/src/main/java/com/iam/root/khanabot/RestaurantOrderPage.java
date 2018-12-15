package com.iam.root.khanabot;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class RestaurantOrderPage extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    public Handler refresh;
    public static JSONArray restaurantorders;
    public static JSONArray pendingorders;
    public static JSONArray acceptedorders;
    public static JSONArray outfordelorders;
    public Context context;
    public OrderOutAdapter orderhistoryAdapter;
    public AllKindOrdersContainer Pending ,History,Current;
    public  Runnable runnable;
    private CoordinatorLayout coordinatorLayout;
    Boolean visible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_order_page);
        context=this;
        restaurantorders=new JSONArray();
        pendingorders = new JSONArray();
        acceptedorders = new JSONArray();
        outfordelorders = new JSONArray();
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(4);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        makerequest();
        refresh = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                refresh.postDelayed(runnable,10000);
                makerequest();
            }
        };
        runnable.run();
    }

    public void makerequest(){
        final Snackbar snackbar = Snackbar.make(coordinatorLayout,"Internet Connection Failed",Snackbar.LENGTH_INDEFINITE);
        RequestParams params = new RequestParams();
        params.put("number","8574418045");
        RestClient.get("/orderhistoryrest",params,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
                restaurantorders=response;
                notifychange();
                Log.e("response",restaurantorders.toString());
                if(visible) {
                    snackbar.setText("Connection Restored");
                    snackbar.setDuration(Snackbar.LENGTH_LONG);
                    snackbar.show();
                    visible = false;
                }
               // Toast.makeText(context,"Updated",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                //onLoginSuccess();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,JSONObject errorResponse){
                Log.e("error failure","connection failed in orderhistory");
               // Toast.makeText(context,"Internet connection failed",Toast.LENGTH_SHORT).show();
                //showSnackBar();
                if(snackbar.isShown() == false) {
                    snackbar.setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            makerequest();
                        }
                    });
                    snackbar.show();
                    visible = true;
                }
            }

        });
    }

    public void showSnackBar(){


    }

    private void setupViewPager(ViewPager viewPager) {
        RestaurantOrderPage.ViewPagerAdapter adapter = new RestaurantOrderPage.ViewPagerAdapter(getSupportFragmentManager());


        History = new AllKindOrdersContainer();
        Bundle bundle = new Bundle();
        bundle.putString("decider","History");
        History.setArguments(bundle);

        Pending = new AllKindOrdersContainer();
        bundle = new Bundle();
        bundle.putString("decider","Pending");
        Pending.setArguments(bundle);

        Current = new AllKindOrdersContainer();
        bundle = new Bundle();
        bundle.putString("decider","Current");
        Current.setArguments(bundle);

        adapter.addFragment(Pending, "Pending");
        adapter.addFragment(Current, "Current");
        adapter.addFragment(History,"History");

        viewPager.setAdapter(adapter);
    }

    public void sendrequest(final String orderid, final String status, String fromnumber){
        RequestParams params = new RequestParams();
        params.put("id",orderid);
        params.put("status",status);
        params.put("fromnumber",fromnumber);

        RestClient.get("/changeorderstatusrest",params,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
                if(response.has("status")){
                    try {
                        if(response.getString("status").equals("changed")){
                            for(int i=0; i<restaurantorders.length();i++){
                                if(restaurantorders.getJSONObject(i).getString("id").equals(orderid)) {
                                    restaurantorders.getJSONObject(i).put("status", status);
                                    notifychange();
                                    break;
                                }
                            }
                        }
                        else{
                            Toast.makeText(context,response.getString("status"),Toast.LENGTH_LONG).show();
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
            }

        });
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        refresh.removeCallbacks(runnable);
    }

    public void notifychange(){
        pendingorders = new JSONArray();
        acceptedorders = new JSONArray();
        outfordelorders = new JSONArray();
        for(int i=0;i<restaurantorders.length();i++){
            try {
                JSONObject order = restaurantorders.getJSONObject(i);

                if(order.getString("status").equals("Pending")){
                    pendingorders.put(order);
                }
                else if(order.getString("status").equals("Accepted")){
                    acceptedorders.put(order);
                }
                else if(order.getString("status").equals("Outfordelivery")||
                        order.getString("status").equals("Declined")||
                        order.getString("status").equals("Ready")){
                    outfordelorders.put(order);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e("changes adapter","done");
        Pending.notifychange();
        Current.notifychange();
        History.notifychange();

    }

    @Override
    public void onResume(){
        super.onResume();
        makerequest();
        runnable.run();
    }
}