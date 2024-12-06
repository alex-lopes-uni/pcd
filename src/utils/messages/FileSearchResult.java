package utils.messages;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

public class FileSearchResult extends Message  implements Serializable {
    private final WordSearchMessage wordSearchMessage;
    private final String hash;
    private final int fileSize;
    private final String fileName;


    public FileSearchResult(Socket connection, WordSearchMessage wordSearchMessage, String hash, int fileSize, String fileName) {
        super(connection);
        this.wordSearchMessage = wordSearchMessage;
        this.hash = hash;
        this.fileSize = fileSize;
        this.fileName = fileName;
    }

    public WordSearchMessage getWordSearchMessage() {
        return wordSearchMessage;
    }

    public String getHash() {
        return hash;
    }

    public int getFileSize() {
        return fileSize;
    }

    public String getFileName() { return fileName; }

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
                + ", fileSize="
                + this.fileSize
                + ", filename="
                + this.fileName
                + ")"
                + "]";
    }
}
