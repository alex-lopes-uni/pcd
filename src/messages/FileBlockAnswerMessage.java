package messages;

import java.net.Inet4Address;

public class FileBlockAnswerMessage extends Message {
    private final int hash;
    private final byte[] data;

    public FileBlockAnswerMessage(int senderPort, Inet4Address senderAddress, int recieverPort, Inet4Address recieverAddress, int hash, byte[] data) {
        super(senderPort, senderAddress, recieverPort, recieverAddress);
        this.hash = hash;
        this.data = data;
    }

    public int getHash() {
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
                + ", reciever="
                + this.getRecieverAddress()
                + ":"
                + this.getRecieverPort()
                + ", content="
                + "("
                + "hash="
                + this.hash
                + ")"
                + "]";
    }

}