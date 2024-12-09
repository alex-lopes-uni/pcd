package utils;

import app.Node;
import utils.data_classes.FileInfo;
import utils.messages.*;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Manages download tasks for a specific file, coordinating requests and writing the final file
public class DownloadTaskManager extends Thread {
    private final String PATH;
    private List<FileBlockRequestMessage> requestBlocks;
    private final List<FileBlockAnswerMessage> answerBlocks;
    private final FileInfo fileInfo;
    private final List<String> downloadInfo;


    public DownloadTaskManager(FileInfo fileInfo, String path) {
        this.PATH = path;
        this.fileInfo = fileInfo;
        downloadInfo = new ArrayList<>();
        answerBlocks = new ArrayList<>();
        createFileRequestBlocks();
    }

    // Creates request blocks for the entire file
    public void createFileRequestBlocks() {
        requestBlocks = new ArrayList<>();

        for (int i = 0; i < this.fileInfo.fileSize(); i+= Constants.BLOCK_SIZE) {
            // Handle last smaller block
            if (i + Constants.BLOCK_SIZE > this.fileInfo.fileSize()) {
                int size = this.fileInfo.fileSize() - i;
                addBlockRequest(i , size);
                break;
            }
            // Standard block size
            addBlockRequest(i, Constants.BLOCK_SIZE);
        }
    }

    // Adds a block request with the specified offset and size
    public void addBlockRequest(int offset, int size) {
        FileBlockRequestMessage block = new FileBlockRequestMessage(0, null, 0, null, fileInfo.fileHash(), offset, size);
        requestBlocks.add(block);
    }

    // Retrieves the next block request and sets sender/receiver information
    public FileBlockRequestMessage getBlockRequest(int senderPort, InetAddress senderAddress, int receiverPort, InetAddress receiverAddress) {
        if (requestBlocks.isEmpty()) return null;
        FileBlockRequestMessage block = requestBlocks.removeFirst();
        block.setMessage(senderPort, senderAddress, receiverPort, receiverAddress);
        return block;
    }

    // Logs download information such as address, port, and duration
    public void addDownloadInfo(String address, int port, long time) {
        downloadInfo.add("[endereço=" + address + ", porta=" + port + "] : " + time + "ms");
    }

    // Returns the list of download info
    public List<String> getDownloadInfo() {
        return downloadInfo;
    }

    @Override
    public void run() {
        try {
            makeRequests();
        } finally {
            writeFileInDirectory();
        }

    }

    // Initiates download requests by creating threads for each connection
    public void makeRequests() {
        List<Thread> threads = new ArrayList<>();

        for (Node.NodeConnectionThread nodeConnectionThread : fileInfo.nodeConnectionThreads()) {
            DownloadThread handler = new DownloadThread(nodeConnectionThread);
            threads.add(handler);
            handler.start();
        }

        // Waits for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.err.println(this.getClass() + ": [" + "makeRequests()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
            }
        }

    }

    // Combines all received blocks and writes the file to the specified directory
    public void writeFileInDirectory() {
        int size = answerBlocks.stream().mapToInt(block -> block.getData().length).sum();
        byte[] fileBytes = new byte[size];
        int offset = 0;

        // Turn all the blocks into one array
        for (FileBlockAnswerMessage block : answerBlocks) {
            System.arraycopy(block.getData(), 0, fileBytes, offset, block.getData().length);
            offset += block.getData().length;
        }

        // Write the array in a file in the directory
        try {
            Files.write(Paths.get(PATH + "\\" + fileInfo.fileName()), fileBytes);
        } catch (IOException e) {
            System.err.println(this.getClass() + ": [" + "writeFileInDirectory()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
            downloadInfo.clear();
        }

    }

    // Inner class to handle individual download tasks from a specific connection
    public class DownloadThread extends Thread {
        private final Node.NodeConnectionThread nodeConnectionThread;
        private final Object lock = new Object();
        private long time;

        public DownloadThread(Node.NodeConnectionThread nodeConnectionThread) {
            this.nodeConnectionThread = nodeConnectionThread;
            this.nodeConnectionThread.addDownloadThread(this);
        }

        // Main execution: sends requests and waits for responses
        @Override
        public void run() {
            synchronized (lock) {
                while (!requestBlocks.isEmpty()) {
                    try {
                        // Sends request
                        FileBlockRequestMessage request =  getBlockRequest(nodeConnectionThread.getConnection().getLocalPort(), nodeConnectionThread.getConnection().getLocalAddress(), nodeConnectionThread.getConnection().getPort(), nodeConnectionThread.getConnection().getLocalAddress());
                        nodeConnectionThread.sendMessageToConnection(request);
                        time = System.currentTimeMillis();
                        // Waits for the block
                        lock.wait();
                    } catch (InterruptedException e) {
                        System.err.println(this.getClass() + ": [" + "run()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
                        break;
                    }
              }
            }
        }

        // Handles responses and updates the list of received blocks
        public void messageAnswer(FileBlockAnswerMessage answer) {
            synchronized (lock) {
                long end = System.currentTimeMillis();
                long diff = end - time;
                // Add block to list
                answerBlocks.add(answer);
                // Add info to display in GUI
                addDownloadInfo(answer.getSenderAddress().toString(), answer.getSenderPort(), diff);
                // Unlocks so that the thread can send the next request
                lock.notify();
            }
        }

    }

}
