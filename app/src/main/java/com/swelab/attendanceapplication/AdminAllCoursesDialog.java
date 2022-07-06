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

import java.util.Map;

public class AdminAllCoursesDialog extends DialogFragment {

    private TextInputLayout newIdTextInput;
    private TextInputEditText newIdEditText;
    private TextInputLayout newNameTextInput;
    private TextInputEditText newNameEditText;
    private TextView currName;
    private TextView currId;
    private TextView actionOk;
    private TextView actionCancel;
    private Map<String, Object> classData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_admin_all_courses, container, false);
        newIdEditText = view.findViewById(R.id.new_id_edit_text);
        newIdTextInput = view.findViewById(R.id.new_id_text_input);
        newNameEditText = view.findViewById(R.id.new_name_edit_text);
        newNameTextInput = view.findViewById(R.id.new_name_text_input);
        currName = view.findViewById(R.id.course_name);
        currId = view.findViewById(R.id.course_id);
        actionCancel = view.findViewById(R.id.action_cancel);
        actionOk = view.findViewById(R.id.action_ok);

        FirebaseFirestore.getInstance().collection("Classes").document(selectedCourse.getCourseId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                classData = task.getResult().getData();
                currName.setText(classData.get("courseName").toString());
                newNameEditText.setText(classData.get("courseName").toString());
                currId.setText(classData.get("courseID").toString());
                newIdEditText.setText(classData.get("courseID").toString());
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
                newIdTextInput.setError(null);
                newNameTextInput.setError(null);
                String txt_name = newNameEditText.getText().toString();
                String txt_code = newIdEditText.getText().toString();
                if (txt_name.isEmpty()) newNameTextInput.setError("Name can not be empty");
                else if (txt_code.isEmpty()) newIdTextInput.setError("ID can not be empty");
                else {
                    classData.put("courseName", txt_name);
                    classData.put("courseID", txt_code);
                    FirebaseFirestore.getInstance().collection("Classes").document(selectedCourse.getCourseId()).set(classData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                newIdEditText.setText("successfully modified to " + newIdEditText.getText().toString());
                                newNameEditText.setText("successfully modified to " + newNameEditText.getText().toString());
                            } else {
                                newIdTextInput.setError(task.getException().toString());
                            }
                        }
                    });
                }
            }
        });
        return view;
    }
}
