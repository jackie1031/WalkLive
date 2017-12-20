package com.WalkLiveApp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.dbcp2.BasicDataSource;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

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

import javax.sql.DataSource;

import static org.junit.Assert.*;
import static spark.Spark.get;
import static spark.Spark.put;

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


    @Before
    public void setup() throws Exception {
        //Clear the database
        clearDB();

    }


    @AfterClass
    public static void tearDownAfterClass() {
        //Stop the server

        Spark.stop();
    }
    //------------------------------------------------------------------------//
    // Setup
    //------------------------------------------------------------------------//


//    @After
//    public void tearDown() {
//        clearDB();
//        Spark.stop();
//    }

    //------------------------------------------------------------------------//
    // Tests
    //------------------------------------------------------------------------//

    @Test
    public void testDataSource() throws Exception{
            WalkLiveService.logger.info("org.apache.commons.dbcp2.BasicDataSource found "
                    + Thread.currentThread()
                    .getContextClassLoader()
                    .loadClass(
                            "org.apache.commons.dbcp2.BasicDataSource")
                    .getProtectionDomain().getCodeSource()
                    .getLocation());
            WalkLiveService.logger.info("org.apache.commons.pool2.impl.GenericObjectPool found "
                    + Thread.currentThread()
                    .getContextClassLoader()
                    .loadClass(
                            "org.apache.commons.pool2.impl.GenericObjectPool")
                    .getProtectionDomain().getCodeSource()
                    .getLocation());
    }


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

        //log in with those credentials
        Response r2 = request("POST", "/WalkLive/api/users/login", expected);
        assertEquals("Failed to post and authenticate login request", 200, r2.httpStatus);

        //test for nonexistent user
        User fake = new User("fakenews", "test-1", null);
        Response r3 = request("POST", "/WalkLive/api/users/login", fake);
        assertEquals("Failed to detect nonexistent user", 401, r3.httpStatus);

        //test for incorrect password
        User incorrect = new User("jeesoo", "incorrectpassword", null);
        Response r4 = request("POST", "/WalkLive/api/users/login", incorrect);
        assertEquals("Failed to detect incorrect password", 401, r4.httpStatus);
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
    }


//
    /**
     * ================================================================
     * Friend Request Handling
     * ================================================================
     */
//
//
//     @Test
//     public void testCreateFriendRequest() throws Exception {
//         SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//         User[] entries = new User[] {
//                 new User("jeesookim", "123456","4405339063"),
//                 new User("michelle", "0123", "4405339063"),
//                 new User("yangcao1", "121212", "1231231233")
//         };
//
//         //add to database
//         for (User t : entries) {
//             Response rCreateNew = request("POST", "/WalkLive/api/users", t);
//             assertEquals("Failed to create new User", 201, rCreateNew.httpStatus);
//         }
//
//         //add a few elements
//         Relationship[] frs = new Relationship[] {
//                 new Relationship("jeesookim", "michelle", null),
//                 new Relationship("jeesookim", "yangcao1", null)
//         };
//
//         for (Relationship f : frs) {
//             Response rCreateFR = request("POST", "/WalkLive/api/users/jeesookim/friend_requests", f);
//
//
//             assertEquals("Failed to create new friend request", 201, rCreateFR.httpStatus);
//         }
//
//         //check content of friendrequests in database!! list request id and stuff
//         //Get them back
//         Response r = request("GET", "/WalkLive/api/users/jeesookim", null);
//         assertEquals("Failed to get user entries", 200, r.httpStatus);
//     }

//    @Test
//    public void testGetOutgoingFriendRequests() throws Exception {
//        User[] entries = new User[] {
//                new User("jeesookim", "123456","4405339063"),
//                new User("michelle", "0123", "4405339063"),
//                new User("yangcao1", "121212", "1231231233")
//        };
//
//        //add to database
//        for (User t : entries) {
//            Response rCreateNew = request("POST", "/WalkLive/api/users", t);
//            assertEquals("Failed to create new User", 201, rCreateNew.httpStatus);
//        }
//        //add a few elements
//        Relationship[] frs = new Relationship[] {
//                new Relationship("jeesookim", "michelle", null),
//                new Relationship("jeesookim", "yangcao1", null)
//        };
//
//        for (Relationship f : frs) {
//            Response rCreateFR = request("POST", "/WalkLive/api/users/jeesookim/friend_requests", f);
//            assertEquals("Failed to create new friend request", 201, rCreateFR.httpStatus);
//        }
//
//        Relationship[] frs2 = new Relationship[] {
//                new Relationship("michelle", "jeesookim", null),
//                new Relationship("michelle", "yangcao1", null)
//        };
//
//        for (Relationship f : frs2) {
//            Response rCreateFR = request("POST", "/WalkLive/api/users/michelle/friend_requests", f);
//            assertEquals("Failed to create new friend request", 201, rCreateFR.httpStatus);
//        }
//
//        Response r = request("GET", "/WalkLive/api/users/jeesookim/sent_friend_requests", null);
//        List<Relationship> results = getRelationships(r);
//
//        assertEquals("Number of user entries differ", frs.length, results.size());
//
//        for (int i = 0; i < results.size(); i++) {
//            Relationship actual = results.get(i);
//
//            assertEquals("Not returning the outgoing requests", actual.getSender(), "jeesookim");
//            assertEquals("Mismatch in recipient", frs[i].getRecipient(), actual.getRecipient());
//            assertEquals("Mismatch in relationship", frs[i].getRelationship(), actual.getRelationship());
//        }
//    }

