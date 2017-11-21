package com.WalkLiveApp;

public class DangerZone {

    public static class LinkId {
        private int linkId, count;

        public int getLinkId() {
            return linkId;
        }

        public void setLinkId(int id) {
            linkId = id;
        }
    }

    @SuppressWarnings("unused")
    private int[] red, yellow;

    public DangerZone(int[] red, int[] yellow) {
        this.red = red;
        this.yellow = yellow;
    }

    public int[] getRed() {
        return this.red;
    }

    public int[] getYellow() {
        return this.yellow;
    }
}
