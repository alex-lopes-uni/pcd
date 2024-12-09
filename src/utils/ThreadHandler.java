package utils;

import utils.messages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class ThreadHandler extends Thread {
    protected ObjectInputStream in;
    protected ObjectOutputStream out;
    private final Socket connection;

    public ThreadHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            getStreams();
            processMessages();
        } finally {
            closeConnection();
        }
    }

    private void getStreams() {
        try {
            System.out.println("Getting Streams...");
            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());
            System.out.println("Streams are ready!");
        } catch (IOException e) {
            System.err.println("Get Streams: [exception: " + e.getClass().getName() + ", error: " + e.getMessage() + "]");
        }
    }

    protected void processMessages() {
        System.out.println("Processing Messages...");
    }

    protected synchronized void sendMessageToConnection(Message message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.err.println("Send Message to Connection: [exception: " + e.getClass().getName() + ", error: " + e.getMessage() + "]");
        }
        System.out.println("[sent message: " + message + "]");
    }

    private void closeConnection() {
        CloseConnectionRequest message = new CloseConnectionRequest(connection.getLocalPort(), connection.getLocalAddress(), connection.getPort(), connection.getInetAddress());
        sendMessageToConnection(message);
        close();
    }

    protected void close() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            connection.close();

        } catch (IOException e) {
            System.err.println("Close: [exception: " + e.getClass().getName() + ", error: " + e.getMessage() + "]");
        }
    }

    public Socket getConnection() {
        return connection;
    }

}
