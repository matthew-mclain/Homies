package com.example.homies.model;

public class Message {
    private String senderUserId;
    private String messageContent;
    private long timestamp;

    public Message() {
    }

    public Message(String senderUserId, String messageContent, long timestamp) {
        this.senderUserId = senderUserId;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
    }

    public String getSenderId() {
        return senderUserId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
