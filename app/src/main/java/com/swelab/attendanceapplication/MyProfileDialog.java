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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class MyProfileDialog extends DialogFragment {

    private TextView name;
    private TextInputLayout newNameInputText;
    private TextInputEditText newNameEditText;
    private TextView actionOk;
    private TextView actionCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_my_profile, container, false);
        name = view.findViewById(R.id.name);
        newNameInputText = view.findViewById(R.id.new_name_text_input);
        newNameEditText = view.findViewById(R.id.new_name_edit_text);
        actionOk = view.findViewById(R.id.action_ok);
        actionCancel = view.findViewById(R.id.action_cancel);

        actionCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        actionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_name = newNameEditText.getText().toString();
                if (txt_name.isEmpty()) newNameInputText.setError("Name can not be empty");
                else {
                    Map<String, Object> NEW = new HashMap<>();
                    NEW.put("name", txt_name);
                    FirebaseFirestore.getInstance().collection("users").document(currUser.getFirebaseUser().getUid()).set(NEW, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                newNameEditText.setText("Successfully changed name to " + txt_name);
                                actionOk.setText("");
                                actionOk.setEnabled(false);
                            } else {
                                newNameEditText.setText("failed");
                            }
                        }
                    });
                }
            }
        });

        return view;
    }
}
