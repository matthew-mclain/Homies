package com.example.homies.ui.messages;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homies.MyApplication;
import com.example.homies.R;
import com.example.homies.model.GroceryItem;
import com.example.homies.model.GroupChat;
import com.example.homies.model.Message;
import com.example.homies.model.viewmodel.GroupChatViewModel;
import com.example.homies.model.viewmodel.HouseholdViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class MessagesFragment extends Fragment implements View.OnClickListener {
    View view;
    private Button sendButton;
    private EditText messageEditText;
    private RecyclerView recyclerViewChat;
    private ArrayList<Message> messagesList = new ArrayList<>();
    private ChatAdapter adapter;
    private HouseholdViewModel householdViewModel;
    private GroupChatViewModel groupChatViewModel;
    private static final String TAG = MessagesFragment.class.getSimpleName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_messages, container, false);
        Timber.tag(TAG).d("onCreateView()");

        sendButton = view.findViewById(R.id.buttonSendMessage);
        messageEditText = view.findViewById(R.id.editTextMessage);
        recyclerViewChat = view.findViewById(R.id.recyclerViewChat);

        // Create an instance of the ChatAdapter
        String currentUserId = FirebaseAuth.getInstance().getUid();
        adapter = new ChatAdapter(messagesList, currentUserId);

        // Set the layout manager for the RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setStackFromEnd(true); // Set stackFromEnd property to true
        recyclerViewChat.setLayoutManager(layoutManager);

        // Set the adapter for the RecyclerView
        recyclerViewChat.setAdapter(adapter);

        // Set click listener for the send button
        sendButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.tag(TAG).d("onViewCreated()");

        // Initialize the ViewModel instances
        householdViewModel = new ViewModelProvider(requireActivity()).get(HouseholdViewModel.class);
        groupChatViewModel = new ViewModelProvider(this).get(GroupChatViewModel.class);

        // Check if network connection exists
        if (MyApplication.hasNetworkConnection(requireContext())) {
            // Observe the selected household LiveData
            householdViewModel.getSelectedHousehold(requireContext()).observe(getViewLifecycleOwner(), household -> {
                if (household != null) {
                    Timber.tag(TAG).d("Selected household observed: %s", household.getHouseholdId());
                    // Fetch messages for the selected household's group chat
                    groupChatViewModel.getMessagesForGroupChat(household.getHouseholdId());
                } else {
                    Timber.tag(TAG).d("No household selected.");
                }
            });

            // Observe the messages LiveData from GroupChatViewModel
            groupChatViewModel.getSelectedMessages().observe(getViewLifecycleOwner(), messages -> {
                if (messages != null && !messages.isEmpty()) {
                    Timber.tag(TAG).d("Received messages: %s", messages.size());
                    // Clear existing messages and add the new messages to the adapter
                    messagesList.clear();
                    messagesList.addAll(messages);
                    adapter.notifyDataSetChanged();

                    // Scroll RecyclerView to the last position after updating the adapter
                    recyclerViewChat.scrollToPosition(adapter.getItemCount() - 1);
                } else {
                    // Handle the case where no messages are available
                    Timber.tag(TAG).d("No messages available.");
                }
            });
        } else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonSendMessage) {
            if (MyApplication.hasNetworkConnection(requireContext())) {
                String senderUserId = FirebaseAuth.getInstance().getUid();
                String messageContent = messageEditText.getText().toString();
                long timestamp = System.currentTimeMillis();

                if (!messageContent.trim().isEmpty()) {
                    groupChatViewModel.addMessage(senderUserId, messageContent, timestamp);

                    // Clear the messageEditText after sending the message
                    messageEditText.getText().clear();
                } else {
                    // Handle the case where the message content is empty
                    Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Message> messages;
        private String currentUserId;
        private static final int VIEW_TYPE_SENT = 1;
        private static final int VIEW_TYPE_RECEIVED = 2;

        public ChatAdapter(List<Message> messages, String currentUserId) {
            this.messages = messages;
            this.currentUserId = currentUserId;
        }

        @Override
        public int getItemViewType(int position) {
            Message message = messages.get(position);
            if (message.isSentByCurrentUser(currentUserId)) {
                return VIEW_TYPE_SENT;
            } else {
                return VIEW_TYPE_RECEIVED;
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view;
            switch (viewType) {
                case VIEW_TYPE_SENT:
                    view = inflater.inflate(R.layout.item_message_sent, parent, false);
                    return new SentMessageViewHolder(view);
                case VIEW_TYPE_RECEIVED:
                    view = inflater.inflate(R.layout.item_message_received, parent, false);
                    return new ReceivedMessageViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Message message = messages.get(position);
            if (holder instanceof SentMessageViewHolder) {
                ((SentMessageViewHolder) holder).bind(message);
            } else if (holder instanceof ReceivedMessageViewHolder) {
                ((ReceivedMessageViewHolder) holder).bind(message);
            }
        }

        private static class SentMessageViewHolder extends RecyclerView.ViewHolder {
            TextView displayName, message, timestamp;

            SentMessageViewHolder(View itemView) {
                super(itemView);
                displayName = itemView.findViewById(R.id.textViewDisplayNameSent);
                message = itemView.findViewById(R.id.textViewMessageSent);
                timestamp = itemView.findViewById(R.id.textViewTimestampSent);
            }

            void bind(Message message) {
                message.getSenderDisplayName(displayName -> {
                    if (displayName != null) {
                        this.displayName.setText(displayName);
                        this.message.setText(message.getMessageContent());

                        // Format timestamp to a human-readable date and time with AM/PM indicators
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
                        String formattedDate = dateFormat.format(new Date(message.getTimestamp()));

                        timestamp.setText(formattedDate);
                    }
                });
            }
        }

        private static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
            TextView displayName, message, timestamp;

            ReceivedMessageViewHolder(View itemView) {
                super(itemView);
                displayName = itemView.findViewById(R.id.textViewDisplayNameReceived);
                message = itemView.findViewById(R.id.textViewMessageReceived);
                timestamp = itemView.findViewById(R.id.textViewTimestampReceived);
            }

            void bind(Message message) {
                message.getSenderDisplayName(displayName -> {
                    if (displayName != null) {
                        this.displayName.setText(displayName);
                        this.message.setText(message.getMessageContent());

                        // Format timestamp to a human-readable date and time with AM/PM indicators
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
                        String formattedDate = dateFormat.format(new Date(message.getTimestamp()));

                        timestamp.setText(formattedDate);
                    }
                });
            }
        }
    }
}