//    @Test
//    public void testGetIncomingFriendRequests() throws Exception {
//        User[] entries = new User[] {
//                new User("jeesookim", "123456","4405339063"),
//                new User("michelle", "0123", "4405339063"),
//                new User("yangcao1", "121212", "1231231233")
//        };
//
//        //add to database
//        for (User t : entries) {
//            Response rCreateNew = request("POST", "/WalkLive/api/users", t);
//            assertEquals("Failed to create new User", 201, rCreateNew.httpStatus);
//        }
//
//        Relationship[] frs = new Relationship[] {
//                new Relationship("michelle", "jeesookim", null),
//                new Relationship("yangcao1", "jeesookim", null)
//        };
//
//        Response rCreateFR = request("POST", "/WalkLive/api/users/michelle/friend_requests", frs[0]);
//        assertEquals("Failed to create new friend request", 201, rCreateFR.httpStatus);
//        rCreateFR = request("POST", "/WalkLive/api/users/yangcao1/friend_requests", frs[1]);
//        assertEquals("Failed to create new friend request", 201, rCreateFR.httpStatus);
//
//        Response r = request("GET", "/WalkLive/api/users/jeesookim/friend_requests", null);
//        List<Relationship> results = getRelationships(r);
//
//        assertEquals("Number of entries differ", frs.length, results.size());
//
//        for (int i = 0; i < results.size(); i++) {
//            Relationship actual = results.get(i);
//            assertEquals("Not returning the incoming requests", actual.getRecipient(), "jeesookim");
//            assertEquals("Mismatch in sender", frs[i].getSender(), actual.getSender());
//            assertEquals("Mismatch in relationship", frs[i].getRelationship(), actual.getRelationship());
//        }
//    }

