package com.ersinkoc.hopol.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class chatModel {

    private String id, message, senderID;

    @ServerTimestamp
    private Date time;


    public chatModel() {
    }

    public chatModel(String id, String message, String senderID, Date time) {
        this.id = id;
        this.message = message;
        this.senderID = senderID;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
