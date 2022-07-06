package com.swelab.attendanceapplication;

import com.google.firebase.auth.FirebaseUser;

public class currUser {
    private static FirebaseUser firebaseUser;
    private static String pass;
    private static String email;

    public static String getPass() {
        return pass;
    }

    public static void setPass(String pass) {
        currUser.pass = pass;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        currUser.email = email;
    }

    public static FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public static void setFirebaseUser(FirebaseUser firebaseUser) {
        currUser.firebaseUser = firebaseUser;
    }
}
