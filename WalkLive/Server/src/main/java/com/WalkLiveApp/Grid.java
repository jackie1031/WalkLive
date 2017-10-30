package com.WalkLiveApp;

package com.oose2016.group4.server;

import java.lang.Math;

/**This class is used to map latitudes and longitudes in double type into grids on the map, which is plotted with
 * int type indices x and y.
 * This is an effective measure to cope with MapQuest's restrictions on non-commercial user licenses.
 * Created by vegito2002 on 12/10/16.
 */
public class Grid {
    private int x,y;
    private int linkId;
    private double alarm;
    private int AADT;

    public Grid (int x, int y) {
        this.x=x;
        this.y=y;
        linkId = 0;
        alarm = 0;
        AADT = 0;
    }

    /**
     * The major purpose of this class: mapping actually geographical coordinates
     * to integer-formatted grid coordinates, or conceptually, putting coordinates into
     * grids on the map.
     * @param lat latitude of coordinate
     * @param lng longitude of coordinate
     */
    public Grid (double lat, double lng) {
        double latitudeDegree = lat * Math.PI / 180;
        double dx = (lng + 180) / 360 * 262144;
        double dy = (1 - (Math.log(Math.tan(latitudeDegree) + 1 / Math.cos(latitudeDegree)) / Math.PI)) / 2 * 262144;
        this.x = (int) dx;
        this.y = (int) dy;
        linkId = 0;
        alarm = 0;
        AADT = 0;
    }

    public Grid() {
        x =0 ;
        y = 0 ;
        linkId = 0;
        alarm = 0;
        AADT = 0;
    }

    public Grid(int xArg, int yArg, int linkIdArg, double alarmArg, int aadtArg) {
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
