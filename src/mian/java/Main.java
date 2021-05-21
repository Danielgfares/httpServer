package mian.java;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    /**
     * @param args -port portNumber
     */
    public static void main(String[] args) {
        // write your code here
        boolean error = false;
        int port = 0;
        if (args.length != 2) {
            System.err.println("Incorrect entry");
            print_help();
        } else {
            int index = 0;
            do {
                if (index == 0) {
                    if (!args[0].equals("-port")) {
                        error = true;
                    }
                } else if ( index == 1) {
                    try {
                        port = Integer.parseInt(args[1]);
                    } catch (Exception e) {
                        System.err.println("Option -port: must be a decimal");
                        error = true;
                    }
                }
                index++;
            } while (!error && index < args.length);
            if (error){
                print_help();
            } else {
                startConnection_RunApplication(port);
            }
        }
    }

    /**
     * Given a port number a server socket will start listening on this port waiting for new connections
     * @param port port where the server will listen
     */
    private static void startConnection_RunApplication(int port) {
        ServerSocket s1;
        Socket s2;
        int id = 1;
        ServerApplication s2_thread;
        boolean error = false;

        s1 = startConnection(port);
        if (s1 != null) {
            // iterate almost infinitely
            while (!error) {
                try {
                    // accepts connection and creates client socket
                    s2 = s1.accept();
                    System.out.println("Client %d. [ " + id + " ] " + "connection established: " + s2.getInetAddress());
                    // starts a new thread with the new client socket
                    // this way the main thread here can iterate and wait for new connections
                    s2_thread = new ServerApplication(s2, id);
                    s2_thread.start();
                    id++;
                } catch (IOException ex) {
                    System.err.println("Closing connection... An error has occurred during execution.");
                    error = true;
                }
            }
            try {
                s1.close();
            } catch (IOException ex) {
                System.err.println("Error closing server socket");
            }
        } else {
            print_help();
        }
    }

    /**
     * Create a server socket and start connection
     * @param port port where the socket will be listening
     * @return return the server socket created
     */
    private static ServerSocket startConnection(int port) {
        if (port != 0) {
            ServerSocket serverSocket;
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("Server listening, port: " + port + ".");
                System.out.println("Waiting for connection... ");
                return serverSocket;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void print_help() {
        System.out.println("java –jar httpserver.jar -port portNumber");
    }
}
