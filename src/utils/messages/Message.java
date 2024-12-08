package utils.messages;

import java.io.Serializable;
import java.net.InetAddress;

// abstract class to make sure all message data classes have the sender and receiver
public abstract class Message  implements Serializable {
    private int senderPort;
    private InetAddress senderAddress;
    private int receiverPort;
    private InetAddress receiverAddress;

    public Message(int senderPort, InetAddress senderAddress, int receiverPort, InetAddress receiverAddress) {
        this.senderPort = senderPort;
        this.senderAddress = senderAddress;
        this.receiverPort = receiverPort;
        this.receiverAddress = receiverAddress;
    }

    public int getSenderPort() {
        return senderPort;
    }

    public InetAddress getSenderAddress() {
        return senderAddress;
    }

    public int getReceiverPort() {
        return receiverPort;
    }

    public InetAddress getReceiverAddress() {
        return receiverAddress;
    }

    public void setSenderPort(int senderPort) {
        this.senderPort = senderPort;
    }

    public void setSenderAddress(InetAddress senderAddress) {
        this.senderAddress = senderAddress;
    }

    public void setReceiverPort(int receiverPort) {
        this.receiverPort = receiverPort;
    }

    public void setReceiverAddress(InetAddress receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    @Override
    public String toString() {
        return "Message: [sender="
                + this.senderAddress
                + ":"
                + this.senderPort
                + ", receiver="
                + this.receiverAddress
                + ":"
                + this.receiverPort
                + "]";
    }

}
