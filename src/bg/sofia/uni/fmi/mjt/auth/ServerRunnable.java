package bg.sofia.uni.fmi.mjt.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public class ServerRunnable implements Runnable {

    private Socket socket;
    private AuthenticationServer server;

    public ServerRunnable(Socket socket, AuthenticationServer server) {

        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {

        try  (PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
              BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            while (socket != null && !socket.isClosed()) {

                String input = reader.readLine();

                if (input != null) {

                    String[] tokens = input.split(" ");

                    String command = tokens[0];

                    HashMap<String, String> parameters = Validator.getParameters(tokens);

                    if (Validator.isRegisterValid(command, tokens, parameters)) {

                        String userName = parameters.get(Constants.USER_NAME);
                        String password = parameters.get(Constants.PASSWORD);
                        String firstName = parameters.get(Constants.FIRST_NAME);
                        String lastName = parameters.get(Constants.LAST_NAME);
                        String email = parameters.get(Constants.EMAIL);

                        writer.println(register(userName, password, firstName, lastName, email));
                    }

                    else if (Validator.isLoginDataValid(command, tokens, parameters)) {

                        String userName = parameters.get(Constants.USER_NAME);
                        String password = parameters.get(Constants.PASSWORD);

                        writer.println(login(userName, password));
                    }

                    else if (Validator.isLoginIDValid(command, tokens, parameters)) {

                        String sessionID = parameters.get(Constants.SESSION_ID);

                        writer.println(login(sessionID));
                    }

                    else if (Validator.isResetValid(command, tokens, parameters)) {

                        String userName = parameters.get(Constants.USER_NAME);
                        String oldPassword = parameters.get(Constants.OLD_PASSWORD);
                        String newPassword = parameters.get(Constants.NEW_PASSWORD);

                        writer.println(resetPassword(userName, oldPassword, newPassword));
                    }

                    else if (Validator.isUpdateValid(command, tokens, parameters)) {

                        String newUserName = parameters.get(Constants.NEW_USER_NAME);
                        String sessionID = parameters.get(Constants.SESSION_ID);
                        String newFirstName = parameters.get(Constants.NEW_FIRST_NAME);
                        String newLastName = parameters.get(Constants.NEW_LAST_NAME);
                        String newEmail = parameters.get(Constants.NEW_EMAIL);

                        writer.println(updateUser(sessionID, newUserName, newFirstName, newLastName, newEmail));
                    }

                    else if (Validator.isDeleteValid(command, tokens, parameters)) {

                        String userName = parameters.get(Constants.USER_NAME);

                        writer.println(deleteUser(userName));
                    }

                    else if (Validator.isLogoutValid(command, tokens, parameters)) {

                        String sessionID = parameters.get(Constants.SESSION_ID);

                        writer.println(logout(sessionID));
                    }

                    else if (Validator.isDisconnectValid(command, tokens, parameters)) {

                        writer.println(disconnect());
                    }

                    else {

                        writer.println("Invalid command.");
                    }
                }
            }

            System.out.println("Socket is closed.");
        }

        catch (IOException ex) {

            System.out.println("Error during IO operations.");
            ExceptionLogger.logException(ex);
        }
    }

    private String register(String userName, String password, String firstName, String lastName, String email) {

        String message = "";

        if (server.getUser(userName) == null) {

            User user = new User(userName, password, firstName, lastName, email);
            server.addUser(user);

            message += "Registration was successful.\n";
        }

        else {

            message += "A user with that user name already exists.\n";
        }

        return message;
    }

    private String login(String userName, String password) {

        String message = "";

        User user = server.getUser(userName);

        if (user != null && server.isPasswordCorrect(user, password)) {

            if (server.getSessionFromUserName(userName) != null) {

                message += userName + " is already logged in.\n";

                logout(server.getSessionFromUserName(userName).getID());
            }

            Session session = new Session(userName, Constants.TTL);

            server.addSession(session);
            message += userName + " successfully logged in.\n";

            message += "Session ID: " + session.getID() + "\n";
            message += "TTL: " + session.getTimeToLive() + "\n";
        }

        else {

            message += "Incorrect user name or password.\n";
        }

        return message;
    }

    private String login(String sessionID) {

        String message = "";

        String userName = server.getUserNameFromSessionID(sessionID);

        if (userName != null) {

            message += userName + " is already logged in.\n";

            logout(sessionID);

            Session session = new Session(userName, Constants.TTL);

            server.addSession(session);
            message += userName + " successfully logged in.\n";

            message += "Session ID: " + session.getID() + "\n";
            message += "TTL: " + session.getTimeToLive() + "\n";
        }

        else {

            message += "Session ID is incorrect.\n";
        }

        return message;
    }

    private String resetPassword(String userName, String oldPassword, String newPassword) {

        String message = "";

        User user = server.getUser(userName);

        if (user != null && server.isPasswordCorrect(user, oldPassword)) {

            user.setPassword(newPassword);
            server.addUser(user);

            message += "The password has been successfully reset.\n";
        }

        else {

            message += "Incorrect user name or password.\n";
        }

        return message;
    }

    private String updateUser(String sessionID, String newUserName, String newFirstName, String newLastName, String newEmail) {

        String message = "";

        String userName = server.getUserNameFromSessionID(sessionID);

        if (userName == null) {

            message +="There is no user corresponding to that session ID.\n";
            return message;
        }

        boolean isUserUpdated = false;

        User user = server.getUser(userName);

        if (newUserName != null) {

            user.setUserName(newUserName);

            Session oldSession = server.getSessionFromUserName(userName);
            server.removeSession(oldSession);

            Session newSession = new Session(newUserName, Constants.TTL);
            server.addSession(newSession);

            isUserUpdated = true;
        }

        if (newFirstName != null) {

            user.setFirstName(newFirstName);
            isUserUpdated = true;
        }

        if (newLastName != null) {

            user.setLastName(newLastName);
            isUserUpdated = true;
        }

        if (newEmail != null) {

            user.setEmail(newEmail);
            isUserUpdated = true;
        }

        if (isUserUpdated) {

            server.removeUser(userName);
            server.addUser(user);
            message += "The user information has been successfully updated.\n";
        }

        else {

            message += "Nothing to change.\n";
        }

        return message;
    }

    private String deleteUser(String userName) {

        String message = "";

        User user = server.getUser(userName);

        if (user != null) {

            Session session = server.getSessionFromUserName(userName);

            if (session != null) {

                logout(server.getSessionFromUserName(userName).getID());
            }

            server.removeUser(userName);

            message += "The user account has successfully been deleted.\n";
        }

        else {

            message += "The user name is incorrect.\n";
        }

        return message;
    }


    private String logout(String sessionID) {

        String message = "";

        Session session = server.getSessionFromID(sessionID);

        if (session != null) {

            server.removeSession(session);
            message += "The user was successfully logged out of the system.\n";
        }

        else {

            message += "The specified user is not logged in.\n";
        }

        return message;
    }

    private String disconnect() {

        String message = "";

        try {

            message += "Closing socket.\n";

            socket.close();
        }

        catch (IOException ex) {

            System.out.println("Error during IO operations.");
            ExceptionLogger.logException(ex);
        }

        return message;
    }







}