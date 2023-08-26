package com.bscs.resellingapp.model;

public class ChatMessage {
    private String message, messageId;
    private String uId;
    private long timestamp;


    public ChatMessage(String message, String messageId, String uId, long timestamp) {

        this.message = message;
        this.messageId = messageId;
        this.uId = uId;
        this.timestamp = timestamp;

    }

    public ChatMessage(String uId , String message) {
        this.uId = uId;
        this.message= message;
    }
    public ChatMessage(){

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
