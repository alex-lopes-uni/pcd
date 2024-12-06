package utils.messages;

import java.io.Serializable;
import java.net.Socket;

public class CloseConnectionMessage extends Message implements Serializable {

    public CloseConnectionMessage(Socket connection) {
        super(connection);
    }
}
