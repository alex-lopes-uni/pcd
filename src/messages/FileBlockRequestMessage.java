package messages;

public record FileBlockRequestMessage(int hash, int blockSize, int offset) {

    @Override
    public String toString() {
        return "messages.FileBlockRequestMessage: [hash="
                + hash
                + ", blockSize="
                + blockSize
                + ", offset="
                + offset
                + "]";
    }
}
