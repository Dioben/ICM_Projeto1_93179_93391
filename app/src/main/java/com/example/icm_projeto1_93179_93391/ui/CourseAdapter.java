package com.example.icm_projeto1_93179_93391.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icm_projeto1_93179_93391.R;
import com.example.icm_projeto1_93179_93391.datamodel.Course;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private List<Course> courses;
    private Activity activity; //main not be necessary, used to needing it
    private LayoutInflater mInflater;

    CourseAdapter(Context context, List<Course> courseList, Activity parent){
        mInflater = LayoutInflater.from(context);
        courses=courseList;
        activity=parent;
    }
    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.courselistelement,
                parent, false);
        return new CourseViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //UI elements here
        //
        //
        CourseAdapter mAdapter;
        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public CourseViewHolder(View mItemView, CourseAdapter courseAdapter) {
            super(mItemView);
            mAdapter = courseAdapter;
            //buttonView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        //whatever you want them to expand here
            // mAdapter.activity.dostuff()
        }
    }
}
