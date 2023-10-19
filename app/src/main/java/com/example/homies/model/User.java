package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.firestore.FirebaseFirestore;

public class User {
    private String userId;
    private String email;
    private String displayName;
    private static FirebaseFirestore db;

    public User(String userId, String email, String displayName) {
        this.userId = userId;
        this.email = email;
        this.displayName = displayName;
    }

    // Getter and setter methods
    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static void getUsers(String userId) {
        db = MyApplication.getDbInstance();
        db.collection("users")
                .document(userId)
                .get();
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public static void createUser (String userId, String email) {
        User user = new User(userId, email, null);

        db = MyApplication.getDbInstance();
        db.collection("users")
                .document(userId)
                .set(user);
    }

    public static void deleteUser (String userId, String email) {
        db = MyApplication.getDbInstance();
        db.collection("users")
                .document(userId)
                .delete();
    }
}
