package com.fosterx.crewride.obj;

public class MessageResponse {
    private String id; // message id if available
    private String rideId;
    private String senderId;
    private String receiverId;
    private String content;
    private String createdAt; // ISO 8601 timestamp

    private String senderProfileUrl;

    public MessageResponse() {
    }

    public MessageResponse(String id, String rideId, String senderId, String receiverId, String content, String createdAt, String senderProfileUrl) {
        this.id = id;
        this.rideId = rideId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.createdAt = createdAt;
        this.senderProfileUrl = senderProfileUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getSenderProfileUrl() {
        return senderProfileUrl;
    }

    public void setSenderProfileUrl(String senderProfileUrl) {
        this.senderProfileUrl = senderProfileUrl;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
                "id='" + id + '\'' +
                ", rideId='" + rideId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", content='" + content + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", senderProfileUrl='" + senderProfileUrl + '\'' +
                '}';
    }
}