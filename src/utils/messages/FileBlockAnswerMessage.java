package utils.messages;

import java.io.Serializable;
import java.net.Inet4Address;

public class FileBlockAnswerMessage extends Message implements Serializable {
    private final String hash;
    private final byte[] data;

    public FileBlockAnswerMessage(int senderPort, Inet4Address senderAddress, int receiverPort, Inet4Address receiverAddress, String hash, byte[] data) {
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
        return "Message: [sender="
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