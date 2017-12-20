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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrimeDataHandler {
    static Connection conn = null;
    public static String url = "jdbc:mysql://us-cdbr-iron-east-05.cleardb.net/heroku_6107fd12485edcb";
    public static String user = "b0a1d19d87f384";
    public static String password = "6d11c74b";
    public static final Logger logger = LoggerFactory.getLogger(CrimeDataHandler.class);

    static PreparedStatement ps = null;
    static ResultSet res = null;
    static Statement stm = null;

    public static void main(String[] args) throws Exception {
        addDB("crimeSampleDay.json");
        addDB("crimeSampleNight.json");
    }

    /**
     * Add the json content to our database
     * @param fileName
     * @throws IOException
     * @throws ParseException
     */
    private static void addDB(String fileName) throws IOException, ParseException, SQLException{
        int dangerLevel;

        JSONParser parser = new JSONParser();

        String checkDay = "Day";
        String checkNight = "Night";

        try{
            JSONArray a = (JSONArray) parser.parse(new FileReader(fileName));

            for (Object o : a)
            {
                JSONObject crime = (JSONObject) o;
                double radius = (double) crime.get("radius");
                long count = (long) crime.get("count");

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

                double latitude = (double) crime.get("latitude");

                double longitude = (double) crime.get("longitude");

                String sqlSetUp = "CREATE TABLE IF NOT EXISTS dangerZonesDay(longitude DOUBLE, latitude DOUBLE, radius DOUBLE, count INT, dangerLevel INT,day_or_night TEXT)";
                String sqlSetUp2 = "CREATE TABLE IF NOT EXISTS dangerZonesNight(longitude DOUBLE, latitude DOUBLE, radius DOUBLE, count INT, dangerLevel INT,day_or_night TEXT)";


                String sqlInsertClusters = null;
                if(fileName.toLowerCase().contains(checkDay.toLowerCase())){
                    sqlInsertClusters = " INSERT INTO dangerZonesDay(longitude, latitude, radius, count,dangerLevel, day_or_night) " +
                        "VALUES (?,?,?,?,?,'day') ";
                } else if (fileName.toLowerCase().contains(checkNight.toLowerCase())){
                    sqlInsertClusters = " INSERT INTO dangerZonesNight(longitude, latitude, radius, count,dangerLevel, day_or_night) " +
                            "VALUES (?,?,?,?,?,'night') ";
                } else {
                    System.out.println("not valid datasets to put in database");
                }


                try {
                    conn = DriverManager.getConnection(url, user, password);
                    stm = conn.createStatement();
                    stm.executeUpdate(sqlSetUp);
                    stm.executeUpdate(sqlSetUp2);

                    ps = conn.prepareStatement(sqlInsertClusters);
                    //System.out.println("got here!");

                    ps.setDouble(1, longitude);
                    ps.setDouble(2, latitude);
                    ps.setDouble(3, radius);
                    ps.setLong(4, count);
                    ps.setLong(5, dangerLevel);
                    ps.executeUpdate();
                    //System.out.println("SUCCESSFULLY ADDED.");

                } catch (SQLException ex) {
                    logger.error("SQLException: Failed to create new entry", ex);
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