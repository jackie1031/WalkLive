package com.WalkLiveApp;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class FriendRequest {
    private Date sent_on = new Date();
    private String sender; //User? or String?
    private String recipient;

    public FriendRequest(String sender, String recipient, Date requestTime) {
        this.sent_on = requestTime;
        this.sender = sender;
        this.recipient = recipient;
    }

    public Date getRequestTime() {
        return this.sent_on;
    }

    public String getSender() {
        return this.sender;
    }

    public String getRecipient() {
        return this.recipient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FriendRequest fr = (FriendRequest) o;

        if (sent_on != null ? !sent_on.equals(fr.sent_on) : fr.sent_on != null) return false;
        if (sender != null ? !sender.equals(fr.sender) : fr.sender != null) return false;
        if (recipient != null ? !recipient.equals(fr.recipient) : fr.recipient != null) return false;

        return true;
    }

    public String toString() {
        String str = "FROM: " + this.sender + " TO: " + this.recipient + " SENT AT: " + this.sent_on;

        return str;
    }
}
