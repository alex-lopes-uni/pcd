package messages;

import java.net.Inet4Address;

public class WordSearchMessage extends Message {
    private final String keyword;

    public WordSearchMessage(int senderPort, Inet4Address senderAddress, int recieverPort, Inet4Address recieverAddress, String keyword) {
        super(senderPort, senderAddress, recieverPort, recieverAddress);
        this.keyword = keyword;
    }

    public String getKeyword() {
        return this.keyword;
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
                + "keyword="
                + this.keyword
                + ")"
                + "]";
    }
}
