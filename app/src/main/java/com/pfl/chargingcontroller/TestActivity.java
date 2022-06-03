package com.pfl.chargingcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;

public class TestActivity extends AppCompatActivity {

    Button buttonTest;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        buttonTest = findViewById(R.id.buttonTest);
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(TestActivity.this, buttonTest);
                popup.getMenuInflater().inflate(R.menu.setting_value_item_menu, popup.getMenu());
                popup.show();
            }
        });

    }
}