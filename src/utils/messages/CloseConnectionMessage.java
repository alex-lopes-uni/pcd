package utils.messages;

import java.io.Serializable;
import java.net.InetAddress;

public class CloseConnectionMessage extends Message implements Serializable {

    public CloseConnectionMessage(int senderPort, InetAddress senderAddress, int receiverPort, InetAddress receiverAddress) {
        super(senderPort, senderAddress, receiverPort, receiverAddress);
    }
}
