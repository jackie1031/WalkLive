package com.WalkLiveApp;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class FriendRequest {
    private Date time = new Date();
    private String from; //User? or String?
    private String to;

    public FriendRequest(String sender, String recipient, Date requestTime) {
        this.time = requestTime;
        this.from = sender;
        this.to = recipient;
    }

    public Date getRequestTime() {
        return this.time;
    }

    public String getSender() {
        return this.from;
    }

    public String getRecipient() {
        return this.to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FriendRequest fr = (FriendRequest) o;

        if (time != null ? !time.equals(fr.time) : fr.time != null) return false;
        if (from != null ? !from.equals(fr.from) : fr.from != null) return false;
        if (to != null ? !to.equals(fr.to) : fr.to != null) return false;

        return true;
    }

    public String toString() {
        String str = "FROM: " + this.from + " TO: " + this.to + " SENT AT: " + this.time;

        return str;
    }
}
