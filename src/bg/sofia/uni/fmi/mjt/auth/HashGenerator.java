package bg.sofia.uni.fmi.mjt.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashGenerator {

    public static String sha1(String input) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA1");

        byte[] result = digest.digest(input.getBytes());

        StringBuilder builder = new StringBuilder();

        for (byte current: result) {

            builder.append(Integer.toString((current & 0xff) + 0x100, 16).substring(1));
        }

        return builder.toString();
    }
}
