public class FileBlockRequestMessage {
    private final int hash;
    private final int blockSize;
    private final int offset;

    public FileBlockRequestMessage(int hash, int blockSize, int offset) {
        this.hash = hash;
        this.blockSize = blockSize;
        this.offset = offset;
    }

    public int getHash() { return hash; }

    public int getBlockSize() { return blockSize; }

    public int getOffset() { return offset; }

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
