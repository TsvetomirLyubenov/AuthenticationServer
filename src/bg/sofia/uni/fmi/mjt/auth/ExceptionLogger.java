package bg.sofia.uni.fmi.mjt.auth;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class ExceptionLogger {

    public static synchronized void logException(Exception exception) {

        try ( FileOutputStream outputFile = new FileOutputStream(Constants.EXCEPTIONS_PATH_NAME);
              PrintWriter writer = new PrintWriter(outputFile)){

            writer.println(exception);
            exception.printStackTrace(writer);

            writer.close();
            outputFile.close();
        }
        catch (IOException ex) {

            System.out.println("Cannot log exceptions to file.");
            ex.printStackTrace();
        }
    }
}
