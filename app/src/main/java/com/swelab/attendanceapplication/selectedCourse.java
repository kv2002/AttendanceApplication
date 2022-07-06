package com.swelab.attendanceapplication;

public class selectedCourse {
    private static String courseId;

    public static String getCourseId() {
        return courseId;
    }

    public static void setCourseId(String courseId) {
        selectedCourse.courseId = courseId;
    }
}
