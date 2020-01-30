package bg.sofia.uni.fmi.mjt.auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;


public class AuthenticationServer {

    private final HashMap<String, Session> nameSessions;
    private final HashMap<String, Session> idSessions;
    private final HashMap<String, User> users;
    private final File usersDatabase;

    public static void main(String[] args) {

        new AuthenticationServer(Constants.USERS_PATH_NAME).run();
    }

    public AuthenticationServer(String pathName) {

        nameSessions = new HashMap<>();
        idSessions = new HashMap<>();
        usersDatabase = new File(pathName);
        users = getUsersDatabase();
    }

    private void run() {

        try (ServerSocket serverSocket = new ServerSocket(Constants.PORT)) {

            System.out.println("Server is running.");

            while (true) {

                Socket socket = serverSocket.accept();
                System.out.println("A client connected to server " + socket.getInetAddress());

                ServerRunnable runnable = new ServerRunnable(socket, this);
                new Thread(runnable).start();
            }
        }

        catch (IOException ex) {

            System.out.println("Another server may be running on the same port.");
            ExceptionLogger.logException(ex);
        }
    }


    public synchronized void addUser(User user) {

        users.put(user.getUserName(), user);
        setUsersDatabase(users);
    }

    public synchronized User getUser(String userName) {

        return users.get(userName);
    }

    public synchronized void removeUser(String userName) {

        users.remove(userName);
        setUsersDatabase(users);
    }

    public synchronized boolean isPasswordCorrect(User user, String password) {

        String sha1Password = "";

        try {

            sha1Password = HashGenerator.sha1(password);
        }

        catch (NoSuchAlgorithmException ex) {

            System.out.println("Algorithm not available.");
            ExceptionLogger.logException(ex);
        }

        return user.getPassword().equals(sha1Password);
    }

    public synchronized HashMap<String, User> getUsersDatabase() {

        HashMap<String, User> users = new HashMap<>();

        if (usersDatabase.exists() && usersDatabase.length() > 0) {

            try  ( FileInputStream inputFile = new FileInputStream(usersDatabase);
                   ObjectInputStream objectInput = new ObjectInputStream(inputFile)){

                User user;
                int numberOfUsers;

                numberOfUsers = objectInput.readInt();

                for (int i = 0; i < numberOfUsers; i++) {

                     user = (User) objectInput.readObject();

                     users.put(user.getUserName(), user);
                }
            }

            catch (IOException ex) {

                System.out.println("Error during IO operations.");
                ExceptionLogger.logException(ex);
            }

            catch (ClassNotFoundException ex) {

                System.out.println("The class of the serialized object cannot be found.");
                ExceptionLogger.logException(ex);
            }
        }

        return users;
    }

    public synchronized void setUsersDatabase(HashMap<String, User> users) {

        try ( FileOutputStream outputFile = new FileOutputStream(usersDatabase);
              ObjectOutputStream objectOutput = new ObjectOutputStream(outputFile)){

            objectOutput.writeInt(users.size());

            for (User user: users.values()) {

                objectOutput.writeObject(user);
            }
        }

        catch (IOException ex) {

            System.out.println("Error during IO operations.");
            ExceptionLogger.logException(ex);
        }

    }

    public synchronized void addSession(Session session) {

        nameSessions.put(session.getUserName(), session);
        idSessions.put(session.getID(), session);
    }


    public synchronized void removeSession(Session session) {

        if (session != null) {

            nameSessions.remove(session.getUserName());
            idSessions.remove(session.getID());
        }
    }

    public synchronized String getUserNameFromSessionID(String sessionID) {

        Session session = idSessions.get(sessionID);

        if (session != null && session.hasSessionExpired()) {

            removeSession(session);
            return null;
        }

        if (session != null) {

            return session.getUserName();
        }

        return null;
    }

    public synchronized Session getSessionFromUserName(String userName) {

        Session session = nameSessions.get(userName);

        if (session != null && session.hasSessionExpired()) {

            removeSession(session);
            return null;
        }

        return session;
    }

    public synchronized Session getSessionFromID(String sessionID) {

        Session session = idSessions.get(sessionID);

        if (session != null && session.hasSessionExpired()) {

            removeSession(session);
            return null;
        }

        return session;
    }
}
