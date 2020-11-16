package com.example.icm_projeto1_93179_93391.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.icm_projeto1_93179_93391.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        findViewById(R.id.googlesign).setOnClickListener(this::googlesign_onClick);
    }

    @Override
    protected void onStart() {
        FirebaseUser usr = auth.getCurrentUser();
        if (usr!=null){ //TODO: SET UP DATA,SKIP TO MAIN MENU
            Intent skip = new Intent(this,main_menu.class);
            startActivity(skip);
        }
        super.onStart();
    }

    public void signbutton_onClick(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void logbutton_onClick(View view) {
        Intent intent = new Intent(this, main_menu.class);
        startActivity(intent);
    }

    public void googlesign_onClick(View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.activity_username_popup, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
        );

        Button confirm_upload_button = (Button) popupView.findViewById(R.id.confirm_username_button);
        confirm_upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logbutton_onClick(v);
            }
        });

        popupWindow.setElevation(20);
        popupWindow.showAtLocation(findViewById(R.id.login_layout), Gravity.CENTER, 0, 0);
    }
}