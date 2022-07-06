package com.swelab.attendanceapplication;

import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class StudentAllCoursesDialog extends DialogFragment {

    private TextInputLayout keyTextInput;
    private TextInputEditText keyEditText;
    private TextView courseName;
    private TextView courseId;
    private TextView actionOk;
    private TextView actionCancel;
    private DocumentSnapshot studentData;
    private DocumentSnapshot classData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_student_all_courses, container, false);
        keyTextInput = view.findViewById(R.id.key_text_input);
        keyEditText = view.findViewById(R.id.key_edit_text);
        courseName = view.findViewById(R.id.course_name);
        courseId = view.findViewById(R.id.course_id);
        actionCancel = view.findViewById(R.id.action_cancel);
        actionOk = view.findViewById(R.id.action_ok);

        FirebaseFirestore.getInstance().collection("Classes").document(selectedCourse.getCourseId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                courseName.setText(task.getResult().get("courseName").toString());
                courseId.setText(task.getResult().get("courseID").toString());
                classData = task.getResult();
                FirebaseFirestore.getInstance().collection("Students").document(currUser.getFirebaseUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        studentData = task.getResult();
                        Map<String, Object> data = studentData.getData();
                        Map<String, Object> studentClasses = (Map<String, Object>) data.get("Classes");
                        if (studentClasses.containsKey(selectedCourse.getCourseId())) {
                            keyEditText.setText("Already in the Course");
                            actionOk.setText("");
                            actionOk.setEnabled(false);
                        }
                    }
                });
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
                keyTextInput.setError(null);
                String txt_key = keyEditText.getText().toString();
                if (txt_key.isEmpty()) {
                    keyTextInput.setError("Key cannot be empty");
                } else {
                    if (classData.get("studentKey").toString().equals(txt_key)) {
                        Map<String, Object> data = studentData.getData();
                        Map<String, Object> studentClasses = (Map<String, Object>) data.get("Classes");
                        Map<String, Object> newCourse = new HashMap<>();
                        studentClasses.put(selectedCourse.getCourseId(), newCourse);
                        data.put("Classes", studentClasses);
                        FirebaseFirestore.getInstance().collection("Students").document(currUser.getFirebaseUser().getUid()).set(data);
                        data = classData.getData();
                        Map<String, Object> classStudents = (Map<String, Object>) data.get("Students");
                        classStudents.put(currUser.getFirebaseUser().getUid(), studentData.getReference());
                        data.put("Students", classStudents);
                        FirebaseFirestore.getInstance().collection("Classes").document(selectedCourse.getCourseId()).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                keyEditText.setText("Course Joined");
                                actionOk.setText("");
                                actionOk.setEnabled(false);
                            }
                        });
                    } else {
                        keyTextInput.setError("Incorrect Key");
                    }
                }
            }
        });
        return view;
    }
}

