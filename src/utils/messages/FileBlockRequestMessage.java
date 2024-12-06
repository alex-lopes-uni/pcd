package utils.messages;

import java.io.Serializable;
import java.net.Socket;

public class FileBlockRequestMessage extends Message implements Serializable {
    private final String hash;
    private final int offset;
    private final int length;

    public FileBlockRequestMessage(Socket connection, String hash, int offset, int length) {
        super(connection);
        this.hash = hash;
        this.offset = offset;
        this.length = length;
    }

    public String getHash() {
        return hash;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
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
                + ", length="
                + this.length
                + ", offset="
                + this.offset
                + ")"
                + "]";
    }
}
