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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class TeacherMyCoursesDialog extends DialogFragment {

    private TextInputLayout codeTextInput;
    private TextInputEditText codeEditText;
    private TextInputLayout timeTextInput;
    private TextInputEditText timeEditText;
    private TextView courseName;
    private TextView courseId;
    private TextView actionOk;
    private TextView actionCancel;
    private TextView actionViewStats;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_teacher_my_courses, container, false);
        codeTextInput = view.findViewById(R.id.code_text_input);
        codeEditText = view.findViewById(R.id.code_edit_text);
        courseName = view.findViewById(R.id.course_name);
        courseId = view.findViewById(R.id.course_id);
        actionCancel = view.findViewById(R.id.action_cancel);
        actionOk = view.findViewById(R.id.action_ok);
        actionViewStats = view.findViewById(R.id.action_view_stats);
        timeTextInput = view.findViewById(R.id.time_text_input);
        timeEditText = view.findViewById(R.id.time_edit_text);

        FirebaseFirestore.getInstance().collection("Classes").document(selectedCourse.getCourseId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                courseName.setText(task.getResult().get("courseName").toString());
                courseId.setText(task.getResult().get("courseID").toString());
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
                String txt_time = timeEditText.getText().toString();
                Timestamp curr_time = Timestamp.now();
                Timestamp endTime;
                codeTextInput.setError(null);
                timeTextInput.setError(null);
                CollectionReference students = FirebaseFirestore.getInstance().collection("Students");
                DocumentReference attendanceCodes = FirebaseFirestore.getInstance().collection("attendanceCodes").document(selectedCourse.getCourseId());
                Map<String, Object> newCode = new HashMap<>();
                Map<String, Object> newCodeData = new HashMap<>();
                if (!txt_code.isEmpty() && !txt_time.isEmpty()) {
                    newCodeData.put("startTime", Timestamp.now());
                    newCodeData.put("endTime", new Timestamp(Timestamp.now().getSeconds() + 60 * Integer.parseInt(txt_time), 0));
                    Map<String, Object> IPs = new HashMap<>();
                    newCodeData.put("IP", IPs);
                    newCode.put(txt_code, newCodeData);
                    attendanceCodes.set(newCode, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            actionOk.setText("");
                            actionOk.setEnabled(false);
                            codeEditText.setText(txt_code + " set as attendance code");
                            timeEditText.setText(txt_time + " min set as time");
                        }
                    });
                } else if (txt_code.isEmpty()) {
                    codeTextInput.setError("NO CODE ENTERED");
                } else {
                    timeTextInput.setError("Please enter Time Limit");
                }
            }
        });


        actionViewStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TeacherViewStatsDialog dialog = new TeacherViewStatsDialog();
                dialog.show(getParentFragmentManager(), "");
            }
        });


        return view;
    }
}