//    @Test
//    public void testGetNewRequestId() throws Exception {
//        User[] entries = new User[] {
//                new User("jeesookim", "123456","4405339063"),
//                new User("michelle", "0123", "4405339063"),
//                new User("yangcao1", "121212", "1231231233")
//        };
//
//        //add to database
//        for (User t : entries) {
//            Response rCreateNew = request("POST", "/WalkLive/api/users", t);
//            assertEquals("Failed to create new User", 201, rCreateNew.httpStatus);
//        }
//        //add a few elements
//        Relationship[] frs = new Relationship[] {
//                new Relationship("jeesookim", "michelle", null),
//                new Relationship("jeesookim", "yangcao1", null)
//        };
//
//        for (Relationship f : frs) {
//            Response rCreateFR = request("POST", "/WalkLive/api/users/jeesookim/friend_requests", f);
//            assertEquals("Failed to create new friend request", 201, rCreateFR.httpStatus);
//        }
//
//        Response r = request("GET", "/WalkLive/api/users/jeesookim/sent_friend_requests", null);
//        List<Relationship> results = getRelationships(r);
//
//        assertEquals("Number of user entries differ", frs.length, results.size());
//
//        for (int i = 0; i < results.size(); i++) {
//            Relationship actual = results.get(i);
//            assertEquals("Request IDs do not match", actual.getId(), i + 1);
//        }
//    }
//
//    @Test
//    public void testRespondToFriendRequest() throws Exception {
//        User[] entries = new User[] {
//                new User("jeesookim", "123456","4405339063"),
//                new User("michelle", "0123", "4405339063"),
//                new User("yangcao1", "121212", "1231231233")
//        };
//
//        //add to database
//        for (User t : entries) {
//            Response rCreateNew = request("POST", "/WalkLive/api/users", t);
//            assertEquals("Failed to create new User", 201, rCreateNew.httpStatus);
//        }
//        //add a few elements
//        Relationship[] frs = new Relationship[] {
//                new Relationship("jeesookim", "michelle", null),
//                new Relationship("jeesookim", "yangcao1", null),
//        };
//
//        for (Relationship f : frs) {
//            Response rCreateFR = request("POST", "/WalkLive/api/users/jeesookim/friend_requests", f);
//            assertEquals("Failed to create new friend request", 201, rCreateFR.httpStatus);
//        }
//
//        Relationship[] frs2 = new Relationship[] {
//                new Relationship("michelle", "yangcao1", null)
//        };
//
//        for (Relationship f : frs2) {
//            Response rCreateFR = request("POST", "/WalkLive/api/users/michelle/friend_requests", f);
//            assertEquals("Failed to create new friend request", 201, rCreateFR.httpStatus);
//        }
//
//        Response response = request("GET", "/WalkLive/api/users/yangcao1/friend_requests", null);
//        List<Relationship> res = getRelationships(response);
//        assertEquals("Request relationship have not all been added", res.size(), 2);
//
//        //test accept
//        Response r = request("PUT", "/WalkLive/api/users/yangcao1/friend_requests/3/accept", null);
//        assertEquals("Failed to accept friend request", 200, r.httpStatus);
//
//        //test reject
//        Response r2 = request("PUT", "/WalkLive/api/users/yangcao1/friend_requests/2/reject", null);
//        assertEquals("Failed to reject friend request.", 200, r2.httpStatus);
//
//    }
//
//    @Test
//    public void testGetFriendList() throws Exception{
//        User[] entries = new User[] {
//                new User("jeesookim", "123456","4405339063"),
//                new User("michelle", "0123", "4405339063"),
//                new User("yangcao1", "121212", "1231231233")
//        };
//
//        //add to database
//        for (User t : entries) {
//            Response rCreateNew = request("POST", "/WalkLive/api/users", t);
//            assertEquals("Failed to create new User", 201, rCreateNew.httpStatus);
//        }
//        //add a few elements
//        Relationship[] frs = new Relationship[] {
//                new Relationship("jeesookim", "michelle", null),
//                new Relationship("jeesookim", "yangcao1", null),
//        };
//
//        for (Relationship f : frs) {
//            Response rCreateFR = request("POST", "/WalkLive/api/users/jeesookim/friend_requests", f);
//            assertEquals("Failed to create new friend request", 201, rCreateFR.httpStatus);
//        }
//
//        Relationship[] frs2 = new Relationship[] {
//                new Relationship("michelle", "jeesookim", null),
//                new Relationship("michelle", "yangcao1", null)
//        };
//
//        for (Relationship f : frs2) {
//            Response rCreateFR = request("POST", "/WalkLive/api/users/michelle/friend_requests", f);
//            assertEquals("Failed to create new friend request", 201, rCreateFR.httpStatus);
//        }
//
//        Response r = request("PUT", "/WalkLive/api/users/yangcao1/friend_requests/4/accept", null);
//        assertEquals("Failed to accept friend request", 200, r.httpStatus);
//
//
//        Response rList = request("GET", "/WalkLive/api/users/yangcao1/friends", null);
//        List<User> results = getUsers(rList);
//
//
//        Response rList2 = request("GET", "/WalkLive/api/users/michelle/friends", null);
//        List<User> results2 = getUsers(rList2);
//
//
//        User actual = results.get(0);
//        User actual2 = results2.get(0);
//        assertEquals("Friend list does not return your friends", "michelle", actual.getUsername());
//        assertEquals("Friend list does not return your friends", "yangcao1", actual2.getUsername());
//    }

    /**
     * ================================================================
     * Emergency Contact PUT
     * ================================================================
     */

    @Test
    public void testUpdateEmergencyInfo() throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //add single element
        User expected = new User("bubu", "test-1", "4405339063");
        Response r1 = request("POST", "/WalkLive/api/users", expected);
        User expected2 = new User("kuku", "test-1", "1231231233");
        Response r2 = request("POST", "/WalkLive/api/users", expected2);
        assertEquals("Failed to add new user", 201, r1.httpStatus);
        assertEquals("Failed to add new user", 201, r2.httpStatus);


        //update emergency contact info
        User contactInfo = new User(null, null, null, null, null, "kuku", "1231231233");
        User contactInfo2 = new User(null, null, null, null, null, "kuku", "");
        Response r3 = request("PUT", "/WalkLive/api/users/bubu/emergency_info", contactInfo);
        Response r4 = request("PUT", "/WalkLive/api/users/bubu/emergency_info", contactInfo2);
        assertEquals("Failed to get user", 200, r3.httpStatus);
        assertEquals("Failed to get user", 200, r4.httpStatus);
    }

    /**
     * ================================================================
     * User Contact PUT
     * ================================================================
     */

    @Test
    public void testUpdateUserContact() throws Exception {
        //add single element
        User expected = new User("jaja", "test-1", "440-533-9063");
        Response r1 = request("POST", "/WalkLive/api/users", expected);
        User expected2 = new User("hehe", "test-2", "123-123-1233");
        Response r2 = request("POST", "/WalkLive/api/users", expected2);
        assertEquals("Failed to add new user", 201, r1.httpStatus);
        assertEquals("Failed to add new user", 201, r2.httpStatus);


        //update emergency contact info
        User contactInfo = new User(null, null, "440-533-9063", null, null, null, null);
        User contactInfo2 = new User(null, null, "440-123-1111", null, null, null, null);
        Response r3 = request("PUT", "/WalkLive/api/users/jaja/contact_info", contactInfo);
        Response r4 = request("PUT", "/WalkLive/api/users/hehe/contact_info", contactInfo2);
        assertEquals("Failed to update user contact", 200, r3.httpStatus);
        assertEquals("Failed to update user contact second time", 200, r4.httpStatus);
    }

    /**
     * ================================================================
     * Trips
     * ================================================================
     */

    @Test
    public void testStartTripByID() throws Exception {
        Trip test = new Trip("jackie","JHU","12", false,11.11,22.22,11.11,22.22,77.77,88.88,"18611345670","3hours", "address");
        //Trip tryit = new Trip(1,"jackie","liam","JHU","12", false,11.11,22.22,11.11,22.22,77.77,88.88,"18611345670","3hours", "address");

        //start trip
        Response r1 = request("POST", "/WalkLive/api/trips", test);
        assertEquals("unidentified destination ", 200, r1.httpStatus);


        //username
        Response r2 = request("GET", "/WalkLive/api/trips/getById/1", null);
        //assertEquals("TEST THE TRIP ID", 200, r2.httpStatus);

        assertEquals("Failed to get trip", 200, r2.httpStatus);


        Trip test1 = new Trip("michelle","xyz","12", false,11.11,22.22,11.11,22.22,77.77,88.88,"18611345670","3hours", "address");

        Response r3 = request("POST", "/WalkLive/api/trips", test1);
        assertEquals("unidentified destination ", 200, r3.httpStatus);

        Response r4 = request("GET", "/WalkLive/api/trips/getById/2", null);
        //assertEquals("TEST THE TRIP ID", 200, r2.httpStatus);

        assertEquals("Failed to get user", 200, r4.httpStatus);

    }

