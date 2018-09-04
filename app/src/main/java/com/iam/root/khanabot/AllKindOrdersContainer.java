package com.iam.root.khanabot;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AllKindOrdersContainer extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public RecyclerView recyclerView;
    public  Context context;
    public String decider;
    public SwipeRefreshLayout swiper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = (View) inflater.inflate(
                R.layout.fragment_pending_orders, container, false);
        context= getActivity();
        Log.e("id",this.getId()+"");
        decider = getArguments().getString("decider");
        swiper = (SwipeRefreshLayout)(rootView.findViewById(R.id.swipeme));
        swiper.setOnRefreshListener(this);
        recyclerView = (RecyclerView)(rootView.findViewById(R.id.mymenu));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));

        switch (decider){
            case "Pending":
                recyclerView.setAdapter(new OrderPendingAdapter(context,RestaurantOrderPage.pendingorders));
                break;
            case "History":
                recyclerView.setAdapter(new OrderOutAdapter(context,RestaurantOrderPage.outfordelorders));
                break;
            case "Current":
                recyclerView.setAdapter(new OrderAcceptedAdapter(context,RestaurantOrderPage.acceptedorders));
        }

        return rootView;
    }

    public void notifychange(){
        Log.e("decider",decider+"");
        switch (decider){
            case "Pending":
                ((OrderPendingAdapter)recyclerView.getAdapter()).setData(RestaurantOrderPage.pendingorders);
                ((OrderPendingAdapter)recyclerView.getAdapter()).notifyDataSetChanged();
                break;
            case "History":
                ((OrderOutAdapter)recyclerView.getAdapter()).setData(RestaurantOrderPage.outfordelorders);
                ((OrderOutAdapter)recyclerView.getAdapter()).notifyDataSetChanged();
                break;
            case "Current":
                ((OrderAcceptedAdapter)recyclerView.getAdapter()).setData(RestaurantOrderPage.acceptedorders);
                ((OrderAcceptedAdapter)recyclerView.getAdapter()).notifyDataSetChanged();
        }    }

    @Override
    public void onRefresh() {
        Log.e("onrefresh","i am fired");
        RequestParams params = new RequestParams();
        params.put("number","8574418045");
        RestClient.get("/orderhistoryrest",params,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //Toast.makeText(context,response.toString(),Toast.LENGTH_SHORT).show();
                RestaurantOrderPage.restaurantorders=response;
                ((RestaurantOrderPage)context).notifychange();
                Log.e("refreshed","closing swipe refresher");
                swiper.setRefreshing(false);
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
