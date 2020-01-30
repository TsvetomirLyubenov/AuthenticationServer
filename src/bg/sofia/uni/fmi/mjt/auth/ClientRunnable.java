package bg.sofia.uni.fmi.mjt.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientRunnable implements Runnable {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientRunnable(Socket socket, BufferedReader reader, PrintWriter writer) {

        this.socket = socket;
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public void run() {

        try {

            while (socket != null && !socket.isClosed()) {

                String message = reader.readLine();

                if (message != null) {

                    System.out.println(message);

                    if (message.equals("Closing socket.")) {

                        reader.close();
                        reader = null;

                        writer.close();
                        writer = null;

                        socket.close();
                        socket = null;

                        System.out.println("Client socket is closed.");
                        System.out.println("No more server messages will be received.");
                    }
                }
            }
        }

        catch (IOException ex) {

            System.out.println("Error during IO operations.");
            ExceptionLogger.logException(ex);
        }
    }

}
