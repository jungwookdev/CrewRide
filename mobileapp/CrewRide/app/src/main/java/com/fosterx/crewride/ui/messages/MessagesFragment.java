package com.fosterx.crewride.ui.messages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fosterx.crewride.MainActivity;
import com.fosterx.crewride.R;
import com.fosterx.crewride.obj.ChatRoomHeaderItem;
import com.fosterx.crewride.obj.ChatRoomItem;
import com.fosterx.crewride.obj.ChatRoomListItem;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends Fragment {

    private RecyclerView chatRoomsRecycler;
    private ChatRoomListAdapter adapter;
    private List<ChatRoomListItem> chatRoomItems = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        chatRoomsRecycler = view.findViewById(R.id.chat_rooms_recycler);
        chatRoomsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ChatRoomListAdapter(getContext(), chatRoomItems, new ChatRoomListAdapter.OnItemClickListener() {
            @Override
            public void onChatRoomClick(ChatRoomItem item) {
                // Navigate to ChatRoomDetailFragment, passing along the chat room info (e.g., rideId)
                Bundle args = new Bundle();
                args.putString("rideId", item.getRideId());
                // Also, pass senderId and receiverId as needed.
                ChatRoomDetailFragment detailFragment = new ChatRoomDetailFragment();
                detailFragment.setArguments(args);
                ((MainActivity)getActivity()).loadFragment(detailFragment);
            }
        });
        chatRoomsRecycler.setAdapter(adapter);

        loadDummyChatRooms();

        return view;
    }
    private void loadDummyChatRooms() {
        chatRoomItems.clear();

        chatRoomItems.add(new ChatRoomHeaderItem("August 1, 2023"));
        chatRoomItems.add(new ChatRoomItem("0419bc7d-9d3c-4f89-be29-41d5ee818614",
                "Emily Lopez", "Company HQ", "225, Iris Street, Redwood City",
                "23:47", "2023-08-01"));
        chatRoomItems.add(new ChatRoomItem("b2d4c7a1-4e9a-4b2f-a36b-123456789abc",
                "Sandra Martinez", "Office", "Downtown", "15:30", "2023-08-01"));

        chatRoomItems.add(new ChatRoomHeaderItem("August 2, 2023"));
        chatRoomItems.add(new ChatRoomItem("e7f8a9d0-2c3b-4d1e-9a6b-987654321def",
                "Michael Scott", "Scranton Branch", "Warehouse", "09:15", "2023-08-02"));

        adapter.updateData(chatRoomItems);
    }
}