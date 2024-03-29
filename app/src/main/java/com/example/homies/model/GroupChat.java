package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import timber.log.Timber;

public class GroupChat {
    private String householdId;
    private static FirebaseFirestore db;
    private static final String TAG = GroupChat.class.getSimpleName();

    public GroupChat() {
    }

    public GroupChat(String householdId) {
        this.householdId = householdId;
    }

    public String getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(String householdId) {
        this.householdId = householdId;
    }

    public static void createGroupChat(String householdId) {
        GroupChat groupChat = new GroupChat(householdId);

        db = MyApplication.getDbInstance();
        db.collection("group_chats")
                .add(groupChat)
                .addOnSuccessListener(documentReference -> {
                    Timber.tag(TAG).d("Group chat created successfully: %s", documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).e(e, "Error creating group chat");
                });
    }
}
