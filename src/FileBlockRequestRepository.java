import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileBlockRequestRepository {
    private final List<FileBlockRequestMessage> requestBlocks;
    private final List<FileBlockAnswerMessage> answerBlocks;

    public FileBlockRequestRepository(File file) {
        requestBlocks = new ArrayList<>();
        answerBlocks = new ArrayList<>();
        for (int i = 0; i < file.getTotalSpace(); i+= Constants.BLOCK_SIZE) {
            if (i + Constants.BLOCK_SIZE > file.getTotalSpace()) {
                int size = (int) (file.getTotalSpace() - i);
                addBlockRequest(file.hashCode(), i , size);
                break;
            }
            addBlockRequest(file.hashCode(), i, Constants.BLOCK_SIZE);
        }
    }

    public void addBlockRequest(int hash, int offset, int size) {
        FileBlockRequestMessage block = new FileBlockRequestMessage(hash, offset, size);
        requestBlocks.add(block);
    }

    public FileBlockRequestMessage getBlockRequest() {
        if (requestBlocks.isEmpty()) return null;
        return requestBlocks.removeFirst();
    }

    public File reconstructFile(String filename) {
        //TODO usar FileBlockAnswerMessage e juntar num so file
        File result = new File(filename);
        return result;
    }
}
