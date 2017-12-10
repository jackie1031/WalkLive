package com.WalkLiveApp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;
import spark.utils.IOUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.*;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import java.sql.*;

public class TestServer {

    private final String TESTCRIMES = "TestCrimes";
    private final Logger logger = LoggerFactory.getLogger(TestServer.class);

    private final String url = "jdbc:mysql://us-cdbr-iron-east-05.cleardb.net/heroku_6107fd12485edcb";
    private final String user = "b0a1d19d87f384";
    private final String password = "6d11c74b";

    //------------------------------------------------------------------------//
    // Setup
    //------------------------------------------------------------------------//

    @BeforeClass
    public static void setupBeforeClass() throws Exception {
        //Set up the database
        setupDB();


        //Start the main server
        Bootstrap.main(null);
        Spark.awaitInitialization();
    }

    @AfterClass
    public static void tearDownAfterClass() {
        //Stop the server
        Spark.stop();
    }
    //------------------------------------------------------------------------//
    // Setup
    //------------------------------------------------------------------------//

    @Before
    public void setup() throws Exception {
        //Clear the database
        clearDB();

    }

   @After
   public void tearDown() {
       //clearDB();
       //Spark.stop();
   }

    //------------------------------------------------------------------------//
    // Tests
    //------------------------------------------------------------------------//

    @Test
    public void testCreateNew() throws Exception {
        //Add a few elements
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        User[] entries = new User[] {
                new User("jeesookim", "123456","4405339063"),
                new User("michelle", "0123", "4405339063")
        };

        for (User t : entries) {
            Response rCreateNew = request("POST", "/WalkLive/api/users", t);
            //System.out.println("USER: " + t.toString());
            assertEquals("Failed to create new User", 201, rCreateNew.httpStatus);
        }

        //Get them back
        Response r = request("GET", "/WalkLive/api/users", null);
        assertEquals("Failed to get user entries", 200, r.httpStatus);
        List<User> results = getUsers(r);

        //Verify that we got the right element back - should be two users in entries, and the results should be size 2
        assertEquals("Number of user entries differ", entries.length, results.size());

        for (int i = 0; i < results.size(); i++) {
            User actual = results.get(i);

            assertEquals("Mismatch in username", entries[i].getUsername(), actual.getUsername());
            assertEquals("Mismatch in password", entries[i].getPassword(), actual.getPassword());
            assertEquals("Mismatch in contact", entries[i].getContact(), actual.getContact());
            assertEquals("Mismatch in nickname", entries[i].getNickname(), actual.getNickname());
            //assertEquals("Mismatch in creation date", entries[i].getCreatedOn(), actual.getCreatedOn());
            assertEquals("Mismatch in emergency id", entries[i].getEmergencyId(), actual.getEmergencyId());
            assertEquals("Mismatch in emergency number", entries[i].getEmergencyNumber(), actual.getEmergencyNumber());
        }

    }

    @Test
    public void testDuplicateCreation() {

        //Add a few elements
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        User[] entries = new User[] {
                new User("jeesookim", "123456","4405339063"),
                new User("michelle", "0123", "4405339063")
        };

        //add to database
        for (User t : entries) {
            Response rCreateNew = request("POST", "/WalkLive/api/users", t);
            //System.out.println("USER: " + t.toString());
            assertEquals("Failed to create new User", 201, rCreateNew.httpStatus);
        }

        //check if duplications are caught
        User u = new User("jeesookim", "1234567", "4405339063");

        Response rCreateDuplicate = request("POST", "/WalkLive/api/users", u);
        assertEquals("Failed to detect duplicate username", 401, rCreateDuplicate.httpStatus);

        //try another user duplication
        User u2 = new User("michelle", "123456", "4405339063");
        Response rCreateDuplicate2 = request("POST", "/WalkLive/api/users", u2);
        assertEquals("Failed to detect duplicate username", 401, rCreateDuplicate2.httpStatus);

        //Get them back
        Response s = request("GET", "/WalkLive/api/users", null);
        assertEquals("Failed to get user entries", 200, s.httpStatus);
        List<User> results = getUsers(s);

        //Verify that we got the right element back - should be two users in entries, and the results should be size 2
        assertEquals("Number of user entries differ", entries.length, results.size());
    }

