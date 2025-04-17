package com.fosterx.crewride.obj;


public interface ChatRoomListItem {
    int TYPE_HEADER = 0;
    int TYPE_ITEM = 1;

    /**
     * Returns the type of this list item.
     * @return TYPE_HEADER for header items, or TYPE_ITEM for chat room items.
     */
    int getItemType();
}