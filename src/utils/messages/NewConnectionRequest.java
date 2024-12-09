package utils.messages;

import java.io.Serializable;
import java.net.InetAddress;

// class NewConnectionRequest
public class NewConnectionRequest extends Message implements Serializable {

    public NewConnectionRequest(int senderPort, InetAddress senderAddress, int receiverPort, InetAddress receiverAddress) {
        super(senderPort, senderAddress, receiverPort, receiverAddress);
    }

    @Override
    public String toString() {
        return "NewConnectionRequest: [sender="
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
