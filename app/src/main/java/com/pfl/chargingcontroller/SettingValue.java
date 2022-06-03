package com.pfl.chargingcontroller;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SettingValue {
    private final static String LOG_TAG = "SettingValue";

    private List<SettingValueItem> items = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private boolean isChanged = false;
    private File file, fileDefaults;
    private Context context;
    private FragmentManager fragmentManager;

    ListenerAdapter listenerAdapter = new ListenerAdapter() {
        @Override
        public void onAdapterEvent(Events event, int position) {
            switch (event) {
                case DELETE_ITEM:
                    items.remove(position);
                    adapter.notifyItemRemoved(position);
                    isChanged = true;
                    break;
                case EDIT_ITEM:
                    editItem(position);
                    break;
                case READ_SYSTEM:
                    readSystem(position);
                    break;
                case STORE_DEFAULT:
                    storeDefaults(position);
                    break;
                case RESTORE_DEFAULT:
                    restoreDefaults(position);
                    break;
                case WRITE_SYSTEM:
                    writeSystem(position);
                    break;
                case UPDATE_ITEM:
                    isChanged = true;
                    break;
            }
        }
    };

    public SettingValue() {
        adapter = new SettingValueAdapter(items, listenerAdapter);
    }

    public SettingValue(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        fileDefaults = new File(context.getFilesDir(), "Defaults.txt");
        file = new File(context.getFilesDir(), "SettingValue.txt");
        load(file);
        adapter = new SettingValueAdapter(items, listenerAdapter);
    }



    public void addItem() {
        SettingValueDialog svd = new SettingValueDialog();
        svd.setListener(new ListenerDialog() {
            @Override
            public void onDialogApply(String path) {
                items.add(new SettingValueItem(path, ""));
                adapter.notifyItemInserted(items.size() - 1);
                isChanged = true;
            }
        });
        svd.show(fragmentManager, "SettingValueDialog");
    }

    public void editItem(int position) {
        SettingValueDialog svd = new SettingValueDialog(items.get(position).getPath());
        svd.setListener(new ListenerDialog() {
            @Override
            public void onDialogApply(String path) {
                items.get(position).setPath(path);
                adapter.notifyItemChanged(position);
                isChanged = true;
            }
        });
        svd.show(fragmentManager, "SettingValueDialog");
    }

    public void save() {
        save(file);
    }

    public void save(File file) {
        if (isChanged) {
            try {
                StringBuilder stringBuilder = new StringBuilder();
                for (SettingValueItem i : items) {
                    stringBuilder.append(i.getPath() + ":" + i.getValue() + "\n");
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
                    if (pv.length == 1) {
                        items.add(new SettingValueItem(pv[0], ""));
                    }
                    if (pv.length == 2) {
                        items.add(new SettingValueItem(pv[0], pv[1]));
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

    private void storeDefaults(int position) {

        try {
            String path = items.get(position).getPath();
            String value = findDefaultValueInPath(path);

            if (value == null) {
                String sysValue = readSystemValue(path);
                appendDefaults(path, sysValue);
                items.get(position).setValue(sysValue);
                adapter.notifyItemChanged(position);
                Toast.makeText(context, "Value stored to defaults", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Value exist in defaults", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private void appendDefaults(String path, String value) throws Exception {

        String str = path + ":" + value + "\n";
        FileWriter fileWriter = new FileWriter(file, true);
        fileWriter.append(str);
        fileWriter.close();
        Toast.makeText(context, "Stored to defaults", Toast.LENGTH_SHORT).show();

    }

    private String readSystemValue(String path) throws Exception {
        RootSession r = new RootSession();
        String value = r.readValue(path);
        r.close();
        return value;
    }

    private List<SettingValueItem> readFileDefaults() throws Exception{

        if (!fileDefaults.exists()) {
            return null;
        }

        List<SettingValueItem> items = new LinkedList<>();
        BufferedReader reader = new BufferedReader(new FileReader(fileDefaults));
        String str = "";
        while (str != null) {
            if (str.contains(":")) {
                String[] pv = str.split(":");
                /*if (pv.length == 1) {
                    items.add(new SettingValueItem(pv[0], ""));
                }*/
                if (pv.length == 2) {
                    items.add(new SettingValueItem(pv[0], pv[1]));
                }
            }
            str = reader.readLine();
        }
        reader.close();
        return items;

    }

    private String findDefaultValueInPath(String path) throws Exception {
        List<SettingValueItem> tmpItems = readFileDefaults();
        if (tmpItems != null) {
            for (SettingValueItem i : tmpItems) {
                if (i.getPath().equals(path)) {
                    return i.getValue();
                }
            }
        }
        return null;
    }

    private boolean isDefaultValueExist(int position, List<SettingValueItem> itemsFile) {
        String p = items.get(position).getPath();
        for (SettingValueItem i : itemsFile) {
            if (p.equals(i.getPath())) {
                return true;
            }
        }
        return false;
    }

    private void restoreDefaults(int position) {

        try {

            String path = items.get(position).getPath();
            String value = findDefaultValueInPath(path);

            if (value != null) {
                RootSession r = new RootSession();
                r.writeValueForce(path, value);
                r.close();
                items.get(position).setValue(value);
                adapter.notifyItemChanged(position);
            } else {
                Toast.makeText(context, "Value not exist in defaults", Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void readSystem(int position) {
        try {
            items.get(position).setValue(readSystemValue(items.get(position).getPath()));
            adapter.notifyItemChanged(position);
            Toast.makeText(context, "Success reading system", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error reading system", Toast.LENGTH_SHORT).show();
        }

    }
    private void writeSystem(int position) {
        try {
            RootSession r = new RootSession();
            r.writeValueForce(items.get(position).getPath(), items.get(position).getValue());
            r.close();
            Toast.makeText(context, "Success writing system", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error writing system", Toast.LENGTH_SHORT).show();
        }
    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }


    public interface ListenerDialog {
        public void onDialogApply(String path);
    }

    public interface ListenerAdapter {
        public void onAdapterEvent(Events event, int position);
    }

    enum Events {
        DELETE_ITEM,
        EDIT_ITEM,
        READ_SYSTEM,
        STORE_DEFAULT,
        RESTORE_DEFAULT,
        WRITE_SYSTEM,
        UPDATE_ITEM
    }
}
