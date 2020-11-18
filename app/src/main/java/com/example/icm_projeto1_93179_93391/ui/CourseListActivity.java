package com.example.icm_projeto1_93179_93391.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.icm_projeto1_93179_93391.R;

public class CourseListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        String[] owners = new String[] {"Me","Anyone"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.course_sort_item, owners);
        AutoCompleteTextView dropdown = findViewById(R.id.owner_dropdown);
        dropdown.setAdapter(adapter);
        //dropdown.setFreezesText(false);
        dropdown.setText(owners[0],false);

        String[] sorts = new String[] {"Proximity","Date uploaded"};
        adapter = new ArrayAdapter<>(this, R.layout.course_sort_item, sorts);
        dropdown = findViewById(R.id.sort_dropdown);
        dropdown.setAdapter(adapter);
        //dropdown.setFreezesText(false);
        dropdown.setText(sorts[0],false);

    }
}