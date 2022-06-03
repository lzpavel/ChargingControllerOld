package com.pfl.chargingcontroller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

//import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SettingValueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String LOG_TAG = "SettingValueAdapter";

    private List<SettingValueItem> items;
    private SettingValue.ListenerAdapter listener;
    private Context parentContext;

    public SettingValueAdapter(List<SettingValueItem> items, SettingValue.ListenerAdapter listener) {
        this.items = items;
        this.listener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //return null;
        parentContext = parent.getContext();
        return new RecyclerView.ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.setting_value_item, parent,false)
        ) {};
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        TextView tvPath = holder.itemView.findViewById(R.id.textViewSettingValuePath);
        tvPath.setText("Path: " + this.items.get(holder.getAdapterPosition()).getPath());

        EditText etValue = holder.itemView.findViewById(R.id.editTextSettingValue);
        etValue.setText(this.items.get(holder.getAdapterPosition()).getValue());
        etValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    etValue.clearFocus();
                }
                return false;
            }
        });
        etValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    items.get(holder.getAdapterPosition()).setValue(etValue.getText().toString());
                    listener.onAdapterEvent(SettingValue.Events.UPDATE_ITEM, holder.getAdapterPosition());
                }
            }
        });

        ImageButton imageButtonEdit = holder.itemView.findViewById(R.id.imageButtonSettingValue);
        imageButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(parentContext, imageButtonEdit);
                popup.getMenuInflater().inflate(R.menu.setting_value_item_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.itemSettingValueItemDelete:
                                listener.onAdapterEvent(SettingValue.Events.DELETE_ITEM, holder.getAdapterPosition());
                                return true;
                            case R.id.itemSettingValueItemEdit:
                                listener.onAdapterEvent(SettingValue.Events.EDIT_ITEM, holder.getAdapterPosition());
                                return true;
                            case R.id.itemSettingValueItemStoreDefault:
                                listener.onAdapterEvent(SettingValue.Events.STORE_DEFAULT, holder.getAdapterPosition());
                                return true;
                            case R.id.itemSettingValueItemRestoreDefault:
                                listener.onAdapterEvent(SettingValue.Events.RESTORE_DEFAULT, holder.getAdapterPosition());
                                return true;
                            case R.id.itemSettingValueItemRead:
                                listener.onAdapterEvent(SettingValue.Events.READ_SYSTEM, holder.getAdapterPosition());
                                return true;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        Button buttonSettingValue = holder.itemView.findViewById(R.id.buttonSettingValue);
        buttonSettingValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onAdapterEvent(SettingValue.Events.WRITE_SYSTEM, holder.getAdapterPosition());

            }
        });
    }

    @Override
    public int getItemCount() {
        //return 0;
        return this.items.size();
    }


}
