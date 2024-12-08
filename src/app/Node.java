package app;

import utils.messages.*;
import utils.FileInfo;
import utils.RepositoryFile;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;


public class Node {
    private final String PATH;
    private List<RepositoryFile> repositoryFiles;
    private final int PORT;
    private ServerSocket ss;
    private Socket connection;
    private volatile boolean running = true;
    private final List<NodeConnectionThread> threads = new ArrayList<>();
    private final GUI gui;
    private final List<FileInfo> searchInfo = new ArrayList<>();

    public Node(String folderName, int PORT) {
        this.PORT = PORT;
        PATH = System.getProperty("user.dir") + "/" + folderName;
        setRepositoryFiles();
        gui = new GUI(this);
    }

    public List<RepositoryFile> getRepositoryFiles() {
        return this.repositoryFiles;
    }

    public int getPort() {
        return PORT;
    }

    public void setRepositoryFiles() {
        File dir = new File(PATH);
        List<RepositoryFile> result = new ArrayList<>();

        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Invalid main directory path");
            return;
        }

        File[] temp = dir.listFiles();

        if (temp != null) {
            for (File file : temp) {
                if (!file.isDirectory()) {
                    result.add(new RepositoryFile(file));
                }
            }
        } else {
            System.out.println("No files found");
        }