    @Test
    public void testFindAll() throws Exception {

        //Add a few elements
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        User[] entries = new User[] {
                new User("jeesookim", "123456", "4405339063"),
                new User("michelle", "0123", "4405339063"),
                new User("yangcao1", "1111","4405339063"),
        };

        for (User t : entries) {
            Response rCreateNew = request("POST", "/WalkLive/api/users", t);
            assertEquals("Failed to create new User", 201, rCreateNew.httpStatus);
        }

        //Get them back
        Response r = request("GET", "/WalkLive/api/users", null);
        assertEquals("Failed to get user entries", 200, r.httpStatus);
        List<User> results = getUsers(r);

        //Verify that we got the right element back
        assertEquals("Number of user entries differ", entries.length, results.size());

        for (int i = 0; i < results.size(); i++) {
            User actual = results.get(i);

            assertEquals("Mismatch in username", entries[i].getUsername(), actual.getUsername());
            assertEquals("Mismatch in password", entries[i].getPassword(), actual.getPassword());
            assertEquals("Mismatch in contact", entries[i].getContact(), actual.getContact());
            assertEquals("Mismatch in nickname", entries[i].getNickname(), actual.getNickname());
            //assertEquals("Mismatch in creation date", entries[i].getCreatedOn(), actual.getCreatedOn());
            assertEquals("Mismatch in emergency id", entries[i].getEmergencyId(), actual.getEmergencyId());
            assertEquals("Mismatch in emergency number", entries[i].getEmergencyNumber(), actual.getEmergencyNumber());
        }
    }

    @Test
    public void testLogin() throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

        //add single element
        User expected = new User("jeesoo", "test-1", "4405339063");
        Response r1 = request("POST", "/WalkLive/api/users", expected);
        assertEquals("Failed to add new user", 201, r1.httpStatus);

        //Get it back so that we know its ID
        Response r2 = request("POST", "/WalkLive/api/users/login", expected);
        assertEquals("Failed to post and authenticate login request", 200, r2.httpStatus);

        User fake = new User("fakenews", "test-1", null);
        Response r3 = request("POST", "/WalkLive/api/users/login", fake);
        assertEquals("Failed to detect nonexistent user", 401, r3.httpStatus);

//        User incorrect = new User("jeesoo", "incorrectpassword"), null;
//        Response r4 = request("POST", "/WalkLive/api/users/login", incorrect);
//        assertEquals("Failed to detect incorrect password", 401, r3.httpStatus);
    }

    @Test
    public void testGetUser() throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

        //add single element
        User expected = new User("jeesoo", "test-1", "4405339063");
        Response r1 = request("POST", "/WalkLive/api/users", expected);
        assertEquals("Failed to add new user", 201, r1.httpStatus);

        //Get it back so that we know its ID
        Response r2 = request("GET", "/WalkLive/api/users/jeesoo", null);
        assertEquals("Failed to get user", 200, r2.httpStatus);

