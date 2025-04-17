package com.fosterx.crewride.obj;

public class MessageRequest {
    private String rideId;
    private String senderId;
    private String receiverId;
    private String content;

    public MessageRequest() {
    }

    public MessageRequest(String rideId, String senderId, String receiverId, String content) {
        this.rideId = rideId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "MessageRequest{" +
                "rideId='" + rideId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}