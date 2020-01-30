package bg.sofia.uni.fmi.mjt.auth;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AuthenticationServerTest {

    private AuthenticationServer server;

    @Before
    public void setUp() {

        server = new AuthenticationServer(Constants.TEST_USERS_PATH_NAME);
    }

    @After
    public void free() {

        File file = new File(Constants.TEST_USERS_PATH_NAME);
        file.delete();
    }

    @Test
    public void givenExistingUserWhenGetUserIsInvokedThenReturnUser() {

        User user1 = new User("ivan97", "pass123PASS", "Ivan", "Petkov", "ivan.petkov@gmail.com");
        User user2 = new User("maria80", "qwerty123QWERTY", "Maria", "Blagoeva", "maria.blagoeva@gmail.com");
        User user3 = new User("mitko", "abc000abc", "Dimitar", "Parvanov", "dimitar.parvanov@gmail.com");

        server.addUser(user1);
        server.addUser(user2);
        server.addUser(user3);

        User user = server.getUser(user2.getUserName());

        assertEquals(user2.getUserName(), user.getUserName());
        assertEquals(user2.getPassword(), user.getPassword());
        assertEquals(user2.getFirstName(), user.getFirstName());
        assertEquals(user2.getLastName(), user.getLastName());
        assertEquals(user2.getEmail(), user.getEmail());
    }

    @Test
    public void givenNonExistingUserWhenGetUserIsInvokedThenReturnNull() {

        User user1 = new User("ivan97", "pass123PASS", "Ivan", "Petkov", "ivan.petkov@gmail.com");

        server.addUser(user1);

        User user = server.getUser("maria80");

        assertEquals(null, user);
    }

    @Test
    public void givenCorrectPasswordWhenIsPasswordCorrectIsInvokedThenReturnTrue() {

        User user1 = new User("ivan97", "pass123PASS", "Ivan", "Petkov", "ivan.petkov@gmail.com");
        User user2 = new User("maria80", "qwerty123QWERTY", "Maria", "Blagoeva", "maria.blagoeva@gmail.com");
        User user3 = new User("mitko", "abc000abc", "Dimitar", "Parvanov", "dimitar.parvanov@gmail.com");

        server.addUser(user1);
        server.addUser(user2);
        server.addUser(user3);

        assertEquals(true, server.isPasswordCorrect(user2, "qwerty123QWERTY"));
    }


    @Test
    public void givenMapOfUsersWhenSetUsersDatabaseIsInvokedThenReplaceUsersInDatabase() {

        User user1 = new User("ivan97", "pass123PASS", "Ivan", "Petkov", "ivan.petkov@gmail.com");
        User user2 = new User("maria80", "qwerty123QWERTY", "Maria", "Blagoeva", "maria.blagoeva@gmail.com");

        HashMap<String, User> inputUsers = new HashMap<>();
        inputUsers.put(user1.getUserName(), user1);
        inputUsers.put(user2.getUserName(), user2);

        server.setUsersDatabase(inputUsers);

        Map<String, User> outputUsers = server.getUsersDatabase();

        assertEquals(2, outputUsers.size());
        assertTrue(outputUsers.get("ivan97") != null);
        assertTrue(outputUsers.get("maria80") != null);
    }

    @Test
    public void givenExistingSessionWhenGetUserNameFromSessionIsInvokedThenReturnUserName() {

        User user1 = new User("ivan97", "pass123PASS", "Ivan", "Petkov", "ivan.petkov@gmail.com");
        User user2 = new User("maria80", "qwerty123QWERTY", "Maria", "Blagoeva", "maria.blagoeva@gmail.com");

        Session session1 = new Session(user1.getUserName(), Constants.TTL);
        Session session2 = new Session(user2.getUserName(), Constants.TTL);

        server.addSession(session1);
        server.addSession(session2);

        String userName1 = server.getUserNameFromSessionID(session1.getID());
        String userName2 = server.getUserNameFromSessionID(session2.getID());

        assertEquals(user1.getUserName(), userName1);
        assertEquals(user2.getUserName(), userName2);
    }

    @Test
    public void givenExistingSessionWhenRemoveSessionIsInvokedThenRemoveSession() {

        User user1 = new User("ivan97", "pass123PASS", "Ivan", "Petkov", "ivan.petkov@gmail.com");
        User user2 = new User("maria80", "qwerty123QWERTY", "Maria", "Blagoeva", "maria.blagoeva@gmail.com");

        Session session1 = new Session(user1.getUserName(), Constants.TTL);
        Session session2 = new Session(user2.getUserName(), Constants.TTL);

        server.addSession(session1);
        server.addSession(session2);

        server.removeSession(session1);

        String userName1 = server.getUserNameFromSessionID(session1.getID());
        String userName2 = server.getUserNameFromSessionID(session2.getID());

        assertEquals(null, userName1);
        assertEquals(user2.getUserName(), userName2);
    }

    @Test
    public void givenExistingSessionWhenGetSessionFromUserIsInvokedThenReturnSession() {

        User user1 = new User("ivan97", "pass123PASS", "Ivan", "Petkov", "ivan.petkov@gmail.com");
        User user2 = new User("maria80", "qwerty123QWERTY", "Maria", "Blagoeva", "maria.blagoeva@gmail.com");

        Session session1 = new Session(user1.getUserName(), Constants.TTL);
        Session session2 = new Session(user2.getUserName(), Constants.TTL);

        server.addSession(session1);
        server.addSession(session2);

        Session session = server.getSessionFromUserName(user2.getUserName());

        assertEquals(session2.getID(), session.getID());
    }

    @Test
    public void givenExistingSessionWhenGetSessionFromIDIsInvokedThenReturnSession() {

        User user1 = new User("ivan97", "pass123PASS", "Ivan", "Petkov", "ivan.petkov@gmail.com");
        User user2 = new User("maria80", "qwerty123QWERTY", "Maria", "Blagoeva", "maria.blagoeva@gmail.com");

        Session session1 = new Session(user1.getUserName(), Constants.TTL);
        Session session2 = new Session(user2.getUserName(), Constants.TTL);

        server.addSession(session1);
        server.addSession(session2);

        Session session = server.getSessionFromID(session1.getID());

        assertEquals(session1.getID(), session.getID());
    }
}