//        User actual = getUser(r2);
//
//            assertEquals("Mismatch in username", expected.getUsername(), actual.getUsername());
//            assertEquals("Mismatch in password", expected.getPassword(), actual.getPassword());
//            assertEquals("Mismatch in contact", expected.getContact(), actual.getContact());
//            assertEquals("Mismatch in nickname", expected.getNickname(), actual.getNickname());
//            assertEquals("Mismatch in creation date", expected.getCreatedOn(), actual.getCreatedOn());
//            assertEquals("Mismatch in emergency id", expected.getEmergencyId(), actual.getEmergencyId());
//            assertEquals("Mismatch in emergency number", expected.getEmergencyNumber(), actual.getEmergencyNumber());

    }

    /**
     * ================================================================
     * Friend Request Handling
     * ================================================================
     */
     @Test
     public void testCreateFriendRequest() throws Exception {
         SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

         //add a few elements
         FriendRequest[] frs = new FriendRequest[] {
                 new FriendRequest("jeesookim", "michelle", null),
                 new FriendRequest("michelle", "yangcao1", null)
         };

         for (FriendRequest f : frs) {
             Response rCreateFR = request("POST", "/WalkLive/api/users/jeesookim/friend_requests", f);
             assertEquals("Failed to create new friend request", 201, rCreateFR.httpStatus);
         }
     }

    @Test
    public void testUpdateEmergencyInfo() throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //add single element
        User expected = new User("jeesoo", "test-1", "4405339063");
        Response r1 = request("POST", "/WalkLive/api/users", expected);
        assertEquals("Failed to add new user", 201, r1.httpStatus);

        //update emergency contact info
        User contactInfo = new User(null, null, null, null, null, "hello", "1231231233");
        Response r2 = request("PUT", "/WalkLive/api/users/jeesoo/emergency_info", contactInfo);
        assertEquals("Failed to get user", 200, r2.httpStatus);

    }

//DELETE LATER
//    @Test
//    public void testUpdate() throws Exception {
//
//        //Add a single element
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
//        Todo expected = new Todo(null, "Test-1", false, df.parse("2015-04-23T23:10:15-0700"));
//        Response r1 = request("POST", "/api/v1/todos", expected);
//        assertEquals("Failed to add", 201, r1.httpStatus);
//
//        //Get it back so that we know its ID
//        Response r2 = request("GET", "/api/v1/todos", null);
//        assertEquals("Failed to get todos", 200, r2.httpStatus);
//        Todo t = getTodos(r2).get(0);
//
//        //Send out an update with a changed title and state
//        Todo updated = new Todo(t.getId(), t.getTitle(), !t.isDone(), t.getCreatedOn());
//        Response r3 = request("PUT", "/api/v1/todos/" + t.getId(), updated);
//        assertEquals("Failed to update", 200, r3.httpStatus);
//
//        //Get stuff back again
//        Response r4 = request("GET", "/api/v1/todos", null);
//        assertEquals("Failed to get todos", 200, r4.httpStatus);
//        List<Todo> results = getTodos(r4);
//
//        //Verify that we got the right element back
//        assertEquals(1, results.size());
//
//        Todo actual = results.get(0);
//        assertEquals("Mismatch in Id", updated.getId(), actual.getId());
//        assertEquals("Mismatch in title", updated.getTitle(), actual.getTitle());
//        assertEquals("Mismatch in creation date", updated.getCreatedOn(), actual.getCreatedOn());
//        assertEquals("Mismatch in done state", updated.isDone(), actual.isDone());
//    }
//
//    @Test
//    public void testDelete() throws Exception {
//
//        //Add a few elements
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
//        Todo[] entries = new Todo[] {
//          new Todo(null, "Test-1", false, df.parse("2015-04-23T23:10:15-0700")),
//          new Todo(null, "Test-2", true, df.parse("2015-03-07T01:10:20-0530")),
//          new Todo(null, "Test-3", false, df.parse("2010-02-19T13:25:43-0530"))
//        };
//
//        for (Todo t : entries) {
//            Response radd = request("POST", "/api/v1/todos", t);
//            assertEquals("Failed to add", 201, radd.httpStatus);
//        }
//
//        //Get them back so that we know our ids
//        Response r1 = request("GET", "/api/v1/todos", null);
//        assertEquals("Failed to get todos", 200, r1.httpStatus);
//        List<Todo> data = getTodos(r1);
//
//        //Delete an entry
//        int indexToDelete = 1;
//        Response r2 = request("DELETE", "/api/v1/todos/" + data.get(indexToDelete).getId(), null);
//        assertEquals("Failed to delete todo", 200, r2.httpStatus);
//
//        //Get it back again
//        Response r3 = request("GET", "/api/v1/todos", null);
//        assertEquals("Failed to get todos", 200, r3.httpStatus);
//        List<Todo> results = getTodos(r3);
//
//        //Verify that we got the right element back
//        assertEquals("Number of todo entries differ", entries.length - 1, results.size());
//
//        //Make a new list of expected Todos with some Java 8 functional foo :)
//        List<Todo> expected = IntStream.range(0, entries.length)
//            .filter(i -> i != indexToDelete)
//            .mapToObj(i -> entries[i])
//            .collect(Collectors.toList());
//
//        //And check
//        for (int i = 0; i < results.size(); i++) {
//            Todo actual = results.get(i);
//            assertEquals(String.format("Index %d: Mismatch in title", i), expected.get(i).getTitle(), actual.getTitle());
//            assertEquals(String.format("Index %d: Mismatch in creation date", i), expected.get(i).getCreatedOn(), actual.getCreatedOn());
//            assertEquals(String.format("Index %d: Mismatch in done state", i), expected.get(i).isDone(), actual.isDone());
//        }
//    }

