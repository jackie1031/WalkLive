package com.WalkLiveApp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import org.sqlite.SQLiteDataSource;
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


public class TestServer {

    SQLiteDataSource dSource;
    private final String TESTCRIMES = "TestCrimes";
    private final Logger logger = LoggerFactory.getLogger(TestServer.class);



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
            assertEquals("Mismatch in creation date", entries[i].getCreatedOn(), actual.getCreatedOn());
            assertEquals("Mismatch in friendId", entries[i].getFriendId(), actual.getFriendId());
            assertEquals("Mismatch in nickname", entries[i].getNickname(), actual.getNickname());
        }

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
            assertEquals("Mismatch in creation date", entries[i].getCreatedOn(), actual.getCreatedOn());
            assertEquals("Mismatch in friendId", entries[i].getFriendId(), actual.getFriendId());
            assertEquals("Mismatch in nickname", entries[i].getNickname(), actual.getNickname());
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
    public void testLogin() throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

        //add single element
        User expected = new User("jeesoo", "test-1", "4405339063");
        Response r1 = request("POST", "/WalkLive/api/users", expected);
        assertEquals("Failed to add new user", 201, r1.httpStatus);

        //Get it back so that we know its ID
        Response r2 = request("POST", "/WalkLive/api/users/login", expected);
        assertEquals("Failed to post and authenticate login request", 200, r2.httpStatus);

        //assert to check for return string uri

    }

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
        WalkLiveService s = new WalkLiveService(dSource);

        try (Connection conn = s.getDb().open()){
            String sql1 = "CREATE TABLE IF NOT EXISTS " + TESTCRIMES
                    + " (date INTEGER NOT NULL, linkId INTEGER NOT NULL, address TEXT NOT NULL, "
                    + "latitude REAL NOT NULL, longitude REAL NOT NULL, "
                    + "type TEXT, PRIMARY KEY (date, linkId, type));";
            conn.createQuery(sql1).executeUpdate();

            int date = 0, linkid = 0, time = 0;
            String address = "", type = "";
            double latitude = 0, longitude = 0;

            String sql2 = " INSERT INTO " + TESTCRIMES
                    + " VALUES(:date, :linkid, :address, :latitude, :longitude, :type); ";

            for (int i = 0; i < 60; i++) {
                Crime c = new Crime(date, time, address, type, latitude, longitude, linkid);
                    conn.createQuery(sql2).bind(c).executeUpdate();

                latitude++;
                longitude++;
            }

            linkid = 1;

            for (int i = 0; i < 40; i++) {
                Crime c = new Crime(date, time, address, type, latitude, longitude, linkid);
                conn.createQuery(sql2).bind(c).executeUpdate();

                latitude++;
                longitude++;
            }

            linkid = 2;

            for (int i = 0; i < 20; i++) {
                Crime c = new Crime(date, time, address, type, latitude, longitude, linkid);
                conn.createQuery(sql2).bind(c).executeUpdate();

                latitude++;
                longitude++;
            }

            Coordinate from = new Coordinate(0, 0);
            Coordinate to = new Coordinate(120, 120);

            int[] red = s.getDangerZone(from, to, TESTCRIMES).getRed();
            int[] yellow = s.getDangerZone(from, to, TESTCRIMES).getYellow();

            int[] redTarget = {0};
            int[] yellowTarget = {1};

            assertTrue(Arrays.equals(red, redTarget));
            assertTrue(Arrays.equals(yellow, yellowTarget));

            Coordinate from1 = null;
            Coordinate to1 = null;
            assertEquals(s.getDangerZone(from1, to1, TESTCRIMES), null);
        } catch (Sql2oException e) {
            logger.error("Failed to get avoid linkIds in ServerTest", e);
        } catch (Exception e) {
            logger.error("Failed to create Coordinate", e);
        }
    }

    /**
     * Test getting getCrimes method within a specific range of coordinates from the
     * database.
     */
    @Test
    public void testGetCrimes() {
        try {
            WalkLiveService s = new WalkLiveService(dSource);

            try (Connection conn = s.getDb().open()) {
                String sql1 = "CREATE TABLE IF NOT EXISTS TestCrimes "
                        + "(date INTEGER NOT NULL, linkId INTEGER NOT NULL, address TEXT NOT NULL, "
                        + "latitude REAL NOT NULL, longitude REAL NOT NULL, "
                        + "type TEXT, PRIMARY KEY (date, linkId, type));";
                conn.createQuery(sql1).executeUpdate();

                List<Crime> crimeList = new LinkedList<>();

                crimeList.add(new Crime(20, 1, "a2", "type2", 200, 200, 1));
                crimeList.add(new Crime(30, 1, "a3", "type3", 300, 300, 2));
                crimeList.add(new Crime(40, 1, "a4", "type4", 400, 400, 3));

                for (Crime c : crimeList) {
                    String sql = "insert into TestCrimes(date, linkId, address, latitude, longitude, type) "
                            + "values (:dateParam, :linkIdParam, :addressParam, :latitudeParam, :longitudeParam, :typeParam)";

                    Query query = conn.createQuery(sql);
                    query.addParameter("dateParam", c.getDate()).addParameter("linkIdParam", c.getLinkId())
                            .addParameter("addressParam", c.getAddress()).addParameter("latitudeParam", c.getLat())
                            .addParameter("longitudeParam", c.getLng()).addParameter("typeParam", c.getType())
                            .executeUpdate();
                }

                double fromLng = 200;
                double toLng = 400;
                double fromLat = 200;
                double toLat = 400;
                int fromDate = 20;
                int toDate = 40;
                int timeOfDay = 1000;

                Crime from = new Crime(fromDate, fromLat, fromLng);
                Crime to = new Crime(toDate, toLat, toLng);
                //List<Crime> crimes = s.getCrimes(from, to, timeOfDay, "TestCrimes");

//              crimes.forEach(crime -> {
//                  assertTrue(crime.getLat() >= fromLat && crime.getLat() <= toLat
//                        && crime.getLng() >= fromLng && crime.getLng() <= toLng
//                        && crime.getDate() >= fromDate && crime.getDate() <= toDate);
//              });
            } catch (Sql2oException e) {
                logger.error("Failed to get crimes in ServerTest", e);
            }
        } catch (WalkLiveService.UserServiceException e) {
        logger.error("User Service Exception.");
        }
    }

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

    private static Sql2o db;

    private static void setupDB() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:walklive.db");

        db = new Sql2o(dataSource);

        try (Connection conn = db.open()) {
            String sql = "CREATE TABLE IF NOT EXISTS user (username TEXT, password TEXT, nickname TEXT, friendId TEXT, createdOn TIMESTAMP)" ;
            conn.createQuery(sql).executeUpdate();
        }
    }

    private List<User> getUsers(Response r) {
        //Getting a useful Type instance for a *generic* container is tricky given Java's type erasure.
        //The technique below is documented in the documentation of com.google.gson.reflect.TypeToken.
        Type type = (new TypeToken<ArrayList<User>>() { }).getType();
        return r.getContentAsObject(type);
    }

    /**
     * Clears the database of all test tables.
     * @return the clean database source
     */
    private SQLiteDataSource clearDB() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:walklive.db");

        Sql2o db = new Sql2o(dataSource);

        try (Connection conn = db.open()) {
            String sql = "DROP TABLE IF EXISTS TestCrimes";
            conn.createQuery(sql).executeUpdate();
            String sql2 = "DROP TABLE IF EXISTS TestSafetyRating";
            conn.createQuery(sql2).executeUpdate();
            String sql3 = "DROP TABLE IF EXISTS user" ;
            conn.createQuery(sql3).executeUpdate();
            sql3 = "CREATE TABLE IF NOT EXISTS user (username TEXT, password TEXT, nickname TEXT, friendId TEXT, createdOn TIMESTAMP)" ;
            conn.createQuery(sql3).executeUpdate();
        }

        return dataSource;
    }
}