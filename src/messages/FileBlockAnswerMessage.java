package messages;

import java.util.Arrays;

public record FileBlockAnswerMessage(int hash, byte[] data) {

    @Override
    public String toString() {
        return "messages.FileBlockRequestMessage: [hash="
                + hash
                + ", data="
                + Arrays.toString(data)
                + "]";
    }
}