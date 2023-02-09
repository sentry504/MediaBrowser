package com.example.myapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

public class adapterRecycler extends RecyclerView.Adapter<adapterRecycler.ViewHolder> {
    public JSONArray listData;

    //CONSTRUCTOR DE LA CLASE
    public  adapterRecycler(JSONArray ld){
        this.listData = ld;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView page;
        TextView url;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            page = itemView.findViewById(R.id.name_url);
            url = itemView.findViewById(R.id.url);
        }


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemActivity = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item, parent, false);
        return new ViewHolder(itemActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull adapterRecycler.ViewHolder holder, int position) {
        try {
            holder.page.setText(listData.getJSONObject(position).getString("name"));
            holder.url.setText(listData.getJSONObject(position).getString("url"));

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /*Receives the number of elements that will be listed on the screen, taking into account that if
     the number of elements exceeds what can be displayed on the screen, the rest of the elements in
     the list will be loaded only until the moment we scroll to the site*/
    @Override
    public int getItemCount() {
        return listData.length();
    }
}