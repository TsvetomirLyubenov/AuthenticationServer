package bg.sofia.uni.fmi.mjt.auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class AuthenticationClient {

    public static void main(String[] args) {

        new AuthenticationClient().run();
    }

    private void run() {

        try (Socket socket = new Socket(Constants.HOST, Constants.PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Connection to server established.");

            ClientRunnable clientRunnable = new ClientRunnable(socket, reader, writer);
            new Thread(clientRunnable).start();

            Scanner scanner = new Scanner(System.in);

            String input;

            do {

                input = scanner.nextLine();
                writer.println(input);

            } while (!input.equals(Constants.DISCONNECT_COMMAND));

        }

        catch (IOException ex) {

            System.out.println("Connection to server failed.");
            ExceptionLogger.logException(ex);
        }

    }
}
