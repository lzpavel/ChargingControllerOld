package com.pfl.chargingcontroller;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class StopCharging {

    private final static String LOG_TAG = "StopCharging";

    private List<StopChargingItem> items = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private boolean isChanged = false;
    private File file;
    private FragmentManager fragmentManager;

    ListenerAdapter listenerAdapter = new ListenerAdapter() {
        @Override
        public void onAdapterEvent(Events event, int position) {
            switch (event) {
                case DELETE:
                    items.remove(position);
                    adapter.notifyItemRemoved(position);
                    isChanged = true;
                    break;
                case EDIT:
                    editItem(position);
                    break;
            }
        }
    };

    public StopCharging() {
        adapter = new StopChargingAdapter(items, listenerAdapter);
    }

    public StopCharging(File filesDir, FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        file = new File(filesDir, "StopCharging.txt");
        load(file);
        adapter = new StopChargingAdapter(items, listenerAdapter);
    }



    public void addItem() {
        //StopChargingDialog scd = new StopChargingDialog(items, adapter);
        StopChargingDialog scd = new StopChargingDialog();
        scd.setListener(new ListenerDialog() {
            @Override
            public void onDialogApply(String path, String onValue, String offValue) {
                items.add(new StopChargingItem(path, onValue, offValue));
                adapter.notifyItemInserted(items.size() - 1);
                isChanged = true;
            }
        });
        scd.show(fragmentManager, "StopChargingDialog");
    }

    private void editItem(int position) {
        StopChargingDialog scd = new StopChargingDialog(items.get(position).getPath(),
                items.get(position).getOnValue(), items.get(position).getOffValue());
        scd.setListener(new ListenerDialog() {
            @Override
            public void onDialogApply(String path, String onValue, String offValue) {
                items.get(position).setPath(path);
                items.get(position).setOnValue(onValue);
                items.get(position).setOffValue(offValue);
                adapter.notifyItemChanged(position);
                isChanged = true;
            }
        });
        scd.show(fragmentManager, "StopChargingDialog");
    }

    public void save() {
        save(file);
    }

    public void save(File file) {
        if (isChanged) {
            try {
                StringBuilder stringBuilder = new StringBuilder();
                for (StopChargingItem i : items) {
                    stringBuilder.append(i.getPath() + ":" + i.getOnValue() + ":" + i.getOffValue() + "\n");
                }
                String str = new String(stringBuilder);

                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(str);
                fileWriter.close();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isChanged = false;
            }

        }
    }

    public void load() {
        load(file);
    }

    public void load(File file) {
        try {
            if (!file.exists()) {
                return;
            }
            //file.length()

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String str = "";
            while (str != null) {
                if (str.contains(":")) {
                    String[] pv = str.split(":");
                    if (pv.length == 3) {
                        items.add(new StopChargingItem(pv[0], pv[1], pv[2]));
                    }
                }
                str = reader.readLine();
            }
            reader.close();
            //adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disableCharging() {
        try {
            RootSession r = new RootSession();
            for (StopChargingItem i : items) {
                r.exec("echo " + i.getOffValue() + " > " + i.getPath());
            }
            r.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enableCharging() {
        try {
            RootSession r = new RootSession();
            for (StopChargingItem i : items) {
                r.exec("echo " + i.getOnValue() + " > " + i.getPath());
            }
            r.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }



    public interface ListenerAdapter {
        public void onAdapterEvent(Events event, int position);
    }

    public interface ListenerDialog {
        public void onDialogApply(String path, String onValue, String offValue);
    }

    enum Events {
        DELETE,
        EDIT
    }

}
