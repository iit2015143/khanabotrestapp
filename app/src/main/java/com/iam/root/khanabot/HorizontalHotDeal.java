package com.iam.root.khanabot;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HorizontalHotDeal extends RecyclerView.Adapter<HorizontalHotDeal.MyViewHolder>{

    public Context context;
    public JSONArray hotdeals;

    public HorizontalHotDeal(Context context,JSONArray hot){
        this.context = context;
        hotdeals = hot;
    }

    @NonNull
    @Override
    public HorizontalHotDeal.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hotdeal,parent,false);
        return new HorizontalHotDeal.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        try {
            final JSONObject HotDeal = hotdeals.getJSONObject(position);
            holder.foodname.setText(HotDeal.getString("name"));
            holder.nameofrest.setText(HotDeal.getString("resname"));
            holder.price.setText("Rs "+ HotDeal.getJSONArray("price").getString(0));
            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer value = Integer.parseInt(holder.change.getText().toString());
                    value++;
                    holder.change.setText(value.toString());
                    //HomePage.updatecart(true,HotDeal);
                    //Log.e("error cart",HomePage.mycart.toString());
                    //((HomePage)context).notifychange();
                }
            });

            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer value = Integer.parseInt(holder.change.getText().toString());
                    if(value>0) {
                        value--;
                        holder.change.setText(value.toString());
                        //HomePage.updatecart(false,HotDeal);
                        //Log.e("error cart",HomePage.mycart.toString());
                        //((HomePage)context).notifychange();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return hotdeals.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView add,remove;
        public TextView change,foodname,nameofrest,price;
        public View view;
        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            change = (TextView)(view.findViewById(R.id.change));
            add = (ImageView)(view.findViewById(R.id.add));
            remove = (ImageView)(view.findViewById(R.id.remove));
            foodname=(TextView)(view.findViewById(R.id.foodname));
            nameofrest=(TextView)(view.findViewById(R.id.nameofrest));
            price = (TextView)(view.findViewById(R.id.price));

        }
    }
}