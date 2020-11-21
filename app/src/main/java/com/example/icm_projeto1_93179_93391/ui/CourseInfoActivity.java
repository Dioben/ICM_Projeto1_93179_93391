package com.example.icm_projeto1_93179_93391.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icm_projeto1_93179_93391.R;
import com.example.icm_projeto1_93179_93391.datamodel.Course;
import com.example.icm_projeto1_93179_93391.network.CourseQueryListener;
import com.example.icm_projeto1_93179_93391.network.FirebaseQueryClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class CourseInfoActivity extends AppCompatActivity implements CourseQueryListener {
    private Course course;
    private List<String> images;
    CourseAdapter mAdapter;
    RecyclerView courselist;
    FirebaseQueryClient client;
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info);

        Toolbar myToolbar = findViewById(R.id.course_info_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        course = getIntent().getParcelableExtra("course");
        if (course==null) onBackPressed();
        images = course.getPictures();

        CarouselView carouselView = findViewById(R.id.image_carousel);
        carouselView.setPageCount(images.size());
        carouselView.setImageListener(imageListener);
        if (images.size() == 0) {
            carouselView.setVisibility(View.GONE);
        }

        courselist = findViewById(R.id.course_copy_list);
        courselist.setLayoutManager(layoutManager);

        client = FirebaseQueryClient.getInstance();
        client.getOtherRuns(course, CourseInfoActivity.this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        ((TextView)findViewById(R.id.course_name)).setText(course.getName());
        ((TextView)findViewById(R.id.owner_name)).setText(course.getUser());
        ((TextView)findViewById(R.id.date_uploaded)).setText(DateUtils.getRelativeTimeSpanString(course.getTimestamp(), Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS));
        ((TextView)findViewById(R.id.rating)).setText(String.valueOf(course.getRating()));
    }

    public void run_button_onClick(View view) {
        Intent intent = new Intent(this, FollowingActivity.class);
        intent.putExtra("course",course);
        startActivity(intent);
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            Picasso.get().load(images.get(position)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    };

    @Override
    public void onCourseListing(LinkedList<Course> list) {
        mAdapter = new CourseAdapter(CourseInfoActivity.this, list, this);
        courselist.setAdapter(mAdapter);
    }

    @Override
    public void onCourseListingFail() {
        Toast.makeText(this, "Track fetch failed", Toast.LENGTH_LONG).show();
    }
}