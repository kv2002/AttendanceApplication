package com.swelab.attendanceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class AdminMyProfileFragment extends Fragment {

    private Button signOut;
    private Button changeName;
    private Button resetPassword;
    private TextView name;
    private TextView email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        changeName = view.findViewById(R.id.change_name);
        signOut = view.findViewById(R.id.sign_out);
        resetPassword = view.findViewById(R.id.reset_password);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        FirebaseFirestore.getInstance().collection("users").document(currUser.getFirebaseUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                name.setText(value.get("name").toString());
                email.setText(value.get("email").toString());
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyProfileDialog dialog = new MyProfileDialog();
                dialog.show(getParentFragmentManager(), "");
            }
        });
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetPasswordDialog dialog = new ResetPasswordDialog();
                dialog.show(getParentFragmentManager(), "");
            }
        });
        return view;
    }
}