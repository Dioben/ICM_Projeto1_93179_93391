package com.example.icm_projeto1_93179_93391.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        View mItemView = mInflater.inflate(R.layout.course_sort_item, parent, false);
        return new CourseViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
    Course current = courses.get(position);
        holder.dataholder.setText(current.name + " at "+current.getFormattedTimestamp());
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView dataholder;
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
            dataholder = mItemView.findViewById(R.id.course_item);
            dataholder.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent inspect = new Intent(activity,CourseInfoActivity.class);
            inspect.putExtra("course",mAdapter.courses.get(this.getAdapterPosition()));
            activity.startActivity(inspect);
        }
    }
}
