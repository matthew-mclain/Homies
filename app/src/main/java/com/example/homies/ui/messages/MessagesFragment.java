package com.example.homies.ui.messages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import com.example.homies.R;
import com.example.homies.model.GroupChat;
import com.example.homies.model.Message;
import com.example.homies.model.viewmodel.GroupChatViewModel;
import com.example.homies.model.viewmodel.HouseholdViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MessagesFragment extends Fragment implements View.OnClickListener {
    View view;
    private Button sendButton;
    private EditText messageEditText;
    private RecyclerView recyclerViewChat;
    private ArrayList<String> chatMessagesList = new ArrayList<>();
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
        adapter = new ChatAdapter(chatMessagesList);

        // Set the adapter for the RecyclerView
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(requireContext()));
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

        // Observe the selected household LiveData
        householdViewModel.getSelectedHousehold().observe(getViewLifecycleOwner(), household -> {
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
                chatMessagesList.clear();
                for (Message message : messages) {
                    Timber.tag(TAG).d("Adding message: %s", message.getMessageContent());
                    chatMessagesList.add(message.getMessageContent());
                }
                adapter.notifyDataSetChanged();
            } else {
                // Handle the case where no messages are available
                Timber.tag(TAG).d("No messages available.");
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonSendMessage) {
            String senderUserId = FirebaseAuth.getInstance().getUid();
            String messageContent = messageEditText.getText().toString();
            long timestamp = System.currentTimeMillis();

            if (!messageContent.trim().isEmpty()) {
                groupChatViewModel.addMessage(senderUserId, messageContent, timestamp);
                // Clear the messageEditText after sending the message
                messageEditText.setText("");
            } else {
                // Handle the case where the message content is empty
                Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
        private List<String> chatMessages;

        public ChatAdapter(List<String> chatMessages) {
            this.chatMessages = chatMessages;
        }

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Create and return a new ChatViewHolder instance
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            // Bind data to the ChatViewHolder's views
            String message = chatMessages.get(position);
            holder.messageTextView.setText(message);
        }

        @Override
        public int getItemCount() {
            return chatMessages.size();
        }

        public static class ChatViewHolder extends RecyclerView.ViewHolder {
            TextView messageTextView;

            public ChatViewHolder(@NonNull View itemView) {
                super(itemView);
                messageTextView = itemView.findViewById(R.id.textViewMessage);
            }
        }
    }
}
