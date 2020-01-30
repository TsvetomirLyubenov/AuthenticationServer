package bg.sofia.uni.fmi.mjt.auth;

import java.util.HashMap;

public class Validator {

    public static boolean isEmailValid(String email) {

        return email.matches("[\\S]+[@][\\S]+[.][\\S]+");
    }

    public static boolean isNameValid(String name) {

        return name.matches("^[a-zA-Z]+$");
    }

    public static boolean isPasswordValid(String password) {

        return (password.matches("[\\S]*[a-z][\\S]*") && password.matches("[\\S]*[A-Z][\\S]*") && password.matches("[\\S]*[0-9][\\S]*"));
    }

    public static boolean isUserNameValid(String userName) {

        return userName.matches("[\\S]*[a-zA-Z][\\S]*");
    }

    public static boolean isRegisterValid(String command, String[] tokens, HashMap<String, String> parameters) {

        boolean checkUserName = parameters.get(Constants.USER_NAME) != null && isUserNameValid(parameters.get(Constants.USER_NAME));
        boolean checkPassword = parameters.get(Constants.PASSWORD) != null && isPasswordValid(parameters.get(Constants.PASSWORD));
        boolean checkFirstName = parameters.get(Constants.FIRST_NAME) != null && isNameValid(parameters.get(Constants.FIRST_NAME));
        boolean checkLastName = parameters.get(Constants.LAST_NAME) != null && isNameValid(parameters.get(Constants.LAST_NAME));
        boolean checkEmail = parameters.get(Constants.EMAIL) != null && isEmailValid(parameters.get(Constants.EMAIL));

        return command.equals(Constants.REGISTER_COMMAND) && checkUserName && checkPassword && checkFirstName
               && checkLastName  && checkEmail && parameters.size() == Constants.REGISTER_LENGTH && tokens.length % 2 != 0;
    }

    public static boolean isLoginDataValid(String command, String[] tokens, HashMap<String, String> parameters) {

        boolean checkUserName = parameters.get(Constants.USER_NAME) != null && isUserNameValid(parameters.get(Constants.USER_NAME));
        boolean checkPassword = parameters.get(Constants.PASSWORD) != null && isPasswordValid(parameters.get(Constants.PASSWORD));

        return command.equals(Constants.LOGIN_COMMAND) && checkUserName && checkPassword
               && parameters.size() == Constants.LOGIN_DATA_LENGTH && tokens.length % 2 != 0;
    }

    public static boolean isLoginIDValid(String command, String[] tokens, HashMap<String, String> parameters) {

        boolean checkSessionID = parameters.get(Constants.SESSION_ID) != null;

        return command.equals(Constants.LOGIN_COMMAND) && checkSessionID
               && parameters.size() == Constants.LOGIN_ID_LENGTH && tokens.length % 2 != 0;
    }

    public static boolean isResetValid(String command, String[] tokens, HashMap<String, String> parameters) {

        boolean checkUserName = parameters.get(Constants.USER_NAME) != null && isUserNameValid(parameters.get(Constants.USER_NAME));
        boolean checkOldPassword = parameters.get(Constants.OLD_PASSWORD) != null && isPasswordValid(parameters.get(Constants.OLD_PASSWORD));
        boolean checkNewPassword = parameters.get(Constants.NEW_PASSWORD) != null && isPasswordValid(parameters.get(Constants.NEW_PASSWORD));

        return command.equals(Constants.RESET_COMMAND) && checkUserName && checkOldPassword && checkNewPassword
               && parameters.size() == Constants.RESET_LENGTH && tokens.length % 2 != 0;
    }

    public static boolean isUpdateValid(String command, String[] tokens, HashMap<String, String> parameters) {

        boolean checkSessionID = parameters.get(Constants.SESSION_ID) != null;
        boolean checkUserName = parameters.get(Constants.NEW_USER_NAME) == null || isUserNameValid(parameters.get(Constants.NEW_USER_NAME));
        boolean checkFirstName = parameters.get(Constants.NEW_FIRST_NAME) == null || isNameValid(parameters.get(Constants.NEW_FIRST_NAME));
        boolean checkLastName = parameters.get(Constants.NEW_LAST_NAME) == null || isNameValid(parameters.get(Constants.NEW_LAST_NAME));
        boolean checkEmail = parameters.get(Constants.NEW_EMAIL) == null || isEmailValid(parameters.get(Constants.NEW_EMAIL));

        int numberOfRealParameters = 1;

        numberOfRealParameters = parameters.get(Constants.NEW_USER_NAME) != null && checkUserName ? numberOfRealParameters + 1 : numberOfRealParameters;
        numberOfRealParameters = parameters.get(Constants.NEW_FIRST_NAME) != null && checkFirstName ? numberOfRealParameters + 1 : numberOfRealParameters;
        numberOfRealParameters = parameters.get(Constants.NEW_LAST_NAME) != null && checkLastName ? numberOfRealParameters + 1 : numberOfRealParameters;
        numberOfRealParameters = parameters.get(Constants.NEW_EMAIL) != null && checkEmail ? numberOfRealParameters + 1 : numberOfRealParameters;

        return command.equals(Constants.UPDATE_COMMAND) && checkSessionID && checkUserName && checkFirstName
               && checkLastName && checkEmail && numberOfRealParameters == parameters.size() && tokens.length % 2 != 0;
    }

    public static boolean isLogoutValid(String command, String[] tokens, HashMap<String, String> parameters) {

        boolean checkSessionID = parameters.get(Constants.SESSION_ID) != null;

        return command.equals(Constants.LOGOUT_COMMAND) && checkSessionID
               && parameters.size() == Constants.LOGOUT_LENGTH && tokens.length % 2 != 0;
    }

    public static boolean isDeleteValid(String command, String[] tokens, HashMap<String, String> parameters) {

        boolean checkUserName = parameters.get(Constants.USER_NAME) != null && isUserNameValid(parameters.get(Constants.USER_NAME));

        return command.equals(Constants.DELETE_COMMAND) && checkUserName
               && parameters.size() == Constants.DELETE_LENGTH && tokens.length % 2 != 0;
    }

    public static boolean isDisconnectValid(String command, String[] tokens, HashMap<String, String> parameters) {

        return command.equals(Constants.DISCONNECT_COMMAND) && parameters.isEmpty() && tokens.length % 2 != 0;
    }

    public static HashMap<String, String> getParameters(String[] tokens) {

        HashMap<String, String> parameters = new HashMap<>();

        for (int i = 1; i < tokens.length - 1; i += 2) {

            parameters.put(tokens[i], tokens[i+1]);
        }

        return parameters;
    }
}
