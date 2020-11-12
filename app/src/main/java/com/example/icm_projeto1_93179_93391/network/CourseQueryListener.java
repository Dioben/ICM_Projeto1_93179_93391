package com.example.icm_projeto1_93179_93391.network;

import com.example.icm_projeto1_93179_93391.datamodel.Course;

import java.util.LinkedList;

public interface CourseQueryListener {
    void onCourseListing(LinkedList<Course> list);
}
