package com.example.homies.model;

import com.example.homies.MainActivity;
import com.example.homies.MyApplication;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Message {
    private String senderUserId;
    private String messageContent;
    private long timestamp;
    private static FirebaseFirestore db;
    private static final String TAG = Message.class.getSimpleName();

    public Message() {
    }

    public Message(String senderUserId, String messageContent, long timestamp) {
        this.senderUserId = senderUserId;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
    }

    public String getSenderUserId() {
        return senderUserId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public interface DisplayNameCallback {
        void onCallback(String displayName);
    }

    public void getSenderDisplayName(DisplayNameCallback callback) {
        // Query Firestore to fetch the display name using senderUserId
        db = MyApplication.getDbInstance();
        db.collection("users")
                .document(senderUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String displayName = documentSnapshot.getString("displayName");
                        if (displayName != null) {
                            callback.onCallback(displayName);
                        }
                    } else {
                        // Handle the case where the document does not exist
                        callback.onCallback(null);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the failure scenario
                    callback.onCallback(null);
                });
    }


    public boolean isSentByCurrentUser(String currentUserId) {
        return senderUserId.equals(currentUserId);
    }
}
