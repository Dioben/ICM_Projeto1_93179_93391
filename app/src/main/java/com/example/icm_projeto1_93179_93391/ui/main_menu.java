package com.example.icm_projeto1_93179_93391.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.icm_projeto1_93179_93391.R;

public class main_menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void free_play_onClick(View view) {
        Intent intent = new Intent(this, TrackingActivity.class);
        startActivity(intent);
    }
}