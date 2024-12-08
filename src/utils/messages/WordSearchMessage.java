package utils.messages;

import java.io.Serializable;
import java.net.InetAddress;

public class WordSearchMessage extends Message implements Serializable {
    private final String keyword;

    public WordSearchMessage(int senderPort, InetAddress senderAddress, int receiverPort, InetAddress receiverAddress, String keyword) {
        super(senderPort, senderAddress, receiverPort, receiverAddress);
        this.keyword = keyword;
    }

    public String getKeyword() {
        return this.keyword;
    }

    @Override
    public String toString() {
        return "WordSearchMessage: [sender="
                + this.getSenderAddress()
                + ":"
                + this.getSenderPort()
                + ", receiver="
                + this.getReceiverAddress()
                + ":"
                + this.getReceiverPort()
                + ", content="
                + "("
                + "keyword="
                + this.keyword
                + ")"
                + "]";
    }
}
