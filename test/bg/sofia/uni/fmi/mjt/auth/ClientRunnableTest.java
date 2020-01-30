package bg.sofia.uni.fmi.mjt.auth;

import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClientRunnableTest {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    private ClientRunnable client;

    private ByteArrayOutputStream out;

    @Before
    public void setUp() {

        socket = mock(Socket.class);
        reader = mock(BufferedReader.class);
        writer = mock(PrintWriter.class);
        client = new ClientRunnable(socket, reader, writer);

        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

    }

    @Test
    public void givenReaderGetsMessageWhenRunIsInvokedThenPrintMessage() throws IOException {

        when(socket.isClosed()).thenReturn(false, true);
        when(reader.readLine()).thenReturn("Test message.");
        client.run();

        assertEquals("Test message.\r\n", out.toString());
    }

    @Test
    public void givenReaderGetsMessageThatSocketIsClosedWhenRunIsInvokedThenPrintMessage() throws IOException {

        when(socket.isClosed()).thenReturn(false);
        when(reader.readLine()).thenReturn("Closing socket.");
        client.run();

        assertEquals("Closing socket.\r\n" +
                              "Client socket is closed.\r\n" +
                              "No more server messages will be received.\r\n", out.toString());
    }

    @Test
    public void givenReaderThrowsExceptionWhenRunIsInvokedThenCatchExceptionAndPrintMessage() throws IOException {

        when(socket.isClosed()).thenReturn(false);
        when(reader.readLine()).thenThrow(IOException.class);
        client.run();

        assertEquals("Error during IO operations.\r\n", out.toString());
    }
}
