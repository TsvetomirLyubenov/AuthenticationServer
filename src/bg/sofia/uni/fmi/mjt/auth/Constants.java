package bg.sofia.uni.fmi.mjt.auth;

public class Constants {

    public static final String HOST = "localhost";
    public static final int PORT = 8080;

    public static final String USERS_PATH_NAME = "resources/users/users.dat";
    public static final String TEST_USERS_PATH_NAME = "resources/testUsers.dat";

    public static final String EXCEPTIONS_PATH_NAME = "resources/logs/exceptions.txt";

    public static final int TTL = 300;

    public static final String REGISTER_COMMAND = "register";
    public static final String LOGIN_COMMAND = "login";
    public static final String RESET_COMMAND = "reset-password";
    public static final String UPDATE_COMMAND = "update-user";
    public static final String LOGOUT_COMMAND = "logout";
    public static final String DELETE_COMMAND = "delete-user";
    public static final String DISCONNECT_COMMAND = "disconnect";

    public static final int REGISTER_LENGTH = 5;
    public static final int LOGIN_DATA_LENGTH = 2;
    public static final int LOGIN_ID_LENGTH = 1;
    public static final int RESET_LENGTH = 3;
    public static final int LOGOUT_LENGTH = 1;
    public static final int DELETE_LENGTH = 1;

    public static final String USER_NAME = "--username";
    public static final String NEW_USER_NAME = "--new-username";
    public static final String PASSWORD = "--password";
    public static final String OLD_PASSWORD = "--old-password";
    public static final String NEW_PASSWORD = "--new-password";
    public static final String FIRST_NAME = "--first-name";
    public static final String NEW_FIRST_NAME = "--new-first-name";
    public static final String LAST_NAME = "--last-name";
    public static final String NEW_LAST_NAME = "--new-last-name";
    public static final String EMAIL = "--email";
    public static final String NEW_EMAIL = "--new-email";
    public static final String SESSION_ID = "--session-id";

}
