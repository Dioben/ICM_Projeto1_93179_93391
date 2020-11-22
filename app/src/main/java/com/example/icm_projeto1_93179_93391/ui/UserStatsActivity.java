package com.example.icm_projeto1_93179_93391.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

        Toolbar myToolbar = (Toolbar) findViewById(R.id.course_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ((TextView) findViewById(R.id.user_name)).setText(user.getUsername());
        ((TextView) findViewById(R.id.user_top_score)).setText(String.valueOf(user.getTop_rating()));
        ((TextView) findViewById(R.id.user_track_count)).setText(String.valueOf(user.getCoursecount()));
        ((TextView) findViewById(R.id.user_total_track_length)).setText(String.format("%.3f km",user.getTotal_tracklength()));
        ((TextView) findViewById(R.id.user_total_runtime)).setText(user.formattedRuntime());
        ((TextView) findViewById(R.id.user_average_speed)).setText(String.format("%.3f km/h",user.getAvg_speed()));

    }
}