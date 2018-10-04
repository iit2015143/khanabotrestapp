package com.iam.root.khanabot;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class OrderOutAdapter extends RecyclerView.Adapter<OrderOutAdapter.MyViewHolder>{

    public Context context;
    public JSONArray orderhistory;

    public OrderOutAdapter(Context context, JSONArray hot){
        this.context = context;
        orderhistory = hot;
    }

    public void setData(JSONArray hot){
        orderhistory = hot;
    }

    @NonNull
    @Override
    public OrderOutAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderhistoryitem,parent,false);
        return new OrderOutAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        try {
            holder.numcontainer.setVisibility(View.VISIBLE);
            final JSONObject perorder = orderhistory.getJSONObject(position);
            long time = perorder.getLong("id");
            time = time/10000;
            Date date = new Date(time);
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
            String dateFormatted = formatter.format(date);
            DateFormat curdate = new SimpleDateFormat("dd-MM-yy");
            String curdatestr = curdate.format(date);
            dateFormatted += ("\n" + curdatestr);

            holder.time.setText(dateFormatted);

            holder.status.setText(perorder.getString("status"));
            if(perorder.getString("status").equals("Declined"))
                holder.numcontainer.setVisibility(View.GONE);
            else
                holder.number.setText(perorder.getString("fromnumber"));
            holder.summary.setText(perorder.getString("summary"));
            holder.orderid.setText(perorder.getString("id"));
            holder.total.setText(perorder.getString("total"));
            holder.mode.setText(perorder.getString("mode"));
            holder.address.setText(perorder.getString("address"));
            Log.e("revised Total",perorder.getString("revisedTotal"));
            if(perorder.has("revisedTotal")){
                holder.total.setPaintFlags(holder.total.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.revisedTotal.setText(perorder.getString("revisedTotal"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("error in json",e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return orderhistory.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView orderid,summary,total,status,number,mode,address,time,revisedTotal;
        public LinearLayout numcontainer;
        public View view;
        Button accept,decline;
        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            time = (TextView)(view.findViewById(R.id.time));
            revisedTotal = (TextView)view.findViewById(R.id.revisedTotal);
            mode =(TextView)(view.findViewById(R.id.mode));
            address=(TextView)(view.findViewById(R.id.address));
            numcontainer =(LinearLayout)(view.findViewById(R.id.numcontainer));
            orderid = (TextView)(view.findViewById(R.id.orderid));
            summary=(TextView)(view.findViewById(R.id.summary));
            total=(TextView)(view.findViewById(R.id.total));
            number = (TextView)(view.findViewById(R.id.number));
            status = (TextView)(view.findViewById(R.id.status));
            accept= (Button)(view.findViewById(R.id.btnone));
            decline = (Button)(view.findViewById(R.id.btntwo));
        }

    }
}

