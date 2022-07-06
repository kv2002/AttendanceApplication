package com.swelab.attendanceapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAddUsersFragment extends Fragment {

    private MaterialButton addUser;
    private TextInputLayout emailTextInput;
    private TextInputEditText emailEditText;
    private TextInputLayout passwordTextInput;
    private TextInputEditText passwordEditText;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_add_users, container, false);
        addUser = view.findViewById(R.id.add_user);
        passwordTextInput = view.findViewById(R.id.password_text_input);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        emailTextInput = view.findViewById(R.id.email_text_input);
        emailEditText = view.findViewById(R.id.email_edit_text);

        Spinner typeOfUser = view.findViewById(R.id.TypeOfUser);
        mAuth = FirebaseAuth.getInstance();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.TypeOfUser, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        typeOfUser.setAdapter(adapter);
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_email = emailEditText.getText().toString();
                String txt_password = passwordEditText.getText().toString();
                String txt_type = typeOfUser.getSelectedItem().toString();
                emailTextInput.setError(null);
                passwordTextInput.setError(null);
                if (txt_email.isEmpty()) {
                    emailTextInput.setError("no email");
                } else if (txt_password.isEmpty()) {
                    passwordTextInput.setError("no password");
                } else {
                    addUser(txt_email, txt_password, txt_type);
                }
            }
        });
        return view;
    }

    private void addUser(String txt_email, String txt_password, String txt_type) {
        mAuth.createUserWithEmailAndPassword(txt_email, txt_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> authResultTask) {
                if (authResultTask.isSuccessful()) {
                    mAuth.sendPasswordResetEmail(txt_email);
                    mAuth.signOut();
                    mAuth.signInWithEmailAndPassword(currUser.getEmail(), currUser.getPass()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                currUser.setFirebaseUser(mAuth.getCurrentUser());
                                String Uid = authResultTask.getResult().getUser().getUid();
                                Map<String, String> userData = new HashMap<>();
                                userData.put("userID", Uid);
                                userData.put("email", txt_email);
                                userData.put("userType", txt_type.toUpperCase());
                                String name = txt_email.substring(0, txt_email.indexOf("@"));
                                Log.d("display Name", name);
                                userData.put("name", name);
                                DocumentReference UDetails = FirebaseFirestore.getInstance().collection("users").document(Uid);
                                UDetails.set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            if (txt_type.toUpperCase().equals("TEACHER")) {
                                                Map<String, Object> teacherDetails = new HashMap<>();
                                                List<String> courses = new ArrayList<>();
                                                teacherDetails.put("Courses", courses);
                                                teacherDetails.put("userDetails", UDetails);
                                                FirebaseFirestore.getInstance().collection("Teachers").document(Uid).set(teacherDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            emailEditText.setText(txt_email + "  Teacher Created");
                                                        } else {
                                                            UDetails.delete();
                                                            authResultTask.getResult().getUser().reauthenticate(EmailAuthProvider.getCredential(txt_email, txt_password)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    authResultTask.getResult().getUser().delete();
                                                                }
                                                            });
                                                            emailTextInput.setError(task.getException().toString());
                                                        }
                                                    }
                                                });
                                            } else if (txt_type.toUpperCase().equals("STUDENT")) {
                                                Map<String, Object> studentDetails = new HashMap<>();
                                                Map<String, Object> classes = new HashMap<>();
                                                studentDetails.put("userDetails", UDetails);
                                                studentDetails.put("Classes", classes);
                                                FirebaseFirestore.getInstance().collection("Students").document(Uid).set(studentDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            emailEditText.setText(txt_email + "  Student Created");
                                                        } else {
                                                            UDetails.delete();
                                                            authResultTask.getResult().getUser().reauthenticate(EmailAuthProvider.getCredential(txt_email, txt_password)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    authResultTask.getResult().getUser().delete();
                                                                }
                                                            });
                                                            emailTextInput.setError(task.getException().toString());
                                                        }
                                                    }
                                                });
                                            }
                                        } else {
                                            authResultTask.getResult().getUser().reauthenticate(EmailAuthProvider.getCredential(txt_email, txt_password)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    authResultTask.getResult().getUser().delete();
                                                }
                                            });
                                            emailTextInput.setError(task.getException().toString());
                                        }
                                    }
                                });
                            }
                        }
                    });
                } else {
                    emailTextInput.setError(authResultTask.getException().toString());
                }
            }
        });
    }
}