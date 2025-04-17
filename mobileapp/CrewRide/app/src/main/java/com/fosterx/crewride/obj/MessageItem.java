package com.fosterx.crewride.obj;

import org.json.JSONObject;

public class MessageItem {
    public String senderId;
    public String content;

    public MessageItem(String senderId, String content) {
        this.senderId = senderId;
        this.content = content;
    }

    public MessageItem(JSONObject json) {
        this.senderId = json.optString("senderId");
        this.content = json.optString("message");
    }
}
