package com.fosterx.crewride.obj;

public class ChatRoomHeaderItem implements ChatRoomListItem {
    private String header;

    public ChatRoomHeaderItem(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Override
    public int getItemType() {
        return TYPE_HEADER;
    }
}