package utils.messages;

import java.io.Serializable;
import java.net.InetAddress;

// class FileBlockRequestMessage with the hash, the offset and the length of the block to get
public class FileBlockRequestMessage extends Message implements Serializable {
    private final String hash;
    private final int offset;
    private final int length;

    public FileBlockRequestMessage(int senderPort, InetAddress senderAddress, int receiverPort, InetAddress receiverAddress, String hash, int offset, int length) {
        super(senderPort, senderAddress, receiverPort, receiverAddress);
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

    public void setMessage(int senderPort, InetAddress senderAddress, int receiverPort, InetAddress receiverAddress) {
        this.setSenderPort(senderPort);
        this.setSenderAddress(senderAddress);
        this.setReceiverPort(receiverPort);
        this.setReceiverAddress(receiverAddress);
    }

    @Override
    public String toString() {
        return "FileBlockRequestMessage: [sender="
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
                + ", length="
                + this.length
                + ", offset="
                + this.offset
                + ")"
                + "]";
    }
}
