package utils.messages;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

public class NewConnectionRequest extends Message implements Serializable {

    public NewConnectionRequest(Socket connection) {
        super(connection);
    }

}
