package utils.messages;

import java.io.Serializable;
import java.net.Socket;

public class FileBlockAnswerMessage extends Message implements Serializable {
    private final String hash;
    private final byte[] data;

    public FileBlockAnswerMessage(Socket connection, String hash, byte[] data) {
        super(connection);
        this.hash = hash;
        this.data = data;
    }

    public String getHash() {
        return hash;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Message: [sender="
                + this.getConnection().getLocalAddress().getHostAddress()
                + ":"
                + this.getConnection().getLocalPort()
                + ", receiver="
                + this.getConnection().getInetAddress()
                + ":"
                + this.getConnection().getPort()
                + ", content="
                + "("
                + "hash="
                + this.hash
                + ")"
                + "]";
    }

}