package com.swelab.attendanceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginScreenActivity extends AppCompatActivity {

    private MaterialButton login;
    private TextInputLayout emailTextInput;
    private TextInputEditText emailEditText;
    private TextInputLayout passwordTextInput;
    private TextInputEditText passwordEditText;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        login = findViewById(R.id.login_button);
        passwordTextInput = findViewById(R.id.password_text_input);
        passwordEditText = findViewById(R.id.password_edit_text);
        emailTextInput = findViewById(R.id.email_text_input);
        emailEditText = findViewById(R.id.email_edit_text);
        Bundle extras = getIntent().getExtras();
        String loginError;
        if (extras != null) {
            loginError = extras.getString("login fail error");
            passwordTextInput.setError(loginError);
        }

        Spinner typeOfUser = findViewById(R.id.TypeOfUser);
        mAuth = FirebaseAuth.getInstance();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.TypeOfUser, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        typeOfUser.setAdapter(adapter);
        login.setOnClickListener(new View.OnClickListener() {
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
                    loginUser(txt_email, txt_password, txt_type);
                }
            }
        });

    }

    private void loginUser(String txt_email, String txt_password, String txt_type) {
        mAuth.signInWithEmailAndPassword(txt_email, txt_password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> authResultTask) {
                if (authResultTask.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference users = db.collection("users");
                    FirebaseUser user = mAuth.getCurrentUser();
                    users.whereEqualTo("userID", user.getUid()).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> dbTask) {
                                    boolean isCorrectType = false;
                                    if (dbTask.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : dbTask.getResult()) {
                                            if (document.getData().get("userType").equals(txt_type.toUpperCase())) {
                                                isCorrectType = true;
                                            }
                                        }
                                        if (isCorrectType) {
                                            currUser.setFirebaseUser(user);
                                            currUser.setEmail(txt_email);
                                            currUser.setPass(txt_password);
                                            Toast.makeText(LoginScreenActivity.this, "login successful", Toast.LENGTH_SHORT).show();
                                            if (txt_type.equalsIgnoreCase("ADMIN")) {
                                                startActivity(new Intent(LoginScreenActivity.this, AdminDefaultActivity.class));
                                            } else if (txt_type.equalsIgnoreCase("STUDENT")) {
                                                startActivity(new Intent(LoginScreenActivity.this, StudentDefaultActivity.class));
                                            } else if (txt_type.equalsIgnoreCase("TEACHER")) {
                                                startActivity(new Intent(LoginScreenActivity.this, TeacherDefaultActivity.class));
                                            }
                                        } else {
                                            Toast.makeText(LoginScreenActivity.this, "Incorrect type of user selected,Retry", Toast.LENGTH_SHORT).show();
                                            mAuth.signOut();
                                            Intent loginFailIntent = new Intent(LoginScreenActivity.this, LoginScreenActivity.class);
                                            loginFailIntent.putExtra("login fail error", "Incorrect type of user selected,Retry");
                                            startActivity(loginFailIntent);
                                        }
                                    } else {
                                        mAuth.signOut();
                                        Toast.makeText(LoginScreenActivity.this, dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        Intent loginFailIntent = new Intent(LoginScreenActivity.this, LoginScreenActivity.class);
                                        loginFailIntent.putExtra(dbTask.getException().getMessage(), authResultTask.getException().getMessage());
                                        startActivity(loginFailIntent);
                                    }
                                }
                            });
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(LoginScreenActivity.this, "login failed", Toast.LENGTH_SHORT).show();
                    Intent loginFailIntent = new Intent(LoginScreenActivity.this, LoginScreenActivity.class);
                    loginFailIntent.putExtra("login fail error", authResultTask.getException().getMessage());
                    startActivity(loginFailIntent);
                }
                finish();
            }
        });
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}