package com.fosterx.crewride.ui.messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fosterx.crewride.R;
import com.fosterx.crewride.obj.ChatRoomHeaderItem;
import com.fosterx.crewride.obj.ChatRoomItem;
import com.fosterx.crewride.obj.ChatRoomListItem;

import java.util.List;

public class ChatRoomListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemClickListener {
        void onChatRoomClick(ChatRoomItem item);
    }

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private Context context;
    private List<ChatRoomListItem> items;
    private OnItemClickListener listener;

    public ChatRoomListAdapter(Context context, List<ChatRoomListItem> items, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof ChatRoomHeaderItem) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_room_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_room, parent, false);
            return new ChatRoomViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind((ChatRoomHeaderItem) items.get(position));
        } else {
            ((ChatRoomViewHolder) holder).bind((ChatRoomItem) items.get(position), listener);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateData(List<ChatRoomListItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    // Header ViewHolder
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvHeader;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.text_header);
        }

        public void bind(ChatRoomHeaderItem header) {
            tvHeader.setText(header.getHeader());
        }
    }

    // Chat Room Item ViewHolder
    public static class ChatRoomViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDriverName;
        private TextView tvFromTo;
        private TextView tvTime;

        public ChatRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDriverName = itemView.findViewById(R.id.text_driver_name);
            tvFromTo = itemView.findViewById(R.id.text_from_to);
            tvTime = itemView.findViewById(R.id.text_time);
        }

        public void bind(ChatRoomItem item, OnItemClickListener listener) {
            tvDriverName.setText(item.getDriverName());
            // Combine From and To with an arrow symbol
            tvFromTo.setText(item.getFromAddress() + " → " + item.getToAddress());
            tvTime.setText(item.getDepartureTime());
            itemView.setOnClickListener(v -> listener.onChatRoomClick(item));
        }
    }
}