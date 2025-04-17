package com.fosterx.crewride.ui.messages;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fosterx.crewride.R;
import com.fosterx.crewride.obj.MessageResponse;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private Context context;
    private List<MessageResponse> messageList;
    private String currentEmployeeId;

    public ChatMessageAdapter(Context context, List<MessageResponse> messageList, String currentEmployeeId) {
        this.context = context;
        this.messageList = messageList;
        this.currentEmployeeId = currentEmployeeId;
    }

    @Override
    public int getItemViewType(int position) {
        MessageResponse message = messageList.get(position);
        // Assume message.getSenderId() returns the sender's ID.
        Log.d("TAG", message.toString());
        if (message.getSenderId().equals(currentEmployeeId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageResponse message = messageList.get(position);
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).bind(message);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void updateData(List<MessageResponse> messages) {
        this.messageList = messages;
        notifyDataSetChanged();
    }

    // ViewHolder for Sent Messages
    public class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
        }

        void bind(MessageResponse message) {
            messageText.setText(message.getContent());
            timeText.setText(message.getCreatedAt()); // You can format it here if needed.
        }
    }

    // ViewHolder for Received Messages
    public class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, senderName;
        ImageView profileImage;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            senderName = itemView.findViewById(R.id.text_message_name);
            profileImage = itemView.findViewById(R.id.image_message_profile);
        }

        void bind(MessageResponse message) {
            messageText.setText(message.getContent());
            timeText.setText(message.getCreatedAt());
            senderName.setText(message.getSenderId()); // Optionally, use sender name if available.

            Glide.with(context).load(message.getSenderProfileUrl())
                  .apply(RequestOptions.circleCropTransform())
                  .placeholder(R.drawable.ic_person_placeholder)
                  .into(profileImage);
        }
    }
}