/*
    @Test
    public void testCoordinate() throws Exception {
        Coordinate c = new Coordinate(0.6, 0.7);
        assertEquals(0.6, c.getLatitude(), 0);
        assertEquals(0.7, c.getLongitude(), 0);
    }


    @Test
    public void testSortAndExpand() throws Exception {
        //c1 < c2
        Coordinate c1 = new Coordinate(0.5, 0.7);
        Coordinate c2 = new Coordinate(0.9, 1.1);
        Coordinate.sortAndExpand(c1, c2);
        assertEquals(0.49, c1.getLatitude(), 0);
        assertEquals(0.6886, c1.getLongitude(), 0.01);
        assertEquals(0.91, c2.getLatitude(), 0);
        assertEquals(1.11608, c2.getLongitude(), 0.01);

        //c3 > c4
        Coordinate c3 = new Coordinate(0.9, 1.1);
        Coordinate c4 = new Coordinate(0.5, 0.7);
        Coordinate.sortAndExpand(c3, c4);
        assertEquals(0.49, c3.getLatitude(), 0);
        assertEquals(0.6886, c3.getLongitude(), 0.01);
        assertEquals(0.91, c4.getLatitude(), 0);
        assertEquals(1.11608, c4.getLongitude(), 0.01);

    }


    @Test
    public void testGetDangerZone() throws Exception {


        double lat = 3.454;
        double lng = 6.929;
        Coordinate c = new Coordinate(lat, lng);

        Crime[] z1 = new Crime[]{
                new Crime(1025, 18, "JHU malone", "Robbery", c, 23523523),
                new Crime(1128, 18, "JHU levering", "Sexual", c, 4405339),
        };
        Crime[] z2 = new Crime[]{

                new Crime(1126, 18, "JHU brody", "Sexual", c, 24124124),
                new Crime(1127, 18, "JHU shaffer", "Sexual", c, 24124224)
        };

        Crime[] z3 = z2;

        DangerZone testDanger = new DangerZone(z1,z2,z3);

        Crime testZ1 = new Crime(1025, 18, "JHU malone", "Robbery", c, 23523523);
        Crime testZ2 = new Crime(1128, 18, "JHU levering", "Sexual", c, 4405339);
        Crime testZ3 = new Crime(1128, 18, "JHU levering", "Sexual", c, 4405339);
        Crime testZ4 = new Crime(1128, 18, "JHU levering", "Sexual", c, 4405339);
        Crime testZ5 = new Crime(1128, 18, "JHU levering", "Sexual", c, 4405339);
        Crime testZ6 = new Crime(1128, 18, "JHU levering", "Sexual", c, 4405339);
        Crime[] testForZone1 = {testZ1,testZ2,testZ3,testZ4,testZ5,testZ6};

        assertEquals("Number of user entries differ", testDanger.size(), testForZone1.length);


        //assertEquals();


        for (Crime t : z1) {
            Response rCreateNew = request("GET", "/WalkLive/api/getdangerzone", t);
            //System.out.println("USER: " + t.toString());
            assertEquals("Failed to create new User", 404, rCreateNew.httpStatus);
        }



    }
    */

    /**
     * Test getting getCrimes method within a specific range of coordinates from the
     * database.
     */ /*
    @Test
    public void testGetCrimes() throws Exception {

        List<Crime> crimeList = new LinkedList<>();

        double lat = 3.454;
        double lng = 6.929;
        Coordinate c = new Coordinate(lat, lng);

        crimeList.add(new Crime(1025, 18, "JHU malone", "Robbery", c, 1));
        crimeList.add(new Crime(1026, 12, "brody", "theft", c, 2));
        crimeList.add(new Crime(1027, 13, "", "Robbery", c, 3));
        crimeList.add(new Crime(1028, 5, "", "theft", c, 4));

        double fromLng = 200;
        double toLng = 400;

        double fromLat = 200;
        double toLat = 400;

        Coordinate c1 = new Coordinate(fromLat, fromLng);
        Coordinate c2 = new Coordinate(toLat, toLng);

        int fromDate = 20;
        int toDate = 40;
        int timeOfDay = 18;

        Crime from = new Crime(fromDate, c1);
        Crime to = new Crime(toDate, c2);
        int count = 1;
        for(Crime t: crimeList){
            assertEquals(count, t.getLinkId());
            count++;

        }

    }
*/