//
//    @Test
//    public void testUpdateTrip() throws Exception{
//
//        Trip trip = new Trip("jeesookim","JHU","12", false,11.11,22.22,11.11,22.22,77.77,88.88,"18611345670","3hours", "address");
//
//        Response r1 = request("POST", "/WalkLive/api/trips", trip);
//        assertEquals("unidentified destination ", 200, r1.httpStatus);
//
//        Trip updateLocation = new Trip(23,null,null,null,false,1.1,2.2,123.123,456.456,1.1,1.1,null,"8hours","address");
//
//        Response r2 = request("PUT", "/WalkLive/api/trips/1/update", updateLocation);
//        assertEquals("Failed to get user", 200, r2.httpStatus);
//    }

    @Test
    public void testTripDangerLevel() throws Exception {
        Trip test = new Trip("jackie", "JHU", "12", false, 11.11, 22.22, 38.982012186658675, -76.94064758017299, 77.77, 88.88, "18611345670", "3hours", "3501 St Paul");
        //Trip tryit = new Trip(1,"jackie","liam","JHU","12", false,11.11,22.22,11.11,22.22,77.77,88.88,"18611345670","3hours");

        //1,38.9846371,-76.9661682,0.018,12

        //start trip
        Response r1 = request("POST", "/WalkLive/api/trips", test);
        assertEquals("unidentified destination ", 200, r1.httpStatus);

        //by id
        Response r2 = request("GET", "/WalkLive/api/trips/getById/1", null);
        assertEquals("TEST THE TRIP ID", 200, r2.httpStatus);
        //assertEquals("Failed to get user", 200, r2.httpStatus);

        //Trip update = new Trip(1,null, null, null, false, 0, 0, 38.9846379, -76.9661682, 0, 0, "18611345670", "3hours", "3501 St Paul");

        Response r3 = request("GET", "/WalkLive/api/crime/38.982012186658675/-76.94064758017299/0", null);
        assertEquals("Failed to get user", 200, r3.httpStatus);

    }

//
//    @Test
//    public void testTripDangerLevelOnly() throws Exception {
//        Trip test = new Trip("jackie", "JHU", "12", false, 11.11, 22.22, 38.982012186658675, -76.94064758017299, 77.77, 88.88, "18611345670", "3hours", "3501 St Paul");
//        //Trip tryit = new Trip(1,"jackie","liam","JHU","12", false,11.11,22.22,11.11,22.22,77.77,88.88,"18611345670","3hours");
//
//        //1,38.9846371,-76.9661682,0.018,12
//
//        //start trip
//        Response r1 = request("POST", "/WalkLive/api/trips", test);
//        assertEquals("unidentified destination ", 200, r1.httpStatus);
//
//        //by id
//        Response r2 = request("GET", "/WalkLive/api/trips/getById/1", null);
//        assertEquals("TEST THE TRIP ID", 200, r2.httpStatus);
//        //assertEquals("Failed to get user", 200, r2.httpStatus);
////        get(API_CONTEXT + "/crime/clusterOnly/:curLat/:curLong/:isDay", "application/json", (request, response) -> {
//        //Response r3 = request("GET", "/WalkLive/api/crime/38.982012186658675/-76.94064758017299/0", null);
//        Response r3 = request("GET", "/WalkLive/api/crime/clusterOnly/38.982012186658675/-76.94064758017299/0", null);
//        assertEquals("Failed to get user", 200, r3.httpStatus);
//
//    }


    @Test
    public void testgetTripByName() throws Exception {
        Trip test = new Trip(1,"jackie", "JHU", "12", false ,11.11, 22.22, 11.11, 22.22, 77.77, 88.88, "18611345670", "3hours", "address");
        //Trip tryit = new Trip(1,"jackie","liam","JHU","12", false,11.11,22.22,11.11,22.22,77.77,88.88,"18611345670","3hours", "address");

        //start trip
        Response r1 = request("POST", "/WalkLive/api/trips", test);
        assertEquals("unidentified destination ", 200, r1.httpStatus);

        Response r2 = request("GET", "/WalkLive/api/trips/getByName/jackie", null);
        //assertEquals("TEST THE TRIP ID", 200, r2.httpStatus);

        assertEquals("Failed to get user", 200, r2.httpStatus);


        Trip test1 = new Trip(2,"michelle", "xyz", "12", false, 11.11, 22.22, 11.11, 22.22, 77.77, 88.88, "18611345670", "3hours", "address");


        Response r3 = request("POST", "/WalkLive/api/trips", test1);
        assertEquals("unidentified destination ", 200, r3.httpStatus);

        Response r4 = request("GET", "/WalkLive/api/trips/getByName/michelle", null);

        //assertEquals("TEST THE TRIP ID", 200, r2.httpStatus);

    assertEquals("Failed to get user",200,r4.httpStatus);

}



//    @Test
//    public void testgetTripByName() throws Exception {
//        Trip test = new Trip("jackie","JHU","12", false,11.11,22.22,11.11,22.22,77.77,88.88,"18611345670","3hours");
//        //Trip tryit = new Trip(1,"jackie","liam","JHU","12", false,11.11,22.22,11.11,22.22,77.77,88.88,"18611345670","3hours");
//
//        //start trip
//        Response r1 = request("POST", "/WalkLive/api/trips", test);
//        assertEquals("unidentified destination ", 200, r1.httpStatus);
//
//        Response r2 = request("GET", "/WalkLive/api/trips/getByName/jackie", null);
//        //assertEquals("TEST THE TRIP ID", 200, r2.httpStatus);
//
//        assertEquals("Failed to get user", 200, r2.httpStatus);
//
//
//        Trip test1 = new Trip("michelle","xyz","12", false,11.11,22.22,11.11,22.22,77.77,88.88,"18611345670","3hours");
//
//        Response r3 = request("POST", "/WalkLive/api/trips", test1);
//        assertEquals("unidentified destination ", 200, r3.httpStatus);
//
//        Response r4 = request("GET", "/WalkLive/api/trips/getByName/michelle", null);
//        //assertEquals("TEST THE TRIP ID", 200, r2.httpStatus);
//
//        assertEquals("Failed to get user", 200, r4.httpStatus);
//
//    }

