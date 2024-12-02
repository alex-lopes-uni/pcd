package messages;

import java.net.Inet4Address;

public abstract class Message {
    private final int senderPort;
    private final Inet4Address senderAddress;
    private final int recieverPort;
    private final Inet4Address recieverAddress;

    public Message(int senderPort, Inet4Address senderAddress, int recieverPort, Inet4Address recieverAddress) {
        this.senderPort = senderPort;
        this.senderAddress = senderAddress;
        this.recieverPort = recieverPort;
        this.recieverAddress = recieverAddress;
    }

    public int getSenderPort() {
        return senderPort;
    }

    public Inet4Address getSenderAddress() {
        return senderAddress;
    }

    public int getRecieverPort() {
        return recieverPort;
    }

    public Inet4Address getRecieverAddress() {
        return recieverAddress;
    }

    @Override
    public String toString() {
        return "Message: [sender="
                + this.senderAddress
                + ":"
                + this.senderPort
                + ", reciever="
                + this.recieverAddress
                + ":"
                + this.recieverPort
                + "]";
    }

}