/*
    @Test
    public void testAddTimePoints() throws Exception {
        double lat = 3.454;
        double lng = 6.929;
        Coordinate c = new Coordinate(lat, lng);

        //public TimePoint(int TimePointID, String "12", Coordinate c, int 1)
        TimePoint temp = new TimePoint(12,"13",c,1);
    }
*/

    //------------------------------------------------------------------------//
    // Generic Helper Methods and classes
    //------------------------------------------------------------------------//

    private Response request(String method, String path, Object content) {
        try {
            URL url = new URL("http", Bootstrap.IP_ADDRESS, Bootstrap.PORT, path);
            System.out.println(url);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoInput(true);
            if (content != null) {
                String contentAsJson = new Gson().toJson(content);
                http.setDoOutput(true);
                http.setRequestProperty("Content-Type", "application/json");
                OutputStreamWriter output = new OutputStreamWriter(http.getOutputStream());
                output.write(contentAsJson);
                output.flush();
                output.close();
            }
            try {
                String responseBody = IOUtils.toString(http.getInputStream());
                return new Response(http.getResponseCode(), responseBody);
            } catch (IOException e) {
//                e.printStackTrace();
//                fail("Sending request failed: " + e.getMessage());
                return new Response(http.getResponseCode(), "ERROR"); //still return the http status code for testing sake
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail("Sending request failed: " + e.getMessage());
            return null;
        }
    }

    private Response request(String method, String path, String content) {
        try {
            URL url = new URL("http", Bootstrap.IP_ADDRESS, Bootstrap.PORT, path);
            System.out.println(url);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoInput(true);
            if (content != null) {
                http.setDoOutput(true);
                http.setRequestProperty("Content-Type", "application/json");
                OutputStreamWriter output = new OutputStreamWriter(http.getOutputStream());
                output.write(content);
                output.flush();
                output.close();
            }
            try {
                String responseBody = IOUtils.toString(http.getInputStream());
                return new Response(http.getResponseCode(), responseBody);
            } catch (IOException e) {
//                e.printStackTrace();
//                fail("Sending request failed: " + e.getMessage());
                return new Response(http.getResponseCode(), "ERROR"); //still return the http status code for testing sake
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail("Sending request failed: " + e.getMessage());
            return null;
        }
    }


    private static class Response {

        public String content;

        public int httpStatus;

        public Response(int httpStatus, String content) {
            this.content = content;
            this.httpStatus = httpStatus;
        }

        public <T> T getContentAsObject(Type type) {
            return new Gson().fromJson(content, type);
        }
    }

    // ------------------------------------------------------------------------//
    // Survival Maps Specific Helper Methods and classes
    // ------------------------------------------------------------------------//

    private static void setupDB() {

        String url = "jdbc:mysql://us-cdbr-iron-east-05.cleardb.net/heroku_6107fd12485edcb";
        String user = "b0a1d19d87f384";
        String password = "6d11c74b";

        Connection conn = null;
        Statement stm = null;
        ResultSet res = null;

        try {
            conn = DriverManager.getConnection(url, user, password);
            stm = conn.createStatement();

            String setup = "CREATE TABLE IF NOT EXISTS users (username TEXT, password TEXT, contact TEXT, nickname TEXT, created_on TIMESTAMP, emergency_id TEXT, emergency_number TEXT)" ;
            res = stm.executeQuery(setup);

            String sql = "DROP TABLE IF EXISTS TestCrimes";
            stm.executeUpdate(sql);
            String sql2 = "DROP TABLE IF EXISTS TestSafetyRating";
            stm.executeUpdate(sql2);
            String sql3 = "DROP TABLE IF EXISTS users" ;
            stm.executeUpdate(sql3);
            String sql4 = "DROP TABLE IF EXISTS friendRequests" ;
            stm.executeUpdate(sql4);

            if (res.next()) {

                System.out.println(res.getString(1));
            }

        } catch (SQLException ex) {
            //logger.error("Failed to create schema at startup", ex);
            //throw new WalkLiveService.UserServiceException("Failed to create schema at startup");
        }
    }

    private List<User> getUsers(Response r) {
        //Getting a useful Type instance for a *generic* container is tricky given Java's type erasure.
        //The technique below is documented in the documentation of com.google.gson.reflect.TypeToken.
        Type type = (new TypeToken<ArrayList<User>>() { }).getType();
        return r.getContentAsObject(type);
    }

//    private User getUser(Response r) {
//        Type type = (new TypeToken<User>() { }).getType();
//        return r.getContentAsObject();
//    }

    /**
     * Clears the database of all test tables.
     * @return the clean database source
     */
    private void clearDB() {
        Connection conn = null;
        Statement stm = null;
        ResultSet res = null;

        try {
            conn = DriverManager.getConnection(url, user, password);
            stm = conn.createStatement();

            String sql = "DROP TABLE IF EXISTS TestCrimes";
            stm.executeUpdate(sql);
            String sql2 = "DROP TABLE IF EXISTS TestSafetyRating";
            stm.executeUpdate(sql2);
            String sql3 = "DROP TABLE IF EXISTS users" ;
            stm.executeUpdate(sql3);
            String sql4 = "DROP TABLE IF EXISTS friendRequests" ;
            stm.executeUpdate(sql4);
            String sql5 = "DROP TABLE IF EXISTS Trips" ;
            stm.executeUpdate(sql5);

            String sqlNew = "CREATE TABLE IF NOT EXISTS users (username TEXT, password TEXT, contact TEXT, nickname TEXT, created_on TIMESTAMP, emergency_id TEXT, emergency_number TEXT)" ;
            String sqlNew2 = "CREATE TABLE IF NOT EXISTS friendRequests (sender TEXT, recipient TEXT, sent_on TIMESTAMP)" ;
            String sqlNew3 = "CREATE TABLE IF NOT EXISTS Trips (sender TEXT, recipient TEXT, sent_on TIMESTAMP)" ;
            stm.executeUpdate(sqlNew);
            stm.executeUpdate(sqlNew2);
            stm.executeUpdate(sqlNew3);
        } catch (SQLException ex) {
            logger.error("Failed to create schema at startup", ex);
            //throw new WalkLiveService.UserServiceException("Failed to create schema at startup");

        } finally {
            if (stm != null) {
                try {
                    stm.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) { /* ignored */}
            }
        }
    }
}