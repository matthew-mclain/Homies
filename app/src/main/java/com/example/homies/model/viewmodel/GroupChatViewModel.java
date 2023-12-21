package com.example.homies.model.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.homies.MyApplication;
import com.example.homies.model.GroupChat;
import com.example.homies.model.Message;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class GroupChatViewModel extends ViewModel {
    private MutableLiveData<GroupChat> selectedGroupChat = new MutableLiveData<>();

    private MutableLiveData<List<Message>> selectedMessages = new MutableLiveData<>();

    private static FirebaseFirestore db;

    private static final String TAG = GroupChatViewModel.class.getSimpleName();

    public LiveData<List<Message>> getSelectedMessages() {
        return selectedMessages;
    }

    public void setSelectedGroupChat(GroupChat groupChat) {
        selectedGroupChat.setValue(groupChat);
    }

    public void setSelectedMessages(List<Message> messages) {
        selectedMessages.setValue(messages);
    }

    public void getMessagesForGroupChat(String householdId) {
        db = MyApplication.getDbInstance();
        db.collection("group_chats")
                .whereEqualTo("householdId", householdId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Group chat document found
                            GroupChat groupChat = document.toObject(GroupChat.class);
                            setSelectedGroupChat(groupChat);
                            String groupChatId = document.getId();
                            Timber.tag(TAG).d("Group chat found with ID: %s", groupChatId);
                            fetchMessagesFromSubcollection(groupChatId);
                            return;
                        }
                        // Group chat not found with the given householdId
                        Timber.tag(TAG).w("No group chat found for householdId: %s", householdId);
                    } else {
                        // Handle failures
                        Timber.tag(TAG).e(task.getException(), "Error fetching group chat for householdId: %s", householdId);
                    }
                });
    }

    private void fetchMessagesFromSubcollection(String groupChatId) {
        db = MyApplication.getDbInstance();
        db.collection("group_chats")
                .document(groupChatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(messagesTask -> {
                    if (messagesTask.isSuccessful()) {
                        // Messages fetched successfully, handle them
                        Timber.tag(TAG).d("Messages fetched successfully for group chat ID: %s", groupChatId);
                        List<Message> messages = new ArrayList<>();
                        for (QueryDocumentSnapshot messageDocument : messagesTask.getResult()) {
                            Message message = messageDocument.toObject(Message.class);
                            messages.add(message);
                        }
                        setSelectedMessages(messages);
                    } else {
                        // Handle failures
                        Timber.tag(TAG).e(messagesTask.getException(), "Error fetching messages for group chat ID: %s", groupChatId);
                        selectedMessages.setValue(null);
                    }
                });
    }

    public void addMessage(String senderUserId, String messageContent, long timestamp) {
        GroupChat groupChat = selectedGroupChat.getValue();
        String householdId = groupChat.getHouseholdId();
        if (householdId != null) {
            Message message = new Message(senderUserId, messageContent, timestamp);

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

                                        // Update selectedMessages LiveData with the new message
                                        List<Message> currentMessages = selectedMessages.getValue();
                                        if (currentMessages == null) {
                                            currentMessages = new ArrayList<>();
                                        }
                                        currentMessages.add(message);
                                        selectedMessages.setValue(currentMessages);
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
