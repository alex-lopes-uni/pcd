package app;

import utils.Constants;
import utils.FileInfo;
import utils.ThreadHandler;
import utils.messages.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
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
                int size = (int) (this.fileInfo.fileSize() - i);
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

    public void addDownloadInfo(String address, int port, int length) {
        downloadInfo.add("[endereço=" + address + ", porta=" + port + "] : " + length);
    }

    public List<String> getDownloadInfo() {
        return downloadInfo;
    }

    public void makeRequests() {
        List<Thread> threads = new ArrayList<>();

        for (Socket connection : fileInfo.connections()) {
            DownloadThread handler = new DownloadThread(connection);
            threads.add(handler);
            handler.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }

    }

    public void writeFileInDirectory() {
        int size = answerBlocks.stream().mapToInt(block -> block.getData().length).sum();
        byte[] fileBytes = new byte[size];
        int offset = 0;

        for (FileBlockAnswerMessage block : answerBlocks) {
            System.arraycopy(block.getData(), 0, fileBytes, offset, block.getData().length);
            addDownloadInfo(block.getSenderAddress().toString(), block.getSenderPort(), block.getData().length);
            offset += block.getData().length;
        }

        try {
            Files.write(Paths.get(PATH), fileBytes);
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }

    }

    @Override
    public void run() {
        try {
            makeRequests();
        } finally {
            writeFileInDirectory();
        }

    }

    private class DownloadThread extends ThreadHandler {

        public DownloadThread(Socket connection) {
            super(connection);
        }

        @Override
        protected void processMessages() {
            while (!requestBlocks.isEmpty()) {
                try {
                    FileBlockRequestMessage request =  getBlockRequest(getConnection().getLocalPort(), getConnection().getLocalAddress(), getConnection().getPort(), getConnection().getLocalAddress());
                    sendMessageToConnection(request);

                    FileBlockAnswerMessage answer = (FileBlockAnswerMessage) in.readObject();
                    if (answer == null) {
                        System.out.println("An error occurred while reading the answer from the server" );
                        return;
                    }

                    answerBlocks.add(answer);

                } catch (ClassNotFoundException | IOException e) {
                    System.err.println("An error occurred: " + e.getMessage());
                    break;
                }
            }
        }


    }

}
