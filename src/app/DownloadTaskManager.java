package app;

import utils.Constants;
import utils.FileInfo;
import utils.messages.FileBlockRequestMessage;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class DownloadTaskManager {
    private final List<FileBlockRequestMessage> requestBlocks;
    private final Socket connection;
    private final String hash;


    public DownloadTaskManager(Socket connection, FileInfo file) {
        this.connection = connection;
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
        FileBlockRequestMessage block = new FileBlockRequestMessage(connection, hash, offset, size);
        requestBlocks.add(block);
    }

    public FileBlockRequestMessage getBlockRequest() {
        if (requestBlocks.isEmpty()) return null;
        return requestBlocks.removeFirst();
    }

}
