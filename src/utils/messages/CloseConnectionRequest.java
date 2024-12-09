package utils.messages;

import java.io.Serializable;
import java.net.InetAddress;

// message type to close the connection on the other side
public class CloseConnectionRequest extends Message implements Serializable {

    public CloseConnectionRequest(int senderPort, InetAddress senderAddress, int receiverPort, InetAddress receiverAddress) {
        super(senderPort, senderAddress, receiverPort, receiverAddress);
    }

    @Override
    public String toString() {
        return "CloseConnectionRequest: [sender="
                + this.getSenderAddress()
                + ":"
                + this.getSenderPort()
                + ", receiver="
                + this.getReceiverAddress()
                + ":"
                + this.getReceiverPort()
                + "]";
    }

}
