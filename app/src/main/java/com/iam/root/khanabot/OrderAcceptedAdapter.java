package com.iam.root.khanabot;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class OrderAcceptedAdapter extends RecyclerView.Adapter<OrderAcceptedAdapter.MyViewHolder>{

    public Context context;
    public JSONArray orderhistory;

    public OrderAcceptedAdapter(Context context, JSONArray hot){
        this.context = context;
        orderhistory = hot;
    }

    public void setData(JSONArray hot){
        orderhistory = hot;
    }

    @NonNull
    @Override
    public OrderAcceptedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.currentorder,parent,false);
        return new OrderAcceptedAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        try {
            holder.accept.setEnabled(true);
            holder.decline.setEnabled(true);
            final JSONObject perorder = orderhistory.getJSONObject(position);

            long time = perorder.getLong("id");
            time = time/10000;
            Date date = new Date(time);
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
            String dateFormatted = formatter.format(date);
            holder.time.setText(dateFormatted);

            //holder.status.setText(perorder.getString("status"));
            if(perorder.getString("mode").equals("book")){
                holder.accept.setText("Ready");
            }
            else
                holder.accept.setText("Outfordelivery");
            holder.accept.setEnabled(true);
            holder.decline.setEnabled(true);
            holder.number.setText(perorder.getString("fromnumber"));
            holder.summary.setText(perorder.getString("summary"));
            holder.orderid.setText(perorder.getString("id"));
            holder.total.setText(perorder.getString("total"));
            if(perorder.has("address")){
                holder.address.setText(perorder.getString("address"));
            }
            if(perorder.has("revisedTotal")){
                holder.total.setPaintFlags(holder.total.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.revisedTotal.setText(perorder.getString("revisedTotal"));
            }
            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.accept.setEnabled(false);
                    try {
                        String id = perorder.getString("id");
                        String status = "Outfordelivery";
                        if(perorder.getString("mode").equals("book")){
                            status = "Ready";
                        }
                        String number = perorder.getString("fromnumber");
                        ((RestaurantOrderPage)context).sendrequest(id,status,number);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.decline.setEnabled(false);
                    try {
                        String id = perorder.getString("id");
                        String status = "Declined";
                        String number = perorder.getString("fromnumber");
                        ((RestaurantOrderPage)context).sendrequest(id,status,number);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return orderhistory.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView orderid,summary,total,status,number,time,address,revisedTotal;
        public View view;
        Button accept,decline;
        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            address = (TextView)view.findViewById(R.id.address);
            revisedTotal = (TextView)view.findViewById(R.id.revisedTotal);
            time = (TextView)(view.findViewById(R.id.time));
            orderid = (TextView)(view.findViewById(R.id.orderid));
            summary=(TextView)(view.findViewById(R.id.summary));
            total=(TextView)(view.findViewById(R.id.total));
            //status = (TextView)(view.findViewById(R.id.status));
            number =(TextView)(view.findViewById(R.id.number));
            accept= (Button)(view.findViewById(R.id.btnone));
            decline = (Button)(view.findViewById(R.id.btntwo));
        }

    }
}