//    @Test
//    public void testEndTrip() throws Exception {
//        WalkLiveService walkLiveService;
//
//        Trip test = new Trip("jackie","JHU","12", false,11.11,22.22,11.11,22.22,77.77,88.88,"18611345670","3hours", "address");
//        //walkLiveService.startTrip();
//        Response r1 = request("POST", "/WalkLive/api/trips", test);
//        assertEquals("unidentified destination ", 200, r1.httpStatus);
//        assertEquals(test.isCompleted(),false);
//
//        Response r2 = request("PUT", "/WalkLive/api/trips/getById/1", null);
//        assertEquals("Failed to get user", 200, r2.httpStatus);
//
//        //Response r3 = request("GET", "/WalkLive/api/trips/1", null);
//        //assertEqualsx(r3.isCompleted(),false);
//
//    }
//

//    @Test
//    public void testGetFriendList() throws Exception{
//        User[] entries = new User[] {
//                new User("jeesookim", "123456","4405339063"),
//                new User("michelle", "0123", "4405339063"),
//                new User("yangcao1", "121212", "1231231233")
//        };
//
//        //add to database
//        for (User t : entries) {
//            Response rCreateNew = request("POST", "/WalkLive/api/users", t);
//            assertEquals("Failed to create new User", 201, rCreateNew.httpStatus);
//        }
//
//        //add a few elements
//        Relationship[] frs = new Relationship[] {
//                new Relationship("jeesookim", "michelle", null),
//                new Relationship("jeesookim", "yangcao1", null),
//        };
//
//        for (Relationship f : frs) {
//            Response rCreateFR = request("POST", "/WalkLive/api/users/jeesookim/friend_requests", f);
//            assertEquals("Failed to create new friend request", 201, rCreateFR.httpStatus);
//        }
//
//        Relationship[] frs2 = new Relationship[] {
//                new Relationship("michelle", "jeesookim", null),
//                new Relationship("michelle", "yangcao1", null)
//        };
//
//        for (Relationship f : frs2) {
//            Response rCreateFR = request("POST", "/WalkLive/api/users/michelle/friend_requests", f);
//            assertEquals("Failed to create new friend request", 201, rCreateFR.httpStatus);
//        }
//
//        Response r = request("PUT", "/WalkLive/api/users/yangcao1/friend_requests/4/accept", null);
//        assertEquals("Failed to accept friend request", 200, r.httpStatus);
//
//        Response rList = request("GET", "/WalkLive/api/users/yangcao1/friends", null);
//        List<User> results = getUsers(rList);
//
//        User actual = results.get(0);
//        assertEquals("Friend list does not return your friends", "michelle", actual.getUsername());
//    }

//
    @Test
    public void testTripHistory() throws Exception {

        //WalkLiveService walkLiveService;

        Trip trip = new Trip("yangcao1", "JHU", "12", false, 11.11, 22.22, 11.11, 22.22, 77.77, 88.88, "110110110", "3hours", "address");
        Trip trip2 = new Trip("yangcao1", "xyz", "12", false, 11.11, 22.22, 11.11, 22.22, 77.77, 88.88, "120120120", "3hours", "address");
        //Trip trip3 = new Trip("jackie","malone","18", false,123,123,456,456,789,789,"119119119","3hours");

        Response startT1 = request("POST", "/WalkLive/api/trips", trip);
        assertEquals("unidentified destination ", 200, startT1.httpStatus);
        Response endT1 = request("PUT", "/WalkLive/api/trips/1/endtrip", null);
        assertEquals("Failed to get user", 200, endT1.httpStatus);

        Response startT2 = request("POST", "/WalkLive/api/trips", trip2);
        assertEquals("unidentified destination ", 200, startT2.httpStatus);
        Response endT2 = request("PUT", "/WalkLive/api/trips/2/endtrip", null);
        assertEquals("Failed to get user", 200, endT2.httpStatus);

        Response getHistory = request("GET", "/WalkLive/api/trips/yangcao1/tripHistory", null);
        assertEquals("Failed to get user", 200, getHistory.httpStatus);
    }


//
//    @Test
//    public void testGetAllTrips() throws Exception {
//
//        Trip trip = new Trip("jeesookim","JHU","12", false,11.11,22.22,11.11,22.22,77.77,88.88,"18611345670","3hours","address");
//        Trip trip2 = new Trip("yangcao1","xyz","12", false,11.11,22.22,11.11,22.22,77.77,88.88,"18611345670","3hours", "address");
//
//
//        ArrayList<Trip> allTrips = new ArrayList<>();
//        allTrips.add(trip);
//        allTrips.add(trip2);
//
//        Response r1 = request("POST", "/WalkLive/api/trips", trip);
//        assertEquals("unidentified destination ", 200, r1.httpStatus);
//
//        Response r2 = request("GET", "/WalkLive/api/trips/getById/1", null);
//
//        assertEquals("Failed to get user", 200, r2.httpStatus);
//
//        Response r3 = request("POST", "/WalkLive/api/trips", trip2);
//        assertEquals("unidentified destination ", 200, r3.httpStatus);
//
//        //Response r4 = request("GET", "/WalkLive/api/trips/2", null);
//
//
//        User[] entries = new User[] {
//                new User("jeesookim", "123456","4405339063"),
//                new User("michelle", "0123", "4405339063"),
//                new User("yangcao1", "121212", "1231231233")
//                //new User("jackie", "666666", "1231231233")
//        };
//
//        //add to database
//        for (User t : entries) {
//            Response rCreateNew = request("POST", "/WalkLive/api/users", t);
//            assertEquals("Failed to create new User", 201, rCreateNew.httpStatus);
//        }
//
//        //add a few elements
//        //Relationship[] frs = new Relationship[] {
//        Relationship test1 = new Relationship("jeesookim", "michelle", null);
//        Relationship test2 = new Relationship("michelle", "yangcao1", null);
//        //};
//
//        //for (Relationship f : frs) {
//        //send friend request
//        Response rCreateFR = request("POST", "/WalkLive/api/users/jeesookim/friend_requests", test1);
//        assertEquals("Failed to create new friend request", 201, rCreateFR.httpStatus);
//        Response rCreateFR2 = request("POST", "/WalkLive/api/users/michelle/friend_requests", test2);
//        assertEquals("Failed to create new friend request", 201, rCreateFR2.httpStatus);
//        //}
//
//        //accept friend request
//        Response rAccept = request("PUT", "/WalkLive/api/users/michelle/friend_requests/1/accept", null);
//        assertEquals("Failed to accept friend request", 200, rAccept.httpStatus);
//        Response rAccept2 = request("PUT", "/WalkLive/api/users/yangcao1/friend_requests/2/accept", null);
//        assertEquals("Failed to accept friend request", 200, rAccept2.httpStatus);
//
////        Response rList = request("GET", "/WalkLive/api/users/yangcao1/friends", null);
////        List<User> results = getUsers(rList);
//
//        Response rList = request("GET", "/WalkLive/api/trips/michelle/allTrips", null);
//
//        List<Trip> results = getAllTrip(rList);
//
//        assertEquals("Number of trips entries differ", allTrips.size(), results.size());
//
//
//    }



