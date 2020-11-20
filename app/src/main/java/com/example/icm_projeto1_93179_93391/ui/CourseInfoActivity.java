package com.example.icm_projeto1_93179_93391.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.icm_projeto1_93179_93391.R;
import com.example.icm_projeto1_93179_93391.datamodel.Course;

public class CourseInfoActivity extends AppCompatActivity {
    private Course course;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info);

        Toolbar myToolbar = findViewById(R.id.course_info_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        course = getIntent().getParcelableExtra("course");
    }

    @Override
    protected void onStart() {
        super.onStart();
        TextView text= findViewById(R.id.course_info_display);
        text.setText(course.getName()+ " - " +course.getFormattedTimestamp()+ " by "+course.getUser());
    }

    public void run_button_onClick(View view) {
        Intent intent = new Intent(this, FollowingActivity.class);
        intent.putExtra("course",course);
        startActivity(intent);
    }
}