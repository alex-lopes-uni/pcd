import java.util.Arrays;

public record FileBlockAnswerMessage(int hash, byte[] data) {

    @Override
    public String toString() {
        return "FileBlockRequestMessage: [hash="
                + hash
                + ", data="
                + Arrays.toString(data)
                + "]";
    }
}