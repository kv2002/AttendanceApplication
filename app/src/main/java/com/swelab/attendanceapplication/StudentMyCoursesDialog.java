package com.swelab.attendanceapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentMyCoursesDialog extends DialogFragment {

    private TextInputLayout codeTextInput;
    private TextInputEditText codeEditText;
    private TextView courseName;
    private TextView courseId;
    private TextView actionOk;
    private TextView actionCancel;
    private TextView attendanceGiven;
    private TextView totalClasses;
    private Map<String, Object> attendanceCodesData;
    private Map<String, Object> studentData;
    private Map<String, Object> coursesData;
    private Map<String, Object> currentData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_student_my_courses, container, false);
        codeTextInput = view.findViewById(R.id.code_text_input);
        codeEditText = view.findViewById(R.id.code_edit_text);
        courseName = view.findViewById(R.id.course_name);
        courseId = view.findViewById(R.id.course_id);
        actionCancel = view.findViewById(R.id.action_cancel);
        actionOk = view.findViewById(R.id.action_ok);
        attendanceGiven = view.findViewById(R.id.attendance_given);
        totalClasses = view.findViewById(R.id.total_classes);

        FirebaseFirestore.getInstance().collection("Classes").document(selectedCourse.getCourseId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot value = task.getResult();
                courseName.setText(value.get("courseName").toString());
                courseId.setText(value.get("courseID").toString());
            }
        });
        FirebaseFirestore.getInstance().collection("attendanceCodes").document(selectedCourse.getCourseId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                attendanceCodesData = task.getResult().getData();
                totalClasses.setText("total classes: " + attendanceCodesData.size());
            }
        });
        FirebaseFirestore.getInstance().collection("Students").document(currUser.getFirebaseUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                studentData = task.getResult().getData();
                coursesData = (Map<String, Object>) studentData.get("Classes");
                currentData = (Map<String, Object>) coursesData.get(selectedCourse.getCourseId());
                attendanceGiven.setText("attendance given: " + currentData.size());
            }
        });
        actionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        actionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_code = codeEditText.getText().toString();
                Timestamp curr_time = Timestamp.now();
                codeTextInput.setError(null);
                DocumentReference studentDetails = FirebaseFirestore.getInstance().collection("Students").document(currUser.getFirebaseUser().getUid());
                if (txt_code.isEmpty()) codeTextInput.setError("Empty");
                else if (!attendanceCodesData.containsKey(txt_code)) {
                    codeTextInput.setError("code doesn't exist");
                } else {
                    Map<String, Object> currCode = (Map<String, Object>) attendanceCodesData.get(txt_code);
                    Timestamp endTime = (Timestamp) currCode.get("endTime");
                    if (endTime.getSeconds() > curr_time.getSeconds()) {
                        currentData.put(txt_code, true);
                        coursesData.put(selectedCourse.getCourseId(), currentData);
                        studentData.put("Classes", coursesData);
                        String ip = getIPAddress.getIPAddress();
                        Log.d("IP", ip);
                        Map<String, Object> IPs = (Map<String, Object>) currCode.get("IP");
                        if (IPs.containsKey(getIPAddress.getIPAddress())) {
                            List<String> currIP = (List<String>) IPs.get(ip);
                            currIP.add(currUser.getFirebaseUser().getUid());
                            IPs.put(ip, currIP);
                        } else {
                            List<String> currIP = new ArrayList<>();
                            currIP.add(currUser.getFirebaseUser().getUid());
                            IPs.put(ip, currIP);
                        }
                        currCode.put("IP", IPs);
                        attendanceCodesData.put(txt_code, currCode);
                        studentDetails.set(studentData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                attendanceGiven.setText("attendance given: " + currentData.size());
                                codeEditText.setText("Attendance Taken");
                                actionOk.setText("");
                                actionOk.setEnabled(false);
                                FirebaseFirestore.getInstance().collection("attendanceCodes").document(selectedCourse.getCourseId()).set(attendanceCodesData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        codeEditText.setText(codeEditText.getText().toString() + ",IP address stored");
                                    }
                                });
                            }
                        });
                    } else {
                        codeTextInput.setError("code expired");

                    }
                }
            }
        });
        return view;
    }
}