//
//
//    @Test
//    public void testGetDangerZone() throws Exception {
//
//        double lat = 3.454;
//        double lng = 6.929;
//        Coordinate c = new Coordinate(lat, lng);
//
//        Crime[] z1 = new Crime[]{
//                new Crime(1025, 18, "JHU malone", "Robbery", c, 23523523),
//                new Crime(1128, 18, "JHU levering", "Sexual", c, 4405339),
//        };
//        Crime[] z2 = new Crime[]{
//
//                new Crime(1126, 18, "JHU brody", "Sexual", c, 24124124),
//                new Crime(1127, 18, "JHU shaffer", "Sexual", c, 24124224)
//        };
//
//        Crime[] z3 = z2;
//
//        DangerZone testDanger = new DangerZone(z1,z2,z3);
//
//        Crime testZ1 = new Crime(1025, 18, "JHU malone", "Robbery", c, 23523523);
//        Crime testZ2 = new Crime(1128, 18, "JHU levering", "Sexual", c, 4405339);
//        Crime testZ3 = new Crime(1128, 18, "JHU levering", "Sexual", c, 4405339);
//        Crime testZ4 = new Crime(1128, 18, "JHU levering", "Sexual", c, 4405339);
//        Crime testZ5 = new Crime(1128, 18, "JHU levering", "Sexual", c, 4405339);
//        Crime testZ6 = new Crime(1128, 18, "JHU levering", "Sexual", c, 4405339);
//        Crime[] testForZone1 = {testZ1,testZ2,testZ3,testZ4,testZ5,testZ6};
//
//        assertEquals("Number of user entries differ", testDanger.size(), testForZone1.length);
//
//
//        //assertEquals();
//
//
//        for (Crime t : z1) {
//            Response rCreateNew = request("GET", "/WalkLive/api/getdangerzone", t);
//            //System.out.println("USER: " + t.toString());
//            assertEquals("Failed to create new User", 404, rCreateNew.httpStatus);
//        }
//
//
//
//    }


//------------------------------------------------------------------------//
// Generic Helper Methods and classes
//------------------------------------------------------------------------//

//------------------------------------------------------------------------//
// Generic Helper Methods and classes
//------------------------------------------------------------------------//

private Response request(String method,String path,Object content){
    try{
        URL url=new URL("http",Bootstrap.IP_ADDRESS,Bootstrap.PORT,path);
        System.out.println(url);
        HttpURLConnection http=(HttpURLConnection)url.openConnection();
        http.setRequestMethod(method);
        http.setDoInput(true);
        if(content!=null){
        String contentAsJson=new Gson().toJson(content);
        http.setDoOutput(true);
        http.setRequestProperty("Content-Type","application/json");
        OutputStreamWriter output=new OutputStreamWriter(http.getOutputStream());
        output.write(contentAsJson);
        output.flush();
        output.close();
        }
        try{
        String responseBody=IOUtils.toString(http.getInputStream());
        return new Response(http.getResponseCode(),responseBody);
        }catch(IOException e){
//                e.printStackTrace();
//                fail("Sending request failed: " + e.getMessage());
        return new Response(http.getResponseCode(),"ERROR"); //still return the http status code for testing sake
        }
    }catch(IOException e){
        e.printStackTrace();
        fail("Sending request failed: "+e.getMessage());
        return null;
    }
}

