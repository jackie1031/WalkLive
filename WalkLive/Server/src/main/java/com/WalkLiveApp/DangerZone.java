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
    private Crime[] z1, z2;

    public DangerZone(Crime[] z1, Crime[] z2) {
        this.z1 = z1;
        this.z2 = z2;
    }

    public Crime[] getZ1() {
        return this.z1;
    }

    public Crime[] getZ2() {
        return this.z2;
    }

    public int size(){return z1.length +z2.length;}
}
