package com.example.icm_projeto1_93179_93391.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icm_projeto1_93179_93391.R;
import com.example.icm_projeto1_93179_93391.datamodel.Course;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private List<Course> courses;
    private Activity activity;
    private LayoutInflater mInflater;

    CourseAdapter(Context context, List<Course> courseList, Activity parent){
        mInflater = LayoutInflater.from(context);
        courses=courseList;
        activity=parent;
    }
    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = null;
        if (activity instanceof CourseInfoActivity) {
            mItemView = mInflater.inflate(R.layout.course_list_copy_item, parent, false);
        } else {
            mItemView = mInflater.inflate(R.layout.course_list_item, parent, false);
        }
        return new CourseViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course current = courses.get(position);
        if ((position % 2) == 0) {
            holder.course_item.setBackgroundColor(ContextCompat.getColor(activity,R.color.grey_table_even));
        } else {
            holder.course_item.setBackgroundColor(ContextCompat.getColor(activity,R.color.grey_table_odd));
        }
        holder.course_name.setText(current.name);
        if (!current.anon)
            holder.owner_name.setText(current.getUser());
        else
            holder.owner_name.setText(R.string.anonymous_text);
        holder.date_uploaded.setText(DateUtils.getRelativeTimeSpanString(current.getTimestamp(), Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS));
        holder.course_length.setText(current.formattedTrack_length());
        holder.course_runtime.setText(String.valueOf(current.formattedRuntime()));
        holder.course_rating.setText(String.valueOf(current.getRating()));
        if (holder.course_distance != null) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            FusedLocationProviderClient locationclient = LocationServices.getFusedLocationProviderClient(activity);
            locationclient.getLastLocation().addOnSuccessListener(
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                float[] results = new float[3];
                                android.location.Location.distanceBetween(current.getLat(),current.getLon(),location.getLatitude(),location.getLongitude(),results);
                                DecimalFormat df = new DecimalFormat("#.###");
                                df.setRoundingMode(RoundingMode.CEILING);
                                holder.course_distance.setText(String.valueOf(df.format(results[0]/1000))+" km");
                            }
                        }
                    }
            );
        }
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public void clear() {
        courses.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Course> list) {
        courses.addAll(list);
        notifyDataSetChanged();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout course_item;
        TextView course_name;
        TextView owner_name;
        TextView date_uploaded;
        TextView course_length;
        TextView course_runtime;
        TextView course_rating;
        TextView course_distance;

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
            course_length = mItemView.findViewById(R.id.course_length);
            course_runtime = mItemView.findViewById(R.id.course_runtime);
            course_rating = mItemView.findViewById(R.id.course_rating);
            course_distance = mItemView.findViewById(R.id.course_distance);
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
