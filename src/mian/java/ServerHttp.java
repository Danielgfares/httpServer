package mian.java;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

public class ServerHttp {

    private final int id;
    private final BufferedReader inputReader;
    private final PrintStream outputWriter;
    // 0 -> HTTP 1.0 response - close connection
    // 1 -> HTTP 1.1 response - keep connection
    int HTTP_VERSION = 1; // 1.1

    public ServerHttp(InputStream inputStream, OutputStream outputStream, int _id) {
        this.id = _id;
        this.inputReader = new BufferedReader(new InputStreamReader(inputStream));
        this.outputWriter = new PrintStream(outputStream);
    }

    private static int get_random(int range) {
        Random r = new Random();
        return r.nextInt(range) + 1;
    }

    private static String get_hash_string(byte[] bytes) {
        if (bytes != null) {
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes)
                sb.append(String.format("%02x", b));
            return sb.toString();
        }
        return null;
    }

    private static byte[] get_hash(String message) {
        if (message == null) {
            return null;
        }
        MessageDigest digest = null;
        try {
            // SHA-256 return 256 bits -> 32 bytes
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            //e.printStackTrace();
        }
        return digest != null ? digest.digest(message.getBytes(StandardCharsets.UTF_8)) : null;
    }


    /**
     * Read http GET request
     *
     * @throws IOException in case of bad http requests
     */
    public void readMessage() throws IOException {
        String header;
        String line = inputReader.readLine();
        if (line != null && !line.isEmpty()) {
            header = line;
            System.out.println("Client [ " + this.id + " ] " + "requests: " + header);
            // request body - ignored
            while (line != null && !line.isEmpty()) {
                //System.out.println(this.id + " - " + line);
                line = inputReader.readLine();
            }
            if (header.startsWith("GET ") && (header.endsWith(" HTTP/1.0") || header.endsWith(" HTTP/1.1"))) {
                if (header.endsWith(" HTTP/1.0")) {
                    HTTP_VERSION = 0;
                } else {
                    HTTP_VERSION = 1;
                }
            } else {
                throw new IOException("This server handles http GET requests only");
            }
        }
    }

    /**
     * Write response
     */
    public void writeResponse() {
        // takes a random number between 1 - 256 to create a random hash
        int random = get_random(256);
        // then makes a hash from it (the hash created has length of 64 characters)
        String hash = get_hash_string(get_hash(String.valueOf(random)));
        // takes the first 32 characters from the formed hash to return 32 random hexadecimal characters.
        String response = hash.substring(0, 32);
        // write http response
        outputWriter.print("HTTP/1." + this.HTTP_VERSION + " 200 OK" + "\r\n" +
                "Content-Type: text/plain" + "\r\n" +
                "Date: " + new Date() + "\r\n" +
                "Content-length: " + response.length() + "\r\n" + "\r\n" +
                response + "\r\n"
        );
        if (this.HTTP_VERSION == 0) {
            outputWriter.close();
        }
    }
}
