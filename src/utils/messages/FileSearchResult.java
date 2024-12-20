package utils.messages;

import java.io.Serializable;
import java.net.InetAddress;

// class FileSearchResult with the WordSearchMessage that originates it, the hash, file size and name
public class FileSearchResult extends Message  implements Serializable {
    private final WordSearchMessage wordSearchMessage;
    private final String hash;
    private final int fileSize;
    private final String fileName;


    public FileSearchResult(int senderPort, InetAddress senderAddress, int receiverPort, InetAddress receiverAddress, WordSearchMessage wordSearchMessage, String hash, int fileSize, String fileName) {
        super(senderPort, senderAddress, receiverPort, receiverAddress);
        this.wordSearchMessage = wordSearchMessage;
        this.hash = hash;
        this.fileSize = fileSize;
        this.fileName = fileName;
    }

    public String getHash() {
        return hash;
    }

    public WordSearchMessage getWordSearchMessage() {
        return wordSearchMessage;
    }

    public int getFileSize() {
        return fileSize;
    }

    public String getFileName() { return fileName; }

    @Override
    public String toString() {
        return "FileSearchResult Message: [sender="
                + this.getSenderAddress()
                + ":"
                + this.getSenderPort()
                + ", receiver="
                + this.getReceiverAddress()
                + ":"
                + this.getReceiverPort()
                + ", content="
                + "("
                + "hash="
                + this.hash
                + ", fileSize="
                + this.fileSize
                + ", fileName="
                + this.fileName
                + ")"
                + "]";
    }
}
