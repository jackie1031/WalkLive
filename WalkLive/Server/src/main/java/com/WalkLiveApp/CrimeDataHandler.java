package com.WalkLiveApp;
//import org.json.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.io.InputStream;
//import org.apache.commons.io.IOUtils;
import java.util.Iterator;

import org.json.simple.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

//import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CrimeDataHandler {


    public static void main(String[] args) throws Exception {
         Connection conn = null;
         String url = "jdbc:mysql://us-cdbr-iron-east-05.cleardb.net/heroku_6107fd12485edcb";
         String user = "b0a1d19d87f384";
         String password = "6d11c74b";

        PreparedStatement ps = null;
        ResultSet res = null;

        //JSONObject object = (JSONObject) new JSONParser().parse(body);

        //should be in a try catch block in case that incorrect body is given
//        String id = object.get("emergency_id").toString();
//        String number = object.get("emergency_number").toString();

        //query to see if username given is valid

        JSONParser parser = new JSONParser();
        JSONArray a = (JSONArray) parser.parse(new FileReader("test.json"));
        System.out.println(a);



        for (Object o : a)
        {
            JSONObject crime = (JSONObject) o;

            long timeOfDay = (long) crime.get("hour_of_day");
            System.out.println(timeOfDay);
//
            String city = (String) crime.get("city");
            System.out.println(city);

            String job = (String) crime.get("job");
            System.out.println(job);

            JSONArray cars = (JSONArray) crime.get("cars");

            for (Object c : cars)
            {
                System.out.println(c+"");
            }
        }

        String sql = "INSERT INTO friends (_id, sender, recipient, relationship, sent_on) VALUES (?, ?, ?, 0, null)" ;

        try {
            conn = DriverManager.getConnection(url, user, password);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, request_id);
            ps.setString(2, sender);
            ps.setString(3, recipient);
            ps.executeUpdate();

            System.out.println("SUCCESSFULLY ADDED.");
        } catch (SQLException ex) {
            logger.error("WalkLiveService.createFriendRequest: Failed to create new entry", ex);
            throw new WalkLiveService.RelationshipServiceException("WalkLiveService.createFriendRequest: Failed to create new entry", ex);
        }  finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        //JSONArray a = (JSONArray) parser.parse(new FileReader("/Users/lintianyi/Desktop/2017-group-14/WalkLive/Server/test.json "));
        //Object obj = parser.parse(new FileReader("/Users/lintianyi/Desktop/2017-group-14/WalkLive/Server/test.json "));

//        JSONObject jsonObject = (JSONObject) obj;
//        JSONArray array = obj.getJSONArray("/Users/lintianyi/Desktop/2017-group-14/WalkLive/Server/test.json ");


        //System.out.println(obj);
        //System.out.println( new File("test.json").getAbsoluteFile());


//        for (Object o : a)
//        {
//            JSONObject person = (JSONObject) o;
//
//            String name = (String) person.get("name");
//            System.out.println(name);
//
//            String city = (String) person.get("city");
//            System.out.println(city);
//
//            String job = (String) person.get("job");
//            System.out.println(job);
//
//            JSONArray cars = (JSONArray) person.get("cars");
//
//            for (Object c : cars)
//            {
//                System.out.println(c+"");
//            }
//        }

    }
}
