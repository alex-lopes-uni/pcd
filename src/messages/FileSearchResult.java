package messages;

import java.net.Inet4Address;

public class FileSearchResult extends Message {
    private final WordSearchMessage wordSearchMessage;
    private final String hash;
    private final int fileSize;


    public FileSearchResult(int senderPort, Inet4Address senderAddress, int recieverPort, Inet4Address recieverAddress, WordSearchMessage wordSearchMessage, String hash, int fileSize) {
        super(senderPort, senderAddress, recieverPort, recieverAddress);
        this.wordSearchMessage = wordSearchMessage;
        this.hash = hash;
        this.fileSize = fileSize;
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

    @Override
    public String toString() {
        return "Message: [sender="
                + this.getSenderAddress()
                + ":"
                + this.getSenderPort()
                + ", reciever="
                + this.getRecieverAddress()
                + ":"
                + this.getRecieverPort()
                + ", content="
                + "("
                + "hash="
                + this.hash
                + ", fileSize="
                + this.fileSize
                + ")"
                + "]";
    }
}
