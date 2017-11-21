package com.WalkLiveApp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Description;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
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

public class TestServer {

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
            Response rCreateNew = request("POST", "/WalkLive/api/user", t);
            System.out.println("USER: " + t.toString());
            assertEquals("Failed to create new User", 201, rCreateNew.httpStatus);
        }

        //Get them back
        Response r = request("GET", "/WalkLive/api/user", null);
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
    public void testDuplicateCreation() {

        //Add a few elements
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        User[] entries = new User[] {
                new User("jeesookim", "123456","4405339063"),
                new User("michelle", "0123", "4405339063")
        };

        //add to database
        for (User t : entries) {
            Response rCreateNew = request("POST", "/WalkLive/api/user", t);
            System.out.println("USER: " + t.toString());
            assertEquals("Failed to create new User", 201, rCreateNew.httpStatus);
        }

        //check if duplications are caught
        User u = new User("jeesookim", "1234567", "4405339063");

        Response rCreateDuplicate = request("POST", "/WalkLive/api/user", u);
        assertEquals("Failed to detect duplicate username", 401, rCreateDuplicate.httpStatus);

        //try another user duplication
        User u2 = new User("michelle", "123456", "4405339063");
        Response rCreateDuplicate2 = request("POST", "/WalkLive/api/user", u2);
        assertEquals("Failed to detect duplicate username", 401, rCreateDuplicate2.httpStatus);

        //Get them back
        Response s = request("GET", "/WalkLive/api/user", null);
        assertEquals("Failed to get user entries", 200, s.httpStatus);
        List<User> results = getUsers(s);

        //Verify that we got the right element back - should be two users in entries, and the results should be size 2
        assertEquals("Number of user entries differ", entries.length, results.size());
    }

//    @Test
//    public void testShortUsername() {
//        User u = new User("jeesoo", "123456", "4405339063");
//        Response rCreateNew = request("POST", "/WalkLive/api/user", u);
//    }

    // date: df.parse("2016-04-23T23:10:15-0720")

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
            Response rCreateNew = request("POST", "/WalkLive/api/user", t);
            assertEquals("Failed to create new User", 201, rCreateNew.httpStatus);
        }

        //Get them back
        Response r = request("GET", "/WalkLive/api/user", null);
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
    public void testLogin() throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

        //add single element
        User expected = new User("jeesoo", "test-1", "4405339063");
        Response r1 = request("POST", "/WalkLive/api/user", expected);
        assertEquals("Failed to add new user", 201, r1.httpStatus);

        //Get it back so that we know its ID
        Response r2 = request("GET", "/WalkLive/api/user/login", null);
        assertEquals("Failed to get users", 200, r2.httpStatus);
        User u = getUsers(r2).get(0);

        assertEquals("Mismatch in username", u.getUsername(), expected.getUsername());
        assertEquals("Mismatch in password", u.getPassword(), expected.getPassword());
        assertEquals("Mismatch in creation date", u.getCreatedOn(), expected.getCreatedOn());
        assertEquals("Mismatch in friendId", u.getFriendId(), expected.getFriendId());
        assertEquals("Mismatch in nickname", u.getNickname(), expected.getNickname());

    }
//
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

    //------------------------------------------------------------------------//
    // TodoApp Specific Helper Methods and classes
    //------------------------------------------------------------------------//

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
    private  void clearDB() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:walklive.db");

        try (Connection conn = db.open()) {
            String sql = "DROP TABLE IF EXISTS user" ;
            conn.createQuery(sql).executeUpdate();
            sql = "CREATE TABLE IF NOT EXISTS user (username TEXT, password TEXT, nickname TEXT, friendId TEXT, createdOn TIMESTAMP)" ;
            conn.createQuery(sql).executeUpdate();
        }
    }

    private List<User> getUsers(Response r) {
        //Getting a useful Type instance for a *generic* container is tricky given Java's type erasure.
        //The technique below is documented in the documentation of com.google.gson.reflect.TypeToken.
        Type type = (new TypeToken<ArrayList<User>>() { }).getType();
        return r.getContentAsObject(type);
    }
}