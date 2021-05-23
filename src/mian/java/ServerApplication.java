package mian.java;


import java.io.IOException;
import java.net.Socket;

public class ServerApplication extends Thread {

    private final Socket clientSocket;
    private final int id;
    private ServerHttp serverHttp;

    /**
     * @param _clientSocket client socket s2 where the server will speak with the server
     * @param _id           client id in the server perspective
     */
    public ServerApplication(Socket _clientSocket, int _id) {
        this.id = _id;
        this.clientSocket = _clientSocket;
        try {
            getClientSocket().setSoTimeout(60 * 1000);
            this.serverHttp = new ServerHttp(getClientSocket().getInputStream(), getClientSocket().getOutputStream(), _id);
        } catch (IOException e) {
            System.err.println("An error has occurred: Closing connection with client [ " + this.id + " ].\n");
        }
    }


    /**
     * thread main
     */
    public void run() {
        // depends if http1.0 or http1.1
        boolean error = false;
        // if not http/1.0 keep listening for new requests
        // else write response and close connection
        while (!error) {
            try {
                // read request
                this.getCom().readMessage();
                // write response and close connection if http/1.0
                this.getCom().writeResponse();
                if (this.getCom().HTTP_VERSION == 0) {
                    throw new IOException("HTTP/1.0");
                }
            } catch (IOException e) {
                System.err.println("Client [ " + this.id + " ] " + e.getMessage());
                System.err.println("Client [ " + this.id + " ] closing connection.");
                error = true;
            }
        }
    }

    public ServerHttp getCom() {
        return serverHttp;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }
}
