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
import java.util.*;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.util.MathUtils;


public class CrimeDataHandler {
    //public static final Logger logger = LoggerFactory.getLogger(WalkLiveService.class);

    static Connection conn = null;
    static String url = "jdbc:mysql://us-cdbr-iron-east-05.cleardb.net/heroku_6107fd12485edcb";
    static String user = "b0a1d19d87f384";
    static String password = "6d11c74b";


    static PreparedStatement ps = null;
    static ResultSet res = null;
    static Statement stm = null;


    //public static void updateDB() throws FileNotFoundException,IOException,ParseException {
    public static void main(String[] args) throws Exception {

        //JSONObject object = (JSONObject) new JSONParser().parse(body);

        //should be in a try catch block in case that incorrect body is given
//        String id = object.get("emergency_id").toString();
//        String number = object.get("emergency_number").toString();

        //query to see if username given is valid

        JSONParser parser = new JSONParser();
        try{
            JSONArray a = (JSONArray) parser.parse(new FileReader("partCrime.json"));
            //System.out.println(a);

            for (Object o : a)
            {
                JSONObject crime = (JSONObject) o;

                long hour_of_day = (long) crime.get("hour_of_day");
                //System.out.println("hour_of_day: "+hour_of_day);
//
                long incident_id = (long) crime.get("incident_id");
                //System.out.println("incident_id is: "+incident_id);

                String address_1 = (String) crime.get("address_1");
                //System.out.println("address_1: "+address_1);

                String address_2 = (String) crime.get("address_2");
                //System.out.println("address_2: "+address_2);

                double latitude = (double) crime.get("latitude");
                //System.out.println("latitude is: "+latitude);

                double longitude = (double) crime.get("longitude");
                //System.out.println("longitude is: "+longitude);


                String incident_description = (String) crime.get("incident_description");
                //System.out.println("incident_description is: "+incident_description);

                String parent_incident_type = (String) crime.get("parent_incident_type");
                //System.out.println("parent_incident_type is: "+parent_incident_type);

                String sqlSetUp = "CREATE TABLE IF NOT EXISTS Crime(incident_id TEXT, address_1 TEXT, address_2 TEXT, latitude DOUBLE, longitude DOUBLE, hour_of_day INT,incident_description TEXT, parent_incident_type TEXT)";

                String sql = "INSERT INTO Crime (incident_id, address_1, address_2, latitude, longitude,hour_of_day,incident_description,parent_incident_type) VALUES (?, ?, ?, ?,?,?,?,?)" ;
                //System.out.println("got here!");


                /** need to check the range
                (SELECT sensorNumber, rangeStartTime, rangeEndTime, sensorLow, sensorAverage, sensorHigh
                FROM rangeData
                WHERE (rangeStartTime BETWEEN 0 AND 43200) AND (sensorNumber = 3))
                 **/

               // String sqlSelect = "SELECT * FROM Crime WHERE (latitude between ?) LIMIT 1";



                try {
                    conn = DriverManager.getConnection(url, user, password);
                    stm = conn.createStatement();

                    ps = conn.prepareStatement(sql);
                    stm.executeUpdate(sqlSetUp);

                    ps.setLong(1, incident_id);
                    ps.setString(2, address_1);
                    ps.setString(3, address_2);
                    ps.setDouble(4, latitude);
                    ps.setDouble(5, longitude);
                    ps.setLong(6, hour_of_day);
                    ps.setString(7, incident_description);
                    ps.setString(8, parent_incident_type);
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
