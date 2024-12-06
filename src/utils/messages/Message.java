package utils.messages;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

// abstract class to make sure all message data classes have the sender and receiver
public abstract class Message  implements Serializable {
    private final Socket connection;

    public Message(Socket connection) {
        this.connection = connection;
    }

    public Socket getConnection() {
        return connection;
    }

    @Override
    public String toString() {
        return "Message: [sender="
                + this.connection.getLocalAddress().getHostAddress()
                + ":"
                + this.connection.getLocalPort()
                + ", receiver="
                + this.connection.getInetAddress()
                + ":"
                + this.connection.getPort()
                + "]";
    }

}
