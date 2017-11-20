package com.WalkLiveApp;

import java.lang.Math;


public class Table {
    private int x,y;
    private int linkId;
    private double alarm;
    private int AADT;

    public Table(int x, int y) {
        this.x=x;
        this.y=y;
        linkId = 0;
        alarm = 0;
        AADT = 0;
    }


    public Table(double lat, double lng) {
        double latitudeDegree = lat * Math.PI / 180;
        double dx = (lng + 180) / 360 * 262144;
        double dy = (1 - (Math.log(Math.tan(latitudeDegree) + 1 / Math.cos(latitudeDegree)) / Math.PI)) / 2 * 262144;
        this.x = (int) dx;
        this.y = (int) dy;
        linkId = 0;
        alarm = 0;
        AADT = 0;
    }

    public Table() {
        x =0 ;
        y = 0 ;
        linkId = 0;
        alarm = 0;
        AADT = 0;
    }

    public Table(int xArg, int yArg, int linkIdArg, double alarmArg, int aadtArg) {
        x = xArg;
        y= yArg;
        linkId = linkIdArg;
        alarm = alarmArg;
        AADT = aadtArg;
    }

    public void setLinkId(int linkId) {
        this.linkId = linkId;
    }

    public void setAlarm(double alarm) {
        this.alarm = alarm;
    }

    public void setAADT(int AADT) {
        this.AADT = AADT;
    }

    public void setX(int x) {this.x = x;}
    public void setY(int y) {this.y = y;}
    public int getX() { return x; }
    public int getY() { return y; }
    public int getLinkId() { return linkId; }
    public double getAlarm() { return alarm; }
    public int getAADT() { return AADT; }

}
