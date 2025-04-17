package com.fosterx.crewride.ui.messages;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fosterx.crewride.MainActivity;
import com.fosterx.crewride.R;
import com.fosterx.crewride.network.ApiClient;
import com.fosterx.crewride.network.ApiService;
import com.fosterx.crewride.obj.MessageRequest;
import com.fosterx.crewride.obj.MessageResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoomDetailFragment extends Fragment {

    private TextView viewTitle;
    private RecyclerView recyclerMessages;
    private ChatMessageAdapter adapter;
    private List<MessageResponse> messageList = new ArrayList<>();
    private TextInputLayout messageInputLayout;
    private TextInputEditText editMessage;
    private MaterialButton btnSendMessage;
    private String rideId;
    // Variable to track the timestamp from where to fetch newer messages.
    private String lastMessageTime = "1970-01-01T00:00:00.000Z";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_room, container, false);

        viewTitle = view.findViewById(R.id.view_title);
        viewTitle.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new MessagesFragment());

        });
        // Set up RecyclerView and adapter
        recyclerMessages = view.findViewById(R.id.recycler_messages);
        recyclerMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatMessageAdapter(getContext(), messageList, ((MainActivity) getActivity()).getUser().getEmployeeId());
        recyclerMessages.setAdapter(adapter);

        // Load some dummy messages
        loadDummyMessages();

        // Get message input and send button
        editMessage = view.findViewById(R.id.edit_message);
        messageInputLayout = view.findViewById(R.id.message_input_container);
        messageInputLayout.setEndIconOnClickListener(v -> {
            String message = ((TextInputEditText) view.findViewById(R.id.edit_message)).getText().toString().trim();
            if (message.isEmpty()){
                Toast.makeText(getContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
            } else {
                // Handle send message action here
                sendMessage(message);
            }
        });

        // Assume rideId, senderId and receiverId are passed as arguments
        rideId = getArguments().getString("rideId");

        // Handle send button click
        /*
        btnSendMessage.setOnClickListener(v -> {
            String content = editMessage.getText().toString().trim();
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(getContext(), "Enter a message", Toast.LENGTH_SHORT).show();
                return;
            }

            // Simulate sending a message
            MessageResponse newMsg = new MessageResponse();
            newMsg.setReceiverId(((MainActivity)getActivity()).getUser().employeeId);
            // For demonstration, assume the receiver is the driver with ID "EMP876"
            newMsg.setReceiverId("EMP876");
            newMsg.setContent(content);
            newMsg.setCreatedAt(getCurrentTimestamp());

            // Add the message to the list and update adapter
            messageList.add(newMsg);
            adapter.notifyItemInserted(messageList.size() - 1);
            recyclerMessages.scrollToPosition(messageList.size() - 1);
            editMessage.setText("");

            // Hide keyboard after sending
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
        */

        /*
        // Load existing messages from API
        loadMessageHistory();

        // Setup send button click listener
        btnSendMessage.setOnClickListener(v -> {
            // Hide keyboard
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            String content = editMessage.getText().toString().trim();
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(getContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
                return;
            }
            sendMessage(content);
        });
        */

        return view;
    }

    private void loadDummyMessages() {
        // Add some dummy messages for conversation history
        MessageResponse msg1 = new MessageResponse();
        msg1.setRideId("0419bc7d-9d3c-4f89-be29-41d5ee818614");
        msg1.setSenderId("EMP876"); // Driver
        msg1.setReceiverId("EMP038");
        msg1.setContent("Hello, I am on my way. See you soon!");
        msg1.setCreatedAt("23:47");

        MessageResponse msg2 = new MessageResponse();
        msg2.setRideId("0419bc7d-9d3c-4f89-be29-41d5ee818614");
        msg2.setSenderId("EMP876"); // Driver
        msg2.setReceiverId("EMP038");
        msg2.setContent("Great, I'll wait for you here.");
        msg2.setCreatedAt("23:48");

        MessageResponse msg3 = new MessageResponse();
        msg3.setRideId("0419bc7d-9d3c-4f89-be29-41d5ee818614");
        msg3.setReceiverId("EMP876"); // Driver
        msg3.setSenderId("EMP038");
        msg3.setContent("Okay, see you soon.");
        msg3.setCreatedAt("23:49");

        messageList.add(msg1);
        messageList.add(msg2);
        messageList.add(msg3);

        adapter.notifyDataSetChanged();

        // Scroll to the last message
        recyclerMessages.scrollToPosition(messageList.size() - 1);
    }

    private String getCurrentTimestamp() {
        // Return current time in a simple hour:minute format; change this as needed.
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * Loads the message history for the current ride.
     * The API call retrieves messages after the lastMessageTime.
     */
    private void loadMessageHistory() {
        ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        apiService.getMessages(rideId, lastMessageTime).enqueue(new Callback<List<MessageResponse>>() {
            @Override
            public void onResponse(Call<List<MessageResponse>> call, Response<List<MessageResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MessageResponse> messages = response.body();
                    if (!messages.isEmpty()) {
                        // Update lastMessageTime based on the timestamp of the newest message.
                        // (Assuming MessageResponse has a getCreatedAt() that returns an ISO string.)
                        lastMessageTime = messages.get(messages.size() - 1).getCreatedAt();
                        messageList.clear();
                        messageList.addAll(messages);
                        adapter.notifyDataSetChanged();
                        // Scroll to the bottom
                        recyclerMessages.scrollToPosition(messageList.size() - 1);
                    }
                } else {
                    Toast.makeText(getContext(), "Error loading messages", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MessageResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load messages: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sends a message to the current ride chat room using the API.
     */
    private void sendMessage(String content) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Sending message...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        MessageRequest request = new MessageRequest();
        request.setRideId(rideId);
        request.setSenderId(((MainActivity)getActivity()).getUser().employeeId);
        request.setContent(content);

        ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        apiService.sendMessage(request).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    // Clear the input
                    editMessage.setText("");
                    // Optionally add the sent message to the list immediately
                    messageList.add(response.body());
                    adapter.notifyItemInserted(messageList.size() - 1);
                    recyclerMessages.scrollToPosition(messageList.size() - 1);
                } else {
                    Toast.makeText(getContext(), "Error sending message", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Failed to send message: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}