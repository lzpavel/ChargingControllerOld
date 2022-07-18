package com.pfl.chargingcontroller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    Button buttonResetBatteryStats;
    Button buttonEnableCharging;
    Button buttonDisableCharging;
    Button buttonTestActivity;
    Switch switchCapacityLimit;
    EditText editTextCapacityLimit;
    EditText editTextCurrentLimit;
    RecyclerView recyclerViewStopCharging;
    RecyclerView recyclerViewSettingValue;
    ImageButton imageButtonStopChargingMain;
    ImageButton imageButtonSettingValueMain;


    Context contextApp;
    Intent intentService;

    ChargingService chargingService;

    private boolean isBound = false;
    private boolean isSwitchFix = false;

    private StopCharging stopCharging;
    private SettingValue settingValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOG_TAG, "onCreate");

        buttonResetBatteryStats = findViewById(R.id.buttonResetBatteryStats);
        buttonEnableCharging = findViewById(R.id.buttonEnableCharging);
        buttonDisableCharging = findViewById(R.id.buttonDisableCharging);
        buttonTestActivity = findViewById(R.id.buttonTestActivity);

        switchCapacityLimit = findViewById(R.id.switchCapacityLimit);
        editTextCapacityLimit = findViewById(R.id.editTextCapacityLimit);
        editTextCurrentLimit = findViewById(R.id.editTextSettingValue);
        recyclerViewStopCharging = findViewById(R.id.recyclerViewStopCharging);
        recyclerViewSettingValue = findViewById(R.id.recyclerViewSettingValue);
        imageButtonStopChargingMain = findViewById(R.id.imageButtonStopChargingMain);
        imageButtonSettingValueMain = findViewById(R.id.imageButtonSettingValueMain);



        contextApp = getApplicationContext();
        intentService = new Intent(this, ChargingService.class);


        stopCharging = new StopCharging(getFilesDir(), getSupportFragmentManager());
        recyclerViewStopCharging.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewStopCharging.setAdapter(stopCharging.getAdapter());

        settingValue = new SettingValue(this, getSupportFragmentManager());
        recyclerViewSettingValue.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSettingValue.setAdapter(settingValue.getAdapter());






        switchCapacityLimit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!isSwitchFix) {
                    if (!chargingService.isStarted && b) {
                        chargingService.percentLimit = Integer.parseInt(editTextCapacityLimit.getText().toString());
                        contextApp.startService(intentService);

                    } else if (chargingService.isStarted && !b) {
                        //contextApp.stopService(intentService);
                        chargingService.stopControl();

                    }
                } else {
                    isSwitchFix = false;
                }

            }
        });

        editTextCapacityLimit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    editTextCapacityLimit.clearFocus();
                }
                return false;
            }
        });

        buttonResetBatteryStats.setOnClickListener(view -> {
            try {
                RootSession.resetBatteryStats();
                //RootSession.enableCharging();
                Toast.makeText(this, "Success battery stats reset", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error battery stats reset", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
        buttonEnableCharging.setOnClickListener(view -> {
            stopCharging.enableCharging();
        });
        buttonDisableCharging.setOnClickListener(view -> {
            stopCharging.disableCharging();
        });
        buttonTestActivity.setOnClickListener(view -> {
            startActivity(new Intent(this, TestActivity.class));
        });

        imageButtonStopChargingMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(MainActivity.this, imageButtonStopChargingMain);
                popup.getMenuInflater().inflate(R.menu.stop_charging_main_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.itemStopChargingMainAdd:
                                stopCharging.addItem();
                                return true;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
        imageButtonSettingValueMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(MainActivity.this, imageButtonSettingValueMain);
                popup.getMenuInflater().inflate(R.menu.setting_value_main_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.itemSettingValueMainAdd:
                                settingValue.addItem();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemExit:
                stopCharging.save();
                settingValue.save();
                finishAffinity();
                System.exit(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
        bindService(new Intent(this, ChargingService.class), connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
    }

    @Override
    protected void onPause() {
        stopCharging.save();
        settingValue.save();
        Log.d(LOG_TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        stopCharging.save();
        settingValue.save();
        unbindService(connection);
        Log.d(LOG_TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        stopCharging.save();
        settingValue.save();
        super.onDestroy();
    }

    private void syncSwitch() {
        if (chargingService.isStarted != switchCapacityLimit.isChecked()) {
            isSwitchFix = true;
            switchCapacityLimit.setChecked(chargingService.isStarted);
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ChargingService.LocalBinder binder = (ChargingService.LocalBinder) iBinder;
            chargingService = binder.getService();
            syncSwitch();
            isBound = true;
            chargingService.stopCharging = stopCharging;
            chargingService.listener = new Listener() {
                @Override
                public void onStopService() {
                    syncSwitch();
                }
            };
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    public interface Listener {
        public void onStopService();
    }

}