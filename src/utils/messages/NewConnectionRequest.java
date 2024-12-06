package utils.messages;

import java.io.Serializable;
import java.net.InetAddress;

public class NewConnectionRequest extends Message implements Serializable {

    public NewConnectionRequest(int senderPort, InetAddress senderAddress, int receiverPort, InetAddress receiverAddress) {
        super(senderPort, senderAddress, receiverPort, receiverAddress);
    }

}
