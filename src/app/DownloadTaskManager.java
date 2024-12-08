package app;

import utils.Constants;
import utils.FileInfo;
import utils.messages.FileBlockRequestMessage;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class DownloadTaskManager extends Thread {
    private final List<FileBlockRequestMessage> requestBlocks;
    private final String hash;
    private List<String> downloadInfo;


    public DownloadTaskManager(FileInfo file) {
        this.hash = file.fileHash();
        requestBlocks = new ArrayList<>();

        for (int i = 0; i < file.fileSize(); i+= Constants.BLOCK_SIZE) {
            if (i + Constants.BLOCK_SIZE > file.fileSize()) {
                int size = (int) (file.fileSize() - i);
                addBlockRequest(i , size);
                break;
            }
            addBlockRequest(i, Constants.BLOCK_SIZE);
        }
    }

    public void addBlockRequest(int offset, int size) {
//        FileBlockRequestMessage block = new FileBlockRequestMessage(connection, hash, offset, size);
//        requestBlocks.add(block);
    }

    public FileBlockRequestMessage getBlockRequest() {
        if (requestBlocks.isEmpty()) return null;
        return requestBlocks.removeFirst();
    }

    public List<String> getDownloadInfo() {  // "[endereço=127.0.0.1, porta=8082]:253"
        return downloadInfo;
    }

}
