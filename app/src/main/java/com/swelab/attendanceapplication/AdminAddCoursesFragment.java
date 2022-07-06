package com.swelab.attendanceapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminAddCoursesFragment extends Fragment {

    private TextInputLayout nameInputText;
    private TextInputEditText nameEditText;
    private TextInputLayout IDInputText;
    private TextInputEditText IDEditText;
    private TextInputLayout teacherKeyInputText;
    private TextInputEditText teacherKeyEditText;
    private TextInputLayout studentKeyInputText;
    private TextInputEditText studentKeyEditText;
    private MaterialButton addCourse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_add_courses, container, false);
        nameInputText = view.findViewById(R.id.name_text_input);
        nameEditText = view.findViewById(R.id.name_edit_text);
        IDInputText = view.findViewById(R.id.id_text_input);
        IDEditText = view.findViewById(R.id.id_edit_text);
        teacherKeyInputText = view.findViewById(R.id.teacher_key_text_input);
        teacherKeyEditText = view.findViewById(R.id.teacher_key_edit_text);
        studentKeyInputText = view.findViewById(R.id.student_key_text_input);
        studentKeyEditText = view.findViewById(R.id.student_key_edit_text);
        addCourse = view.findViewById(R.id.add_course);

        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_name = nameEditText.getText().toString();
                String txt_id = IDEditText.getText().toString();
                String txt_teacher_key = teacherKeyEditText.getText().toString();
                String txt_student_key = studentKeyEditText.getText().toString();
                if (txt_name.isEmpty()) {
                    nameInputText.setError("Name can not be empty");
                }else if (txt_id.isEmpty()) {
                    nameInputText.setError("Course ID can not be empty");
                } else if (txt_teacher_key.isEmpty()) {
                    nameInputText.setError("Teacher Key can not be empty");
                } else if (txt_student_key.isEmpty()) {
                    nameInputText.setError("Student Key not be empty");
                } else {
                    Map<String, Object> data = new HashMap<>();
                    data.put("courseID", txt_id);
                    data.put("courseName", txt_name);
                    data.put("studentKey", txt_student_key);
                    data.put("teacherKey", txt_teacher_key);
                    data.put("Students", new HashMap<>());
                    data.put("Teachers", new HashMap<>());
                    FirebaseFirestore.getInstance().collection("Classes").add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                nameEditText.setText("Course Successfully added");
                                IDEditText.setText(null);
                                teacherKeyEditText.setText(null);
                                studentKeyEditText.setText(null);
                            }
                        }
                    });
                }
            }
        });

        return view;
    }
}