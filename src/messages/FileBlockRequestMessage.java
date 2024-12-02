package messages;

import java.net.Inet4Address;

public class FileBlockRequestMessage extends Message {
    private final int hash;
    private final int offset;
    private final int lenght;

    public FileBlockRequestMessage(int senderPort, Inet4Address senderAddress, int recieverPort, Inet4Address recieverAddress, int hash, int offset, int lenght) {
        super(senderPort, senderAddress, recieverPort, recieverAddress);
        this.hash = hash;
        this.offset = offset;
        this.lenght = lenght;
    }

    public int getHash() {
        return hash;
    }

    public int getOffset() {
        return offset;
    }

    public int getLenght() {
        return lenght;
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
                + ", lenght="
                + this.lenght
                + ", offset="
                + this.offset
                + ")"
                + "]";
    }
}
