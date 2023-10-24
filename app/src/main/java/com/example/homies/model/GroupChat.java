package com.example.homies.model;

import com.example.homies.MyApplication;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

import timber.log.Timber;

public class GroupChat {
    private String householdId;
    private String groupChatId;
    private static FirebaseFirestore db;
    private static final String TAG = User.class.getSimpleName();

    public GroupChat(String householdId) {
        this.householdId = householdId;
    }

    public void setGroupChatId(String groupChatId) {
        this.groupChatId = groupChatId;
    }

    public interface OnComponentCreatedListener {
        void onComponentCreated(String componentId);
        void onComponentCreationFailed(Exception e);
    }

    public static void createGroupChat(String householdId, OnComponentCreatedListener listener) {
        GroupChat groupChat = new GroupChat(householdId);

        db = MyApplication.getDbInstance();
        db.collection("group_chats")
                .add(groupChat)
                .addOnSuccessListener(documentReference -> {
                    // Household created successfully
                    String groupChatId = documentReference.getId();
                    groupChat.setGroupChatId(groupChatId);

                    Timber.tag(TAG).d("Group chat created successfully: %s", groupChatId);

                    // Invoke the listener to notify that the group chat has been created,
                    // and pass the generated groupChatId.
                    listener.onComponentCreated(groupChatId);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Timber.tag(TAG).e(e, "Error creating group chat");

                    // Invoke the listener to notify about the failure.
                    listener.onComponentCreationFailed(e);
                });
    }
}
