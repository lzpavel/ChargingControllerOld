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

public class StopChargingDialog extends DialogFragment {

    private final static String LOG_TAG = "StopChargingDialog";

    //public String path;
    //public String value;

    //private List<StopChargingItem> items;
    //private RecyclerView.Adapter adapter;

    private EditText etPath;
    private EditText etOnValue;
    private EditText etOffValue;

    private String path = null;
    private String onValue = null;
    private String offValue = null;

    private StopCharging.ListenerDialog listenerDialog;

    /*public StopChargingDialog(List<StopChargingItem> items, RecyclerView.Adapter adapter) {
        this.items = items;
        this.adapter = adapter;
    }*/
    public StopChargingDialog() {

    }

    public StopChargingDialog(String path, String onValue, String offValue) {
        this.path = path;
        this.onValue = onValue;
        this.offValue = offValue;
    }

    public void setListener(StopCharging.ListenerDialog listenerDialog) {
        this.listenerDialog = listenerDialog;
    }

    /*public interface StopChargingDialogListener {
        public void onStopChargingDialogPositiveClick(String path, String value);
        //public void onStopChargingDialogPositiveClick();
    }*/

    //StopChargingDialogListener Dialoglistener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        /*try {
            listener = (StopChargingDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement DialogListener");
        }*/
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

        View view = inflater.inflate(R.layout.stop_charging_dialog, null);
        etPath = view.findViewById(R.id.editTextStopChargingDialogPath);
        etOnValue = view.findViewById(R.id.editTextStopChargingDialogOnValue);
        etOffValue = view.findViewById(R.id.editTextStopChargingDialogOffValue);

        if (path != null) {
            etPath.setText(path);
        }
        if (onValue != null) {
            etOnValue.setText(onValue);
        }
        if (offValue != null) {
            etOffValue.setText(offValue);
        }

        builder.setTitle("On Stop Charging");
        builder.setView(view);
        //builder.setView(inflater.inflate(R.layout.stop_charging_dialog, null));
        builder.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //items.add(new StopChargingItem(etPath.getText().toString(), etValue.getText().toString()));
                //adapter.notifyItemInserted(items.size() - 1);

                //Log.d(LOG_TAG, etPath.getText().toString() + " " + etValue.getText().toString());

                //path = etPath.getText().toString();
                //value = etValue.getText().toString();

                listenerDialog.onDialogApply(etPath.getText().toString(), etOnValue.getText().toString(), etOffValue.getText().toString());

                //listener.onStopChargingDialogPositiveClick(etPath.getText().toString(), etValue.getText().toString());
                //listener.onStopChargingDialogPositiveClick();
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
