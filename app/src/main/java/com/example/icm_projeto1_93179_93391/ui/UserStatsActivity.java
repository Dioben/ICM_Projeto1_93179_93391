package com.example.icm_projeto1_93179_93391.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.icm_projeto1_93179_93391.R;
import com.example.icm_projeto1_93179_93391.datamodel.User;
import com.example.icm_projeto1_93179_93391.network.FirebaseQueryClient;

public class UserStatsActivity extends AppCompatActivity {
    User user;
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_stats);
        user = FirebaseQueryClient.getInstance().getUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        text = findViewById(R.id.user_info_text);
        String t = "";
        t+="Name: "+user.getUsername()+"\n"
        +"\nTop Score: "+user.getTop_rating()
        +"\nTotal track length: "+ String.format("%.3f km",user.getTotal_tracklength())
        +"\nTotal Runtime: "+user.formattedRuntime()
        +"\nAverage Speed: "+String.format("%.3f km/h",user.getAvg_speed());

    }
}