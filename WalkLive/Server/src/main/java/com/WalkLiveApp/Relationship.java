package com.WalkLiveApp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Date;

public class Relationship {
    private int _id;
    private Date sent_on = new Date();
    private String sender;
    private String recipient;
    private int relationship;

    public Relationship(String sender, String recipient, Date requestTime) throws WalkLiveService.UserServiceException {
        this._id = 0; //temp default
        this.sent_on = requestTime;
        this.sender = sender;
        this.recipient = recipient;
        this.relationship = 0;
    }

    public Relationship(int requestId, String sender, String recipient, int relationship, Date requestTime) {
        this._id = requestId;
        this.sent_on = requestTime;
        this.sender = sender;
        this.recipient = recipient;
        this.relationship = relationship;
    }

    public int getId() { return this._id; }

    public Date getRequestTime() {
        return this.sent_on;
    }

    public String getSender() {
        return this.sender;
    }

    public String getRecipient() {
        return this.recipient;
    }

    public int getRelationship() { return this.relationship; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Relationship fr = (Relationship) o;

        if (_id != fr._id) return false;
        if (sent_on != null ? !sent_on.equals(fr.sent_on) : fr.sent_on != null) return false;
        if (sender != null ? !sender.equals(fr.sender) : fr.sender != null) return false;
        if (recipient != null ? !recipient.equals(fr.recipient) : fr.recipient != null) return false;
        if (relationship != fr.relationship) return false;

        return true;
    }

    public String toString() {
        String str = "ID: " + this._id + " FROM: " + this.sender + " TO: " + this.recipient + " RELATION: " + this.relationship + " SENT AT: " + this.sent_on;

        return str;
    }
}
