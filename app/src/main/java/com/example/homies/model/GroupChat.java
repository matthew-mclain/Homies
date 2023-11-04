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

    public void addMessage(String senderUserId, String messageContent, long timestamp) {
        if (householdId != null) {
            // Create a new message
            Message message = new Message(senderUserId, messageContent, timestamp);

            // Add the new message to the "messages" subcollection within the group chat
            db = MyApplication.getDbInstance();
            db.collection("group_chats")
                    .whereEqualTo("householdId", householdId)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String groupChatId = documentSnapshot.getId();

                            // Add the new message to the "messages" subcollection
                            db.collection("group_chats")
                                    .document(groupChatId)
                                    .collection("messages")
                                    .add(message)
                                    .addOnSuccessListener(documentReference -> {
                                        // Message added to the subcollection successfully
                                        Timber.tag(TAG).d("Message added to subcollection successfully: %s", documentReference.getId());
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle errors
                                        Timber.tag(TAG).e(e, "Failed to add message to subcollection: %s", householdId);
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle errors
                        Timber.tag(TAG).e(e, "Failed to query group chats: %s", householdId);
                    });

        } else {
            // Handle the case where no household is selected
            Timber.tag(TAG).d("No household selected.");
        }
    }
}
