package bg.sofia.uni.fmi.mjt.auth;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class ValidatorTest {

    @Test
    public void givenValidEmailWhenIsEmailValidIsInvokedThenReturnTrue() {

        assertEquals(true, Validator.isEmailValid("ivan.petkov@gmail.com"));
    }

    @Test
    public void givenInvalidEmailWhenIsEmailValidIsInvokedThenReturnFalse() {

        assertEquals(false, Validator.isEmailValid("ivan.petkov.gmail.com"));
        assertEquals(false, Validator.isEmailValid("ivan.petkov@gmail"));
        assertEquals(false, Validator.isEmailValid("ivan.petkov@gmail@com"));
        assertEquals(false, Validator.isEmailValid("ivan petkov@gmail.com"));
    }

    @Test
    public void givenValidUserNameWhenIsUserNameValidIsInvokedThenReturnTrue() {

        assertEquals(true, Validator.isUserNameValid("ivan.petkov"));
        assertEquals(true, Validator.isUserNameValid("ivan"));
        assertEquals(true, Validator.isUserNameValid("ivan97"));
        assertEquals(true, Validator.isUserNameValid("iV?an"));
    }

    @Test
    public void givenInvalidUserNameWhenIsUserNameValidIsInvokedThenReturnFalse() {

        assertEquals(false, Validator.isUserNameValid("696889"));
        assertEquals(false, Validator.isUserNameValid("!?.;"));
        assertEquals(false, Validator.isUserNameValid("ivan petkov"));
    }

    @Test
    public void givenValidNameWhenIsNameValidIsInvokedThenReturnTrue() {

        assertEquals(true, Validator.isNameValid("ivan"));
        assertEquals(true, Validator.isNameValid("Ivan"));
    }

    @Test
    public void givenInvalidNameWhenIsNameValidIsInvokedThenReturnFalse() {

        assertEquals(false, Validator.isNameValid("iv4234an"));
        assertEquals(false, Validator.isNameValid("iv an"));
    }

    @Test
    public void givenValidPasswordWhenIsPasswordValidIsInvokedThenReturnTrue() {

        assertEquals(true, Validator.isPasswordValid("pass9567PASS"));
    }

    @Test
    public void givenInvalidPasswordWhenIsPasswordValidIsInvokedThenReturnFalse() {

        assertEquals(false, Validator.isPasswordValid("pass"));
        assertEquals(false, Validator.isPasswordValid("PASS"));
        assertEquals(false, Validator.isPasswordValid("324234"));
        assertEquals(false, Validator.isPasswordValid("pass 9567 PASS"));
    }

    @Test
    public void givenValidParametersWhenIsRegisterValidIsInvokedThenReturnTrue() {

        String input = "register --username ivan97 --first-name Ivan --last-name Petrov --password abc123ABC --email ivan.petrov@gmail.com";
        String[] tokens = input.split(" ");
        String command = tokens[0];
        HashMap<String, String> parameters = Validator.getParameters(tokens);

        assertEquals(true, Validator.isRegisterValid(command, tokens, parameters));
    }

    @Test
    public void givenValidParametersWhenIsLoginDataValidIsInvokedThenReturnTrue() {

        String input = "login --username ivan97 --password abc123ABC";
        String[] tokens = input.split(" ");
        String command = tokens[0];
        HashMap<String, String> parameters = Validator.getParameters(tokens);

        assertEquals(true, Validator.isLoginDataValid(command, tokens, parameters));
    }

    @Test
    public void givenValidParametersWhenIsLoginIDValidIsInvokedThenReturnTrue() {

        String input = "login --session-id 1";
        String[] tokens = input.split(" ");
        String command = tokens[0];
        HashMap<String, String> parameters = Validator.getParameters(tokens);

        assertEquals(true, Validator.isLoginIDValid(command, tokens, parameters));
    }

    @Test
    public void givenValidParametersWhenIsResetValidIsInvokedThenReturnTrue() {

        String input = "reset-password --username ivan97 --old-password abc123ABC --new-password ABC123abc";
        String[] tokens = input.split(" ");
        String command = tokens[0];
        HashMap<String, String> parameters = Validator.getParameters(tokens);

        assertEquals(true, Validator.isResetValid(command, tokens, parameters));
    }

    @Test
    public void givenValidParametersWhenIsUpdateValidIsInvokedThenReturnTrue() {

        String input = "update-user --session-id 1 --new-username maria80 --new-first-name Maria --new-last-name Blagoeva";
        String[] tokens = input.split(" ");
        String command = tokens[0];
        HashMap<String, String> parameters = Validator.getParameters(tokens);

        assertEquals(true, Validator.isUpdateValid(command, tokens, parameters));
    }

    @Test
    public void givenValidParametersWhenIsLogoutValidIsInvokedThenReturnTrue() {

        String input = "logout --session-id 1";
        String[] tokens = input.split(" ");
        String command = tokens[0];
        HashMap<String, String> parameters = Validator.getParameters(tokens);

        assertEquals(true, Validator.isLogoutValid(command, tokens, parameters));
    }

    @Test
    public void givenValidParametersWhenIsDeleteValidIsInvokedThenReturnTrue() {

        String input = "delete-user --username ivan97";
        String[] tokens = input.split(" ");
        String command = tokens[0];
        HashMap<String, String> parameters = Validator.getParameters(tokens);

        assertEquals(true, Validator.isDeleteValid(command, tokens, parameters));
    }


    @Test
    public void givenArrayOfTokensWhenGetParametersIsInvokedThenReturnHashMapOfParameters() {

        HashMap<String, String> parameters;

        String[] tokens = {"register", "--username", "ivan97", "--password", "pass123PASS"};

        parameters = Validator.getParameters(tokens);

        assertEquals("ivan97", parameters.get("--username"));
        assertEquals("pass123PASS", parameters.get("--password"));
    }

}
