package bg.sofia.uni.fmi.mjt.auth;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;

public class User implements Serializable {

    private static final long serialVersionUID = 1234L;

    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String email;


    public User(String userName, String password, String firstName, String lastName, String email) {

        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;

        setPassword(password);
    }

    public String getUserName() {

        return userName;
    }

    public String getPassword() {

        return password;
    }

    public String getEmail() {

        return email;
    }

    public String getFirstName() {

        return firstName;
    }

    public String getLastName() {

        return lastName;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public void setFirstName(String firstName) {

        this.firstName = firstName;
    }

    public void setLastName(String lastName) {

        this.lastName = lastName;
    }

    public void setPassword(String password) {

        try {

            this.password = HashGenerator.sha1(password);
        }

        catch(NoSuchAlgorithmException ex) {

            this.password = null;
            System.out.println("Could not set the password.");
            ExceptionLogger.logException(ex);
        }
    }

}