private Response request(String method,String path,String content){
    try{
        URL url=new URL("http",Bootstrap.IP_ADDRESS,Bootstrap.PORT,path);
        System.out.println(url);
        HttpURLConnection http=(HttpURLConnection)url.openConnection();
        http.setRequestMethod(method);
        http.setDoInput(true);
        if(content!=null){
        http.setDoOutput(true);
        http.setRequestProperty("Content-Type","application/json");
        OutputStreamWriter output=new OutputStreamWriter(http.getOutputStream());
        output.write(content);
        output.flush();
        output.close();
        }
        try{
        String responseBody=IOUtils.toString(http.getInputStream());
        return new Response(http.getResponseCode(),responseBody);
        }catch(IOException e){
//                e.printStackTrace();
//                fail("Sending request failed: " + e.getMessage());
        return new Response(http.getResponseCode(),"ERROR"); //still return the http status code for testing sake
        }
    }catch(IOException e){
        e.printStackTrace();
        fail("Sending request failed: "+e.getMessage());
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

    //
//    // ------------------------------------------------------------------------//
//    // Survival Maps Specific Helper Methods and classes
//    // ------------------------------------------------------------------------//
//
//
    private List<User> getUsers(Response r) {
        //Getting a useful Type instance for a *generic* container is tricky given Java's type erasure.
        //The technique below is documented in the documentation of com.google.gson.reflect.TypeToken.
        Type type = (new TypeToken<ArrayList<User>>() {
        }).getType();
        return r.getContentAsObject(type);
    }

    private List<Relationship> getRelationships(Response r) {
        //Getting a useful Type instance for a *generic* container is tricky given Java's type erasure.
        //The technique below is documented in the documentation of com.google.gson.reflect.TypeToken.
        Type type = (new TypeToken<ArrayList<Relationship>>() {
        }).getType();
        return r.getContentAsObject(type);
    }

    private List<Trip> getTrip(Response r) {
        //Getting a useful Type instance for a *generic* container is tricky given Java's type erasure.
        //The technique below is documented in the documentation of com.google.gson.reflect.TypeToken.
        Type type = (new TypeToken<ArrayList<Trip>>() {
        }).getType();
        return r.getContentAsObject(type);
    }

//   private List<User> getUsers(Response r) {
//        //Getting a useful Type instance for a *generic* container is tricky given Java's type erasure.
//        //The technique below is documented in the documentation of com.google.gson.reflect.TypeToken.
//        Type type = (new TypeToken<ArrayList<User>>() { }).getType();
//        return r.getContentAsObject(type);
//    }

    private List<Trip> getAllTrip(Response r) {
        Type type = (new TypeToken<ArrayList<Trip>>() {
        }).getType();
        return r.getContentAsObject(type);
    }


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
            String setup2 = "CREATE TABLE IF NOT EXISTS friends (_id INT, sender TEXT, recipient TEXT, relationship INT, sent_on TIMESTAMP)" ;
            String setup3 = "CREATE TABLE IF NOT EXISTS counters (friend_request_ids INT DEFAULT 0,trip_ids INT DEFAULT 0,crimes_id INT DEFAULT 0)";
            String counterInit = "INSERT INTO counters (friend_request_ids) VALUES (0)";
            //String setup4 = "CREATE TABLE IF NOT EXISTS Trips(tripId INT, username TEXT, shareTo TEXT, destination TEXT, dangerLevel INT, startTime TEXT, completed BOOL, startLat DOUBLE, startLong DOUBLE, curLat DOUBLE, curLong DOUBLE, endLat DOUBLE, endLong DOUBLE, emergencyNum TEXT, timeSpent TEXT)";
            String sqlNew3 = "CREATE TABLE IF NOT EXISTS ongoingTrips(tripId INT, username TEXT, destination TEXT, dangerLevel INT, startTime TEXT, completed BOOL, startLat DOUBLE, startLong DOUBLE, curLat DOUBLE, curLong DOUBLE, endLat DOUBLE, endLong DOUBLE, emergencyNum TEXT, timeSpent TEXT, address TEXT)";
            String sqlNew4 = "CREATE TABLE IF NOT EXISTS doneTrips(tripId INT, username TEXT, destination TEXT, dangerLevel INT, startTime TEXT, completed BOOL, startLat DOUBLE, startLong DOUBLE, curLat DOUBLE, curLong DOUBLE, endLat DOUBLE, endLong DOUBLE, emergencyNum TEXT, timeSpent TEXT, address TEXT)";
            //String sqlNew7 = "CREATE TABLE IF NOT EXISTS Crime(incident_id TEXT, address_1 TEXT, address_2 TEXT, latitude DOUBLE, longitude DOUBLE, hour_of_day INT,incident_description TEXT, parent_incident_type TEXT)";
            String sqlNew8 = "CREATE TABLE IF NOT EXISTS dangerZonesDay(cluster_id TEXT, longitude DOUBLE, latitude DOUBLE, radius DOUBLE, count INT, dangerLevel INT, day_or_night TEXT)";

            String sqlNew9 = "CREATE TABLE IF NOT EXISTS dangerZonesNight(cluster_id TEXT, longitude DOUBLE, latitude DOUBLE, radius DOUBLE, count INT, dangerLevel INT, day_or_night TEXT)";


//            String sqlInsertClusters = " INSERT INTO dangerZones(cluster_id, longitude, latitude, radius, count,dangerLevel, day_or_night) " +
//                    "VALUES (1,-76.9541682,38.99,0.01,671,1,'day') ";
//
//            String sqlInsertClusters1 = " INSERT INTO dangerZones(cluster_id, longitude, latitude, radius,count, dangerLevel,day_or_night) " +
//                    "VALUES (2,-76.9561688,38.9846379,0.02,1827,2,'day') ";
//
//            String sqlInsertClusters2 = " INSERT INTO dangerZones(cluster_id, longitude, latitude, radius,count, dangerLevel,day_or_night) " +
//                    "VALUES (3,-76.9561688,39.9846379,0.03,994,3,'day') ";
//
//            String sqlInsertClusters3 = " INSERT INTO dangerZones(cluster_id, longitude, latitude, radius,count, dangerLevel,day_or_night) " +
//                    "VALUES (4,-76.9561688,39.9846379,0.04,994,4,'night') ";

            stm.executeUpdate(sqlNew3);
            stm.executeUpdate(sqlNew4);
            //stm.executeUpdate(sqlNew7);
            stm.executeUpdate(sqlNew8);
            stm.executeUpdate(sqlNew9);
//            stm.executeUpdate(sqlInsertClusters);
//            stm.executeUpdate(sqlInsertClusters1);
//            stm.executeUpdate(sqlInsertClusters2);
//            stm.executeUpdate(sqlInsertClusters3);

            stm.executeUpdate(setup);
            stm.executeUpdate(setup2);
            stm.executeUpdate(setup3);
            stm.executeUpdate(counterInit);
            //stm.executeUpdate(setup4);
            //stm.executeUpdate(setup5);
//
//            String sql = "DROP TABLE IF EXISTS TestCrimes";
//            stm.executeUpdate(sql);
            String sql2 = "DROP TABLE IF EXISTS TestSafetyRating";
            stm.executeUpdate(sql2);
            String sql3 = "DROP TABLE IF EXISTS users" ;
            stm.executeUpdate(sql3);
            String sql4 = "DROP TABLE IF EXISTS friends" ;
            stm.executeUpdate(sql4);
            String sql5 = "DROP TABLE IF EXISTS counters" ;
            stm.executeUpdate(sql5);

            String sql6 = "DROP TABLE IF EXISTS Trips" ;
            stm.executeUpdate(sql6);

            String sql7 = "DROP TABLE IF EXISTS ongoingTrips" ;
            stm.executeUpdate(sql7);

            String sql8 = "DROP TABLE IF EXISTS doneTrips" ;
            stm.executeUpdate(sql8);

//            String sql9 = "DROP TABLE IF EXISTS dangerZonesDay" ;
//            stm.executeUpdate(sql9);
//
//            String sql10 = "DROP TABLE IF EXISTS dangerZonesNight" ;
//            stm.executeUpdate(sql10);


        } catch (SQLException ex) {
            //logger.error("Failed to create schema at startup", ex);
            //throw new WalkLiveService.UserServiceException("Failed to create schema at startup");
        } finally {
            if (stm != null) {
                try {
                    stm.close();
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
    }

    //    /**
//     * Clears the database of all test tables.
//     * @return the clean database source
//     */
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
            String sql4 = "DROP TABLE IF EXISTS friends" ;
            stm.executeUpdate(sql4);
            //String sql5 = "DROP TABLE IF EXISTS Trips" ;
            //stm.executeUpdate(sql5);
            String sql6 = "DROP TABLE IF EXISTS counters" ;
            stm.executeUpdate(sql6);
            String sql7 = "DROP TABLE IF EXISTS ongoingTrips" ;
            stm.executeUpdate(sql7);
            String sql8 = "DROP TABLE IF EXISTS doneTrips" ;
            stm.executeUpdate(sql8);
//            String sql9 = "DROP TABLE IF EXISTS dangerZonesDay" ;
//            stm.executeUpdate(sql9);
//
//            String sql10 = "DROP TABLE IF EXISTS dangerZonesNight" ;
//            stm.executeUpdate(sql10);

            String sqlNew = "CREATE TABLE IF NOT EXISTS users (username TEXT, password TEXT, contact TEXT, nickname TEXT, created_on TIMESTAMP, emergency_id TEXT, emergency_number TEXT)" ;
            String sqlNew2 = "CREATE TABLE IF NOT EXISTS friends (_id INT, sender TEXT, recipient TEXT, relationship INT, sent_on TIMESTAMP)" ;
            //String sqlNew3 = "CREATE TABLE IF NOT EXISTS Trips(tripId INT, username TEXT, shareTo TEXT, destination TEXT, dangerLevel INT, startTime TEXT, completed BOOL not NULL, startLat DOUBLE, startLong DOUBLE, curLat DOUBLE, curLong DOUBLE, endLat DOUBLE, endLong DOUBLE, emergencyNum TEXT, timeSpent TEXT)";
            String sqlNew4 = "CREATE TABLE IF NOT EXISTS counters (friend_request_ids INT DEFAULT 0, trip_ids INT DEFAULT 0, crimes_id INT DEFAULT 0)";
            String counterInit = "INSERT INTO counters (friend_request_ids) VALUES (0)";

            String sqlNew5 = "CREATE TABLE IF NOT EXISTS ongoingTrips(tripId INT, username TEXT, destination TEXT, dangerLevel INT, startTime TEXT, completed BOOL, startLat DOUBLE, startLong DOUBLE, curLat DOUBLE, curLong DOUBLE, endLat DOUBLE, endLong DOUBLE, emergencyNum TEXT, timeSpent TEXT, address TEXT)";
            String sqlNew6 = "CREATE TABLE IF NOT EXISTS doneTrips(tripId INT, username TEXT, destination TEXT, dangerLevel INT, startTime TEXT, completed BOOL, startLat DOUBLE, startLong DOUBLE, curLat DOUBLE, curLong DOUBLE, endLat DOUBLE, endLong DOUBLE, emergencyNum TEXT, timeSpent TEXT, address TEXT)";
            //String sqlNew7 = "CREATE TABLE IF NOT EXISTS Crime(incident_id TEXT, address_1 TEXT, address_2 TEXT, latitude DOUBLE, longitude DOUBLE, hour_of_day INT,incident_description TEXT, parent_incident_type TEXT)";
//            String sqlNew8 = "CREATE TABLE IF NOT EXISTS dangerZonesDay(cluster_id TEXT, longitude DOUBLE, latitude DOUBLE, radius DOUBLE, count INT, dangerLevel INT, day_or_night TEXT)";
//            String sqlNew9 = "CREATE TABLE IF NOT EXISTS dangerZonesNight(cluster_id TEXT, longitude DOUBLE, latitude DOUBLE, radius DOUBLE, count INT, dangerLevel INT, day_or_night TEXT)";

            stm.executeUpdate(sqlNew);
            stm.executeUpdate(sqlNew2);
            //stm.executeUpdate(sqlNew3);
            stm.executeUpdate(sqlNew4);
            stm.executeUpdate(counterInit);
            stm.executeUpdate(sqlNew5);
            stm.executeUpdate(sqlNew6);
//            stm.executeUpdate(sqlNew8);
//            stm.executeUpdate(sqlNew9);


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


