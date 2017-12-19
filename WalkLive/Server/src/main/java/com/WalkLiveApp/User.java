package com.WalkLiveApp;

import java.util.Date;

public class User {
    private String username;
    private String password;
    private String contact;
    private String nickname;
    private Date created_on = new Date();
    private String emergency_id;
    private String emergency_number;

    public User(String username, String password, String contact) {
        this.username = username;
        this.password = password;
        this.contact = contact;
        this.nickname = null;
        this.created_on = null;
        this.emergency_id = null;
        this.emergency_number = null;
    }

    public User(String username, String password, String contact, String nickname, Date createdOn, String emergencyId, String emergencyNumber) {
        this.username = username;
        this.password = password;
        this.contact = contact;
        this.nickname = nickname;
        this.created_on = createdOn;
        this.emergency_id = emergencyId;
        this.emergency_number = emergencyNumber;
    }

    //add getters

    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() { return this.password; }

    public String getContact() { return this.contact; }

    public String getNickname() {
        return this.nickname;
    }

    public Date getCreatedOn() {
        return this.created_on;
    }

    public String getEmergencyId() {
        return this.emergency_id;
    }

    public String getEmergencyNumber() {
        return this.emergency_number;
    }

//    public List<String> getLog() {
//        return null;
//    }
//
//    public void manageLog() {
//
//    }
//
//    public int initiateTrip() {
//        return 0;
//    }
//
//    public int modifyContact() {
//        return 0;
//    }
//
//    public int sendTrackRequest() {
//        return 0;
//    }
//
//    public int sendFriendRequest() {
//        return 0;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (username != user.username) return false;
        if (password != user.password) return false;
        if (contact != user.contact) return false;
        if (nickname != null ? !nickname.equals(user.nickname) : user.nickname != null) return false;
        if (created_on != null ? !created_on.equals(user.created_on) : user.created_on != null) return false;
        if (emergency_id != null ? !emergency_id.equals(user.emergency_id) : user.emergency_id != null) return false;
        if (emergency_number != null ? !emergency_number.equals(user.emergency_number) : user.emergency_number != null) return false;

        return true;
    }

    public String toString() {

        String str = this.username + " " + this.password + " " + this.contact;

        return str;
    }

}
