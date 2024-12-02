package messages;

import java.net.Inet4Address;

public class NewConnectionRequest extends Message {

    public NewConnectionRequest(int senderPort, Inet4Address senderAddress, int recieverPort, Inet4Address recieverAddress) {
        super(senderPort, senderAddress, recieverPort, recieverAddress);
    }

}
