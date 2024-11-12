public record FileBlockRequestMessage(int hash, int blockSize, int offset) {

    @Override
    public String toString() {
        return "FileBlockRequestMessage: [hash="
                + hash
                + ", blockSize="
                + blockSize
                + ", offset="
                + offset
                + "]";
    }
}
