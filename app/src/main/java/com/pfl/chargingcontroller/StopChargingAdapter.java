package com.pfl.chargingcontroller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StopChargingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String LOG_TAG = "StopChargingAdapter";

    private List<StopChargingItem> items;
    private StopCharging.ListenerAdapter listenerAdapter;
    private Context parentContext;

    public StopChargingAdapter(List<StopChargingItem> items, StopCharging.ListenerAdapter listenerAdapter) {
        this.items = items;
        this.listenerAdapter = listenerAdapter;
    }

    /*public void setListener(StopCharging.Listener listener) {
        this.listener = listener;
    }*/

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        parentContext = parent.getContext();
        return new RecyclerView.ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.stop_charging_item, parent,false)
        ) {};
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TextView tvPath = holder.itemView.findViewById(R.id.textViewStopChargingPath);
        tvPath.setText("Path: " + this.items.get(holder.getAdapterPosition()).getPath());

        TextView tvOnValue = holder.itemView.findViewById(R.id.textViewStopChargingOnValue);
        tvOnValue.setText("On: " + this.items.get(holder.getAdapterPosition()).getOnValue());

        TextView tvValue = holder.itemView.findViewById(R.id.textViewStopChargingOffValue);
        tvValue.setText("Off: " + this.items.get(holder.getAdapterPosition()).getOffValue());

        ImageButton imageButtonStopChargingItem = holder.itemView.findViewById(R.id.imageButtonStopChargingItem);
        imageButtonStopChargingItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(parentContext, imageButtonStopChargingItem);
                popup.getMenuInflater().inflate(R.menu.stop_charging_item_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.itemStopChargingItemDelete:
                                listenerAdapter.onAdapterEvent(StopCharging.Events.DELETE, holder.getAdapterPosition());
                                return true;
                            case R.id.itemStopChargingItemEdit:
                                listenerAdapter.onAdapterEvent(StopCharging.Events.EDIT, holder.getAdapterPosition());
                                return true;

                        }
                        return false;
                    }
                });
                popup.show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }
}
