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

    public void createFileRequestBlocks() {
        requestBlocks = new ArrayList<>();

        for (int i = 0; i < this.fileInfo.fileSize(); i+= Constants.BLOCK_SIZE) {
            if (i + Constants.BLOCK_SIZE > this.fileInfo.fileSize()) {
                int size = this.fileInfo.fileSize() - i;
                addBlockRequest(i , size);
                break;
            }
            addBlockRequest(i, Constants.BLOCK_SIZE);
        }
    }

    public void addBlockRequest(int offset, int size) {
        FileBlockRequestMessage block = new FileBlockRequestMessage(0, null, 0, null, fileInfo.fileHash(), offset, size);
        requestBlocks.add(block);
    }

    public FileBlockRequestMessage getBlockRequest(int senderPort, InetAddress senderAddress, int receiverPort, InetAddress receiverAddress) {
        if (requestBlocks.isEmpty()) return null;
        FileBlockRequestMessage block = requestBlocks.removeFirst();
        block.setMessage(senderPort, senderAddress, receiverPort, receiverAddress);
        return block;
    }

    public void addDownloadInfo(String address, int port, long time) {
        downloadInfo.add("[endereço=" + address + ", porta=" + port + "] : " + time);
    }

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

    public void makeRequests() {
        List<Thread> threads = new ArrayList<>();

        for (Node.NodeConnectionThread nodeConnectionThread : fileInfo.nodeConnectionThreads()) {
            DownloadThread handler = new DownloadThread(nodeConnectionThread);
            threads.add(handler);
            handler.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.err.println(this.getClass() + ": [" + "makeRequests()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
            }
        }

    }

    public void writeFileInDirectory() {
        int size = answerBlocks.stream().mapToInt(block -> block.getData().length).sum();
        byte[] fileBytes = new byte[size];
        int offset = 0;

        for (FileBlockAnswerMessage block : answerBlocks) {
            System.arraycopy(block.getData(), 0, fileBytes, offset, block.getData().length);
            offset += block.getData().length;
        }

        try {
            Files.write(Paths.get(PATH), fileBytes);
        } catch (IOException e) {
            System.err.println(this.getClass() + ": [" + "writeFileInDirectory()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
            downloadInfo.clear();
        }

    }

    public class DownloadThread extends Thread {
        private final Node.NodeConnectionThread nodeConnectionThread;
        private final Object lock = new Object();
        private long time;

        public DownloadThread(Node.NodeConnectionThread nodeConnectionThread) {
            this.nodeConnectionThread = nodeConnectionThread;
            this.nodeConnectionThread.addDownloadThread(this);
        }

        @Override
        public void run() {
            synchronized (lock) {
                while (!requestBlocks.isEmpty()) {
                    try {
                        FileBlockRequestMessage request =  getBlockRequest(nodeConnectionThread.getConnection().getLocalPort(), nodeConnectionThread.getConnection().getLocalAddress(), nodeConnectionThread.getConnection().getPort(), nodeConnectionThread.getConnection().getLocalAddress());
                        nodeConnectionThread.sendMessageToConnection(request);
                        time = System.currentTimeMillis();
                        lock.wait();
                    } catch (InterruptedException e) {
                        System.err.println(this.getClass() + ": [" + "run()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
                        break;
                    }
              }
            }
        }

        public void messageAnswer(FileBlockAnswerMessage answer) {
            synchronized (lock) {
                long end = System.currentTimeMillis();
                long diff = end - time;
                answerBlocks.add(answer);
                addDownloadInfo(answer.getSenderAddress().toString(), answer.getSenderPort(), diff);
                lock.notify();
            }
        }

    }

}
