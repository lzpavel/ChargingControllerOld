package com.pfl.chargingcontroller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SettingValueDialog extends DialogFragment {
    private final static String LOG_TAG = "SettingValueDialog";

    private EditText etPath;
    private String path = null;


    private SettingValue.ListenerDialog listener;

    public SettingValueDialog() {
    }

    public SettingValueDialog(String path) {
        this.path = path;
    }

    public void setListener(SettingValue.ListenerDialog listener) {
        this.listener = listener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.setting_value_dialog, null);
        etPath = view.findViewById(R.id.editTextSettingValueDialogPath);

        if (path != null) {
            etPath.setText(path);
        }

        builder.setTitle("Edit path");
        builder.setView(view);
        //builder.setView(inflater.inflate(R.layout.stop_charging_dialog, null));
        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onDialogApply(etPath.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        });
        return builder.create();
    }
}
