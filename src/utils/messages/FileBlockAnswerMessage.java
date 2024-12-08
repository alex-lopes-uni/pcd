package utils.messages;

import java.io.Serializable;
import java.net.InetAddress;

public class FileBlockAnswerMessage extends Message implements Serializable {
    private final String hash;
    private final byte[] data;

    public FileBlockAnswerMessage(int senderPort, InetAddress senderAddress, int receiverPort, InetAddress receiverAddress, String hash, byte[] data) {
        super(senderPort, senderAddress, receiverPort, receiverAddress);
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
        return "FileBlockAnswerMessage: [sender="
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
                + ")"
                + "]";
    }

}