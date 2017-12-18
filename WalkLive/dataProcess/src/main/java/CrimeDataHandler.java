//import org.json.*;
import java.io.*;
import java.sql.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class CrimeDataHandler {
    //public static final Logger logger = LoggerFactory.getLogger(WalkLiveService.class);

    static Connection conn = null;
    public static String url = "jdbc:mysql://us-cdbr-iron-east-05.cleardb.net/heroku_6107fd12485edcb";
    public static String user = "b0a1d19d87f384";
    public static String password = "6d11c74b";



    static PreparedStatement ps = null;
    static ResultSet res = null;
    static Statement stm = null;

    public static void main(String[] args) throws Exception {

        addDB("crimeSampleDay.json");
        addDB("crimeSampleNight.json");


//        int dangerLevel;
//
//        JSONParser parser = new JSONParser();
//
//        try{
//            JSONArray a = (JSONArray) parser.parse(new FileReader("crimeSampleDay.json"));
//            //System.out.println(a);
//
//            for (Object o : a)
//            {
//                JSONObject crime = (JSONObject) o;
//
//
//                double radius = (double) crime.get("radius");
//                long count = (long) crime.get("count");
//                String day_or_night = (String)crime.get("time");
//
//
//                //600,900,1200,1500
//                if(count < 100){
//                     dangerLevel = 0;
//                } else if (count < 600){
//                    dangerLevel = 1;
//
//                } else if (count < 900){
//                    dangerLevel = 2;
//
//                } else if (count < 1200){
//                    dangerLevel = 3;
//
//                } else {
//                    dangerLevel = 4;
//
//                }
//
//                //System.out.println("DANGER LEVEL is: "+dangerLevel);
//
//                double latitude = (double) crime.get("latitude");
//
//                double longitude = (double) crime.get("longitude");
//
//                String sqlSetUp = "CREATE TABLE IF NOT EXISTS dangerZonesDay(cluster_id TEXT, longitude DOUBLE, latitude DOUBLE, radius DOUBLE, count INT, dangerLevel INT,day_or_night TEXT)";
//
//                String sqlInsertClusters = " INSERT INTO dangerZones(cluster_id, longitude, latitude, radius, count,dangerLevel, day_or_night) " +
//                        "VALUES (1,?,?,?,?,?,?) ";
//
//
//                try {
//                    conn = DriverManager.getConnection(url, user, password);
//                    stm = conn.createStatement();
//                    stm.executeUpdate(sqlSetUp);
//
//                    ps = conn.prepareStatement(sqlInsertClusters);
//                    //stm.executeUpdate(sqlNew8);
//
//                    System.out.println("got here!");
//
//                    //ps.setLong(1, incident_id);
//                    ps.setDouble(1, longitude);
//                    ps.setDouble(2, latitude);
//                    ps.setDouble(3, radius);
//                    ps.setLong(4, count);
//                    ps.setLong(5, dangerLevel);
//                    ps.setString(6, day_or_night);
//                    ps.executeUpdate();
//                    //System.out.println("SUCCESSFULLY ADDED.");
//
//                } catch (SQLException ex) {
//                    //logger.error("WalkLiveService.createFriendRequest: Failed to create new entry", ex);
//                    //throw new WalkLiveService.RelationshipServiceException("WalkLiveService.createFriendRequest: Failed to create new entry", ex);
//                }  finally {
//                    if (ps != null) {
//                        try {
//                            ps.close();
//                        } catch (SQLException e) { }
//                    }
//                    if (res != null) {
//                        try {
//                            res.close();
//                        } catch (SQLException e) { }
//                    }
//                    if (conn != null) {
//                        try {
//                            conn.close();
//                        } catch (SQLException e) { }
//                    }
//                }
//            }
//        }catch (IOException ex) {
//            throw new IOException("WalkLiveService.createFriendRequest: Failed to create new entry", ex);
//
//        }catch (ParseException ex) {
//            throw new ParseException(1,ex);
//
//        }



    }

    private static void addDB(String fileName) throws IOException, ParseException{
        int dangerLevel;

        JSONParser parser = new JSONParser();

        System.out.println("the data is" +fileName);


        try{
            JSONArray a = (JSONArray) parser.parse(new FileReader(fileName));
            //System.out.println(a);

            for (Object o : a)
            {
                JSONObject crime = (JSONObject) o;


                double radius = (double) crime.get("radius");
                long count = (long) crime.get("count");
                String day_or_night = (String)crime.get("time");


                //600,900,1200,1500
                if(count < 100){
                    dangerLevel = 0;
                } else if (count < 600){
                    dangerLevel = 1;

                } else if (count < 900){
                    dangerLevel = 2;

                } else if (count < 1200){
                    dangerLevel = 3;

                } else if(count < 1600){
                    dangerLevel = 4;
                } else {
                    dangerLevel = 5;

                }

                //System.out.println("DANGER LEVEL is: "+dangerLevel);

                double latitude = (double) crime.get("latitude");

                double longitude = (double) crime.get("longitude");

                String sqlSetUp = "CREATE TABLE IF NOT EXISTS dangerZonesDay(cluster_id TEXT, longitude DOUBLE, latitude DOUBLE, radius DOUBLE, count INT, dangerLevel INT,day_or_night TEXT)";
                String sqlSetUp2 = "CREATE TABLE IF NOT EXISTS dangerZonesNight(cluster_id TEXT, longitude DOUBLE, latitude DOUBLE, radius DOUBLE, count INT, dangerLevel INT,day_or_night TEXT)";

                String checkDay = "Day";
                String checkNight = "Night";
                String sqlInsertClusters = null;
                if(fileName.toLowerCase().contains(checkDay.toLowerCase())){
                    sqlInsertClusters = " INSERT INTO dangerZonesDay(cluster_id, longitude, latitude, radius, count,dangerLevel, day_or_night) " +
                        "VALUES (1,?,?,?,?,?,'check') ";
                } else if (fileName.toLowerCase().contains(checkNight.toLowerCase())){
                    sqlInsertClusters = " INSERT INTO dangerZonesNight(cluster_id, longitude, latitude, radius, count,dangerLevel, day_or_night) " +
                            "VALUES (1,?,?,?,?,?,'check') ";
                } else {
                    System.out.println("not valid datasets to put in database");
                }


                try {
                    conn = DriverManager.getConnection(url, user, password);
                    stm = conn.createStatement();
                    stm.executeUpdate(sqlSetUp);
                    stm.executeUpdate(sqlSetUp2);

                    ps = conn.prepareStatement(sqlInsertClusters);
                    //stm.executeUpdate(sqlNew8);

                    System.out.println("got here!");

                    //ps.setLong(1, incident_id);
                    ps.setDouble(1, longitude);
                    ps.setDouble(2, latitude);
                    ps.setDouble(3, radius);
                    ps.setLong(4, count);
                    ps.setLong(5, dangerLevel);
                    //ps.setString(6, day_or_night);
                    ps.executeUpdate();
                    System.out.println("SUCCESSFULLY ADDED.");

                } catch (SQLException ex) {
                    //logger.error("WalkLiveService.createFriendRequest: Failed to create new entry", ex);
                    //throw new WalkLiveService.RelationshipServiceException("WalkLiveService.createFriendRequest: Failed to create new entry", ex);
                }  finally {
                    if (ps != null) {
                        try {
                            ps.close();
                        } catch (SQLException e) { }
                    }
                    if (res != null) {
                        try {
                            res.close();
                        } catch (SQLException e) { }
                    }
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (SQLException e) { }
                    }
                }
            }
        }catch (IOException ex) {
            throw new IOException("WalkLiveService.createFriendRequest: Failed to create new entry", ex);

        }catch (ParseException ex) {
            throw new ParseException(1,ex);

        }


    }



}