        repositoryFiles = result;
    }

    public void runServer() {
        gui.openGUI();
        try {
            ss = new ServerSocket(PORT);
            System.out.println("node created in port: " + PORT);
            waitForConnections();
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        } finally {
            stopServer();
        }
    }

    public void waitForConnections() {
        while (running) {
            try {
                connection = ss.accept();
                NodeConnectionThread handler = new NodeConnectionThread(connection);
                synchronized (threads) {
                    threads.add(handler);
                }
                threads.get(threads.indexOf(handler)).start();
                System.out.println("Connection to " + connection.getInetAddress().getHostName() + ":" + connection.getPort() + " is established!");
            } catch (IOException e) {
                System.err.println("An error occurred: " + e.getMessage());
            }

        }
    }

    public void stopServer() {
        running = false;
        try {
            for (NodeConnectionThread thread : threads) {
                thread.interrupt();
            }
            if (connection != null)
                connection.close();
            if (ss != null)
                ss.close();
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
        System.out.println("Server stopped");
    }

    public String connectToNode(String connectionAddress, int connectionPort) {
        try {
            connection = new Socket(InetAddress.getByName(connectionAddress), connectionPort);
            NodeConnectionThread handler = new NodeConnectionThread(connection);
            synchronized (threads) {
                threads.add(handler);
            }
            threads.get(threads.indexOf(handler)).start();
            System.out.println("Connection to " + connectionAddress + ":" + connectionPort + " is established!");
            return "success";
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            return e.getMessage();
        }

    }

    public void search(String keyword) {
        searchInfo.clear();
        for (NodeConnectionThread thread : threads) {
            thread.processSearch(keyword);
            System.out.println("Searching for \"" + keyword + "\" at " + thread.connection.getInetAddress().getHostName() + ":" + thread.connection.getPort());
        }

    }

    // TODO DOWNLOAD FUNCTION
    public void download(String fileName) {
        FileInfo fileInfo = searchInfo.stream().filter(file -> file.fileName().equals(fileName)).findFirst().orElse(null);

        if (fileInfo == null) {
            System.err.println("An error occurred: The file info for downloading was not found");
            return;
        }

        DownloadTaskManager downloadTaskManager = new DownloadTaskManager(fileInfo);
        long startTime = System.currentTimeMillis();
        downloadTaskManager.start();

        try {
            downloadTaskManager.join();
        } catch (InterruptedException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }

        long endTime = System.currentTimeMillis();
        setRepositoryFiles();

        long elapsedTime = endTime - startTime;
        long time = elapsedTime / 1000;

        gui.showDownloadInfo(fileName, time, downloadTaskManager.getDownloadInfo());

    }

    private class NodeConnectionThread extends Thread {
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private final Socket connection;

        public NodeConnectionThread(Socket connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                getStreams();
                processMessages();
            } finally {
                closeConnection();
            }
        }

        private void getStreams() {
            try {
                System.out.println("Getting Streams...");
                out = new ObjectOutputStream(connection.getOutputStream());
                out.flush();
                in = new ObjectInputStream(connection.getInputStream());
                System.out.println("Streams are ready!");
            } catch (IOException e) {
                System.err.println("An error occurred: " + e.getMessage());
            }
        }

        private void processMessages() {
            while (true) {
                try {
                    Object object = in.readObject();
                    switch (object) {
                        case FileBlockRequestMessage message:
                            System.out.println("[received message: " + message + "]");
                            handleFileBlockRequestMessage(message);
                            break;
                        case FileSearchResult message:
                            System.out.println("[received message: " + message + "]");
                            handleFileSearchResult(message);
                            break;
                        case WordSearchMessage message:
                            System.out.println("[received message: " + message + "]");
                            handleWordSearchMessage(message);
                            break;
                        case CloseConnectionRequest message:
                            System.out.println("[received message: " + message + "]");
                            close();
                            System.out.println("[closed connection]");
                        case null:
                        default:
                            break;
                    }
                } catch (ClassNotFoundException | IOException e) {
                    System.err.println("An error occurred: " + e.getMessage());
                    break; // Exit the loop on exceptions.
                }
            }
        }

        synchronized private void sendMessageToConnection(Message message) {
            try {
                out.writeObject(message);
                out.flush();
            } catch (IOException e) {
                System.err.println("An error occurred: " + e.getMessage());
            }
            System.out.println("[sent message: " + message + "]");
        }

        private void closeConnection() {
            CloseConnectionRequest message = new CloseConnectionRequest(connection.getLocalPort(), connection.getLocalAddress(), connection.getPort(), connection.getInetAddress());
            sendMessageToConnection(message);
            close();
        }

        private void close() {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                synchronized (threads) {
                    threads.remove(this);
                }
                connection.close();

            } catch (IOException e) {
                System.err.println("An error occurred: " + e.getMessage());
            }
        }

        // TODO
        synchronized private void handleFileBlockRequestMessage(FileBlockRequestMessage input) {
            RepositoryFile file = repositoryFiles.stream().filter(f -> f.getHash().equals(input.getHash())).findFirst().orElse(null);
            FileBlockAnswerMessage response;

            if (file == null) {
                System.err.println("An error occurred: File not found");
                response = null;
            } else {
                response = new FileBlockAnswerMessage(input.getReceiverPort(), input.getReceiverAddress(), input.getSenderPort(), input.getSenderAddress(), input.getHash(), file.getFileBlock(input.getOffset(), input.getLength()));
            }

            try {
                out.writeObject(response);
                out.flush();
            } catch (IOException e) {
                System.err.println("An error occurred: " + e.getMessage());
            }

        }

        synchronized private void handleFileSearchResult(FileSearchResult input) {
            for(FileInfo fileInfo : searchInfo) {
                if(fileInfo.fileName().equals(input.getFileName())) {
                    searchInfo.remove(fileInfo);
                    fileInfo.connections().add(connection);
                    searchInfo.add(fileInfo);
                    return;
                }
            }
            List<Socket> connections = new ArrayList<>();
            connections.add(connection);
            searchInfo.add(new FileInfo(input.getFileName(), input.getHash(), input.getFileSize(), connections));
            SwingUtilities.invokeLater(() -> gui.addToFileList(input.getFileName()));
            System.out.println("Completed search query: " + input.getWordSearchMessage());
        }

        synchronized private void handleWordSearchMessage(WordSearchMessage input) {
            for (RepositoryFile file : repositoryFiles) {
                String fileName = file.getFileName();
                int index = fileName.indexOf(input.getKeyword());
                if (index != -1) {
                    FileSearchResult message = new FileSearchResult(connection.getLocalPort(), connection.getLocalAddress(), connection.getPort(), connection.getInetAddress(), input, file.getHash(), (int) file.getFile().getTotalSpace(), fileName);
                    sendMessageToConnection(message);
                }
            }

        }

        synchronized private void processSearch(String keyword) {
            WordSearchMessage message = new WordSearchMessage(connection.getLocalPort(), connection.getLocalAddress(), connection.getPort(), connection.getInetAddress(), keyword);
            sendMessageToConnection(message);
        }

    }
}
