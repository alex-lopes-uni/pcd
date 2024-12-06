package utils.messages;

import java.io.Serializable;
import java.net.Socket;

public class WordSearchMessage extends Message implements Serializable {
    private final String keyword;

    public WordSearchMessage(Socket connection, String keyword) {
        super(connection);
        this.keyword = keyword;
    }

    public String getKeyword() {
        return this.keyword;
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
                + "keyword="
                + this.keyword
                + ")"
                + "]";
    }
}
