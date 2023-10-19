package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.firestore.FirebaseFirestore;
import timber.log.Timber;

public class User {
    private String userId;
    private String email;
    private String displayName;
    private static FirebaseFirestore db;
    private static final String TAG = User.class.getSimpleName();

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
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // User data retrieved successfully
                        Timber.tag(TAG).d("User data retrieved: %s", documentSnapshot.getData());
                    } else {
                        // User document does not exist
                        Timber.tag(TAG).w("User document does not exist for user ID: %s", userId);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).e(e, "Error getting user data for user ID: %s", userId);
                });
    }

    public void setDisplayName(String displayName) {
        db = MyApplication.getDbInstance();
        db.collection("users")
                .document(userId)
                .update("displayName", displayName)
                .addOnSuccessListener(aVoid -> {
                    // Display name updated successfully
                    Timber.tag(TAG).d("Display name updated to: %s", displayName);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).e(e, "Error updating display name for user ID: %s", userId);
                });
    }

    public static void createUser (String userId, String email) {
        User user = new User(userId, email, null);

        db = MyApplication.getDbInstance();
        db.collection("users")
                .document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    // User created successfully
                    Timber.tag(TAG).d("User created successfully for user ID: %s", userId);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).e(e, "Error creating user for user ID: %s", userId);
                });
    }

    public static void deleteUser (String userId, String email) {
        db = MyApplication.getDbInstance();
        db.collection("users")
                .document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // User deleted successfully
                    Timber.tag(TAG).d("User deleted successfully for user ID: %s", userId);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).e(e, "Error deleting user for user ID: %s", userId);
                });
    }
}
