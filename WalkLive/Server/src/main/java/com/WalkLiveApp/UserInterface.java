package com.WalkLiveApp;

import java.util.List;

public interface UserInterface {
    public List<String> getLog();

    public void manageLog();

    public int initiateTrip();

    public int modifyContact();

    public int sendTrackRequest();

    public int sendFriendRequest();
}