package com.example.icm_projeto1_93179_93391.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icm_projeto1_93179_93391.R;
import com.example.icm_projeto1_93179_93391.datamodel.Course;

import java.util.Calendar;
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
        View mItemView = mInflater.inflate(R.layout.course_list_item, parent, false);
        return new CourseViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course current = courses.get(position);
        holder.course_name.setText(current.name);
        holder.owner_name.setText(current.getUser());
        holder.date_uploaded.setText(DateUtils.getRelativeTimeSpanString(current.getTimestamp(), Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout course_item;
        TextView course_name;
        TextView owner_name;
        TextView date_uploaded;

        CourseAdapter mAdapter;
        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public CourseViewHolder(View mItemView, CourseAdapter courseAdapter) {
            super(mItemView);
            mAdapter = courseAdapter;
            course_item = mItemView.findViewById(R.id.course_item);
            course_name = mItemView.findViewById(R.id.course_name);
            owner_name = mItemView.findViewById(R.id.owner_name);
            date_uploaded = mItemView.findViewById(R.id.date_uploaded);
            course_item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent inspect = new Intent(activity,CourseInfoActivity.class);
            inspect.putExtra("course",mAdapter.courses.get(this.getAdapterPosition()));
            activity.startActivity(inspect);
        }
    }
}
