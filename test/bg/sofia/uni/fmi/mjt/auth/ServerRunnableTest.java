package bg.sofia.uni.fmi.mjt.auth;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ServerRunnable.class)
public class ServerRunnableTest {

    private static ByteArrayOutputStream out;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    private ServerRunnable serverRunnable;
    private AuthenticationServer server;

    @BeforeClass
    public static void changeOutputStream(){

        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @AfterClass
    public static void restoreDefaultOutputStream(){

        System.setOut(System.out);
    }

    @Before
    public void setUp() {

        socket = mock(Socket.class);
        writer = mock(PrintWriter.class);
        reader = mock(BufferedReader.class);

        OutputStream outputStream = mock(OutputStream.class);
        InputStream inputStream = mock(InputStream.class);

        InputStreamReader inputStreamReader = mock(InputStreamReader.class);

        server = new AuthenticationServer(Constants.TEST_USERS_PATH_NAME);
        serverRunnable = new ServerRunnable(socket, server);

        try {

            when(socket.getOutputStream()).thenReturn(outputStream);
            when(socket.getInputStream()).thenReturn(inputStream);
            whenNew(PrintWriter.class).withParameterTypes(OutputStream.class, boolean.class).withArguments(outputStream, true).thenReturn(writer);
            whenNew(InputStreamReader.class).withParameterTypes(InputStream.class).withArguments(inputStream).thenReturn(inputStreamReader);
            whenNew(BufferedReader.class).withParameterTypes(Reader.class).withArguments(inputStreamReader).thenReturn(reader);
        }
        catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    @After
    public void free() {

        File file = new File(Constants.TEST_USERS_PATH_NAME);
        file.delete();
    }

    @Test
    public void givenSocketIsClosedWhenRunIsInvokedThenPrintMessage() {

        when(socket.isClosed()).thenReturn(true);

        serverRunnable.run();
        assertTrue(out.toString().startsWith("Socket is closed.\r\n"));
    }

    @Test
    public void givenReaderGetsInvalidCommandWhenRunIsInvokedThenPrintMessage() throws IOException {

        when(socket.isClosed()).thenReturn(false, false, true);
        when(reader.readLine()).thenReturn("some text here", "register --sessionID 5ab");
        serverRunnable.run();

        verify(writer, times(2)).println("Invalid command.");
    }

    @Test
    public void givenReaderGetsRegistrationDataForNonExistingUserWhenRunIsInvokedThenPrintMessageAndAddUser() throws IOException {

        String register = "register --username ivan97 --first-name Ivan --last-name Petrov --password abc123ABC --email ivan.petrov@gmail.com";

        when(socket.isClosed()).thenReturn(false, true);
        when(reader.readLine()).thenReturn(register);
        serverRunnable.run();

        verify(writer).println("Registration was successful.\n");

        assertNotEquals(null, server.getUser("ivan97"));
    }

    @Test
    public void givenReaderGetsRegistrationDataForExistingUserWhenRunIsInvokedThenPrintMessage() throws IOException {

        String register = "register --username ivan97 --first-name Ivan --last-name Petrov --password abc123ABC --email ivan.petrov@gmail.com";

        when(socket.isClosed()).thenReturn(false, false, true);
        when(reader.readLine()).thenReturn(register, register);
        serverRunnable.run();

        verify(writer).println("A user with that user name already exists.\n");

        assertNotEquals(null, server.getUser("ivan97"));
    }

   @Test
    public void givenReaderGetsIncorrectLoginDataWhenRunIsInvokedThenPrintMessageAndDoNotCreateSession() throws IOException {

        String login = "login --username ivan97 --password abc123ABC";

        when(socket.isClosed()).thenReturn(false, true);
        when(reader.readLine()).thenReturn(login);
        serverRunnable.run();

        verify(writer).println("Incorrect user name or password.\n");

        assertEquals(null, server.getSessionFromUserName("ivan97"));
    }

    @Test
    public void givenReaderGetsCorrectLoginDataWhenRunIsInvokedThenPrintMessageAndCreateSession() throws IOException {

        String register = "register --username ivan97 --first-name Ivan --last-name Petrov --password abc123ABC --email ivan.petrov@gmail.com";
        String login = "login --username ivan97 --password abc123ABC";

        when(socket.isClosed()).thenReturn(false, false, true);
        when(reader.readLine()).thenReturn(register, login);
        serverRunnable.run();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        verify(writer, times(2)).println(captor.capture());

        List<String> resultStrings = captor.getAllValues();

        assertTrue(resultStrings.get(1).startsWith("ivan97 successfully logged in.\n"));
        assertNotEquals(null, server.getSessionFromUserName("ivan97"));
    }

    @Test
    public void givenReaderGetsLoginDataOfAlreadyLoggedInUserWhenRunIsInvokedThenPrintMessageAndReplaceSession() throws IOException {

        String register = "register --username ivan97 --first-name Ivan --last-name Petrov --password abc123ABC --email ivan.petrov@gmail.com";
        String login = "login --username ivan97 --password abc123ABC";

        when(socket.isClosed()).thenReturn(false, false, false, true);
        when(reader.readLine()).thenReturn(register, login, login);
        serverRunnable.run();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        verify(writer, times(3)).println(captor.capture());

        List<String> resultStrings = captor.getAllValues();

        assertTrue(resultStrings.get(2).startsWith("ivan97 is already logged in.\n"));

        assertNotEquals(null, server.getSessionFromUserName("ivan97"));
    }

    @Test
    public void givenReaderGetsLoginDataWithSessionIDOfAlreadyLoggedInUserWhenRunIsInvokedThenReplaceSession() throws IOException {

        String register = "register --username ivan97 --first-name Ivan --last-name Petrov --password abc123ABC --email ivan.petrov@gmail.com";
        String loginData = "login --username ivan97 --password abc123ABC";
        String loginID = "login --session-id 1";

        when(socket.isClosed()).thenReturn(false, false, false, true);
        when(reader.readLine()).thenReturn(register, loginData, loginID);
        serverRunnable.run();

        assertNotEquals(null, server.getSessionFromUserName("ivan97"));
    }

    @Test
    public void givenReaderGetsDataToResetPasswordOfExistingUserWhenRunIsInvokedThenPrintMessageAndResetPassword() throws IOException, NoSuchAlgorithmException {

        String register = "register --username ivan97 --first-name Ivan --last-name Petrov --password abc123ABC --email ivan.petrov@gmail.com";
        String reset = "reset-password --username ivan97 --old-password abc123ABC --new-password ABC123abc";

        when(socket.isClosed()).thenReturn(false, false, true);
        when(reader.readLine()).thenReturn(register, reset);
        serverRunnable.run();

        verify(writer).println("The password has been successfully reset.\n");

        User user = server.getUser("ivan97");
        assertEquals(HashGenerator.sha1("ABC123abc"), user.getPassword());
    }

    @Test
    public void givenReaderGetsNoDataToUpdateExistingUserWhenRunIsInvokedThenPrintMessage() throws IOException {

        String register = "register --username ivan97 --first-name Ivan --last-name Petrov --password abc123ABC --email ivan.petrov@gmail.com";
        String loginData = "login --username ivan97 --password abc123ABC";

        when(socket.isClosed()).thenReturn(false, false, true);
        when(reader.readLine()).thenReturn(register, loginData);
        serverRunnable.run();

        StringBuilder updateNoChanges = new StringBuilder("update-user --session-id " + server.getSessionFromUserName("ivan97").getID());

        when(socket.isClosed()).thenReturn(false, true);
        when(reader.readLine()).thenReturn(updateNoChanges.toString());
        serverRunnable.run();

        verify(writer).println("Nothing to change.\n");
    }

    @Test
    public void givenReaderGetsDataToUpdateExistingUserWhenRunIsInvokedThenPrintMessageAndReplaceSession() throws IOException {

        String register = "register --username ivan97 --first-name Ivan --last-name Petrov --password abc123ABC --email ivan.petrov@gmail.com";
        String loginData = "login --username ivan97 --password abc123ABC";

        when(socket.isClosed()).thenReturn(false, false, true);
        when(reader.readLine()).thenReturn(register, loginData);
        serverRunnable.run();

        StringBuilder updateChanges = new StringBuilder();
        updateChanges.append("update-user --session-id ");
        updateChanges.append(server.getSessionFromUserName("ivan97").getID());
        updateChanges.append(" --new-username maria80 --new-first-name Maria --new-last-name Blagoeva");

        when(socket.isClosed()).thenReturn(false, true);
        when(reader.readLine()).thenReturn(updateChanges.toString());
        serverRunnable.run();

        verify(writer).println("The user information has been successfully updated.\n");

        assertEquals("maria80", server.getUserNameFromSessionID(server.getSessionFromUserName("maria80").getID()));
    }

    @Test
    public void givenReaderGetsDataToDeleteExistingUserWhenRunIsInvokedThenPrintMessageAndRemoveSession() throws IOException {

        String register = "register --username ivan97 --first-name Ivan --last-name Petrov --password abc123ABC --email ivan.petrov@gmail.com";
        String deleteExisting = "delete-user --username ivan97";

        when(socket.isClosed()).thenReturn(false, false, true);
        when(reader.readLine()).thenReturn(register, deleteExisting);
        serverRunnable.run();

        verify(writer).println("The user account has successfully been deleted.\n");

        assertEquals(null, server.getSessionFromUserName("ivan97"));
    }

    @Test
    public void givenReaderGetsDataToDeleteNonExistingUserWhenRunIsInvokedThenPrintMessage() throws IOException {

        String register = "register --username ivan97 --first-name Ivan --last-name Petrov --password abc123ABC --email ivan.petrov@gmail.com";
        String deleteNonExisting = "delete-user --username ivan";

        when(socket.isClosed()).thenReturn(false, false, true);
        when(reader.readLine()).thenReturn(register, deleteNonExisting);
        serverRunnable.run();

        verify(writer).println("The user name is incorrect.\n");

        assertEquals(null, server.getSessionFromUserName("ivan97"));
    }

    @Test
    public void givenReaderGetsDataToLogoutNonLoggedInUserWhenRunIsInvokedThenPrintMessage() throws IOException {

        String register = "register --username ivan97 --first-name Ivan --last-name Petrov --password abc123ABC --email ivan.petrov@gmail.com";
        String logout = "logout --session-id 1";

        when(socket.isClosed()).thenReturn(false, false, true);
        when(reader.readLine()).thenReturn(register, logout);
        serverRunnable.run();

        verify(writer).println("The specified user is not logged in.\n");

        assertEquals(null, server.getSessionFromUserName("ivan97"));
    }

    @Test
    public void givenReaderGetsDataToLogoutLoggedInUserWhenRunIsInvokedThenPrintMessageAndRemoveSession() throws IOException {

        String register = "register --username ivan97 --first-name Ivan --last-name Petrov --password abc123ABC --email ivan.petrov@gmail.com";
        String login = "login --username ivan97 --password abc123ABC";

        when(socket.isClosed()).thenReturn(false, false, true);
        when(reader.readLine()).thenReturn(register, login);
        serverRunnable.run();

        StringBuilder logout = new StringBuilder("logout --session-id " + server.getSessionFromUserName("ivan97").getID());

        when(socket.isClosed()).thenReturn(false, true);
        when(reader.readLine()).thenReturn(logout.toString());
        serverRunnable.run();

        verify(writer).println("The user was successfully logged out of the system.\n");

        assertEquals(null, server.getSessionFromUserName("ivan97"));
    }

    @Test
    public void givenReaderGetsDataToDisconnectWhenRunIsInvokedThenPrintMessage() throws IOException {

        String disconnect = "disconnect";

        when(socket.isClosed()).thenReturn(false, true);
        when(reader.readLine()).thenReturn(disconnect);
        serverRunnable.run();

        verify(writer).println("Closing socket.\n");

    }

}
