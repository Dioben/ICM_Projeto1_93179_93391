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
        AutoCompleteTextView editTextFilledExposedDropdown = findViewById(R.id.owner_dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);

        String[] sorts = new String[] {"Proximity","Date uploaded"};
        adapter = new ArrayAdapter<>(this, R.layout.course_sort_item, sorts);
        editTextFilledExposedDropdown = findViewById(R.id.sort_dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);
    }
}