package app;

import utils.DownloadTaskManager;
import utils.messages.*;
import utils.data_classes.FileInfo;
import utils.data_classes.RepositoryFile;
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
            System.err.println(this.getClass() + ": [" + "setRepositoryFiles()" + ": (error: " + "Invalid main directory path" + ")]");
            System.err.println();
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
            System.err.println(this.getClass() + ": [" + "setRepositoryFiles()" + ": (error: " + "No files found" + ")]");
        }

        repositoryFiles = result;
    }

    public void runServer() {
        gui.openGUI();
        try {
            ss = new ServerSocket(PORT);
            System.out.println("[node created in port: " + PORT + "]");
            waitForConnections();
        } catch (IOException e) {
            System.err.println(this.getClass() + ": [" + "runServer()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
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
                System.out.println("[connection established: " + connection.getInetAddress().getHostName() + ":" + connection.getPort() + "]");
            } catch (IOException e) {
                System.err.println(this.getClass() + ": [" + "waitForConnections()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
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
            System.err.println(this.getClass() + ": [" + "stopServer()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
        }
        System.out.println("[server stopped]");
    }

    public String connectToNode(String connectionAddress, int connectionPort) {
        try {
            connection = new Socket(InetAddress.getByName(connectionAddress), connectionPort);
            NodeConnectionThread handler = new NodeConnectionThread(connection);
            synchronized (threads) {
                threads.add(handler);
            }
            threads.get(threads.indexOf(handler)).start();
            System.out.println("[connection established: " + connectionAddress + ":" + connectionPort + "]");
            return "success";
        } catch (IOException e) {
            System.err.println(this.getClass() + ": [" + "connectToNode()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
            return e.getMessage();
        }

    }

    public void closeAllConnections() {
        for (NodeConnectionThread thread : threads) {
            if (thread.isAlive()) {
                thread.closeConnection();
                thread.interrupt();
            }
        }
    }

    public void search(String keyword) {
        searchInfo.clear();
        for (NodeConnectionThread thread : threads) {
            thread.processSearch(keyword);
        }

    }

    public void download(String fileName) {
        FileInfo fileInfo = searchInfo.stream().filter(file -> file.fileName().equals(fileName)).findFirst().orElse(null);

        if (fileInfo == null) {
            System.err.println(this.getClass() + ": [" + "download()" + ": (error: " + "The file info for downloading was not found" + ")]");
            return;
        }

        DownloadTaskManager downloadTaskManager = new DownloadTaskManager(fileInfo, PATH);
        long startTime = System.currentTimeMillis();
        downloadTaskManager.start();

        try {
            downloadTaskManager.join();
        } catch (InterruptedException e) {
            System.err.println(this.getClass() + ": [" + "download()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
        }

        long endTime = System.currentTimeMillis();
        setRepositoryFiles();

        long elapsedTime = endTime - startTime;
        long time = elapsedTime / 1000;

        gui.showDownloadInfo(fileName, time, downloadTaskManager.getDownloadInfo());

    }

    public class NodeConnectionThread extends Thread {
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private final Socket connection;

        public NodeConnectionThread(Socket connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            getStreams();
            processMessages();
        }

        private void getStreams() {
            try {
                out = new ObjectOutputStream(connection.getOutputStream());
                out.flush();
                in = new ObjectInputStream(connection.getInputStream());
                System.out.println("[streams successfully created]");
            } catch (IOException e) {
                System.err.println(this.getClass() + ": [" + "getStreams()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
            }
        }

        private void processMessages() {
            while (true) {
                try {
                    Object object = in.readObject();
                    System.out.println("[received: " + object + "]");
                    switch (object) {
                        case FileBlockRequestMessage message:
                            handleFileBlockRequestMessage(message);
                            break;
                        case FileSearchResult message:
                            handleFileSearchResult(message);
                            break;
                        case WordSearchMessage message:
                            handleWordSearchMessage(message);
                            break;
                        case CloseConnectionRequest _:
                            close();
                            return;
                        default:
                    }
                } catch (ClassNotFoundException | IOException e) {
                    if (connection.isClosed()) {
                        return;
                    }
                    System.err.println(this.getClass() + ": [" + "processMessages()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
                    closeConnection();
                    return;
                }
            }
        }

        // sends a message to the other side so that it is also closed
        public void closeConnection() {
            CloseConnectionRequest message = new CloseConnectionRequest(connection.getLocalPort(), connection.getLocalAddress(), connection.getPort(), connection.getInetAddress());
            sendMessageToConnection(message);
            close();
        }

        synchronized private void close() {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (connection != null && !connection.isClosed()) connection.close();

            } catch (IOException e) {
                System.err.println(this.getClass() + ": [" + "close()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
            }
            System.out.println("[closed connection]");
            this.interrupt();

        }

        public synchronized void sendMessageToConnection(Message message) {
            try {
                out.writeObject(message);
                out.flush();
            } catch (IOException e) {
                System.err.println(this.getClass() + ": [" + "sendMessageToConnection()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
            }
            System.out.println("[sent: " + message + "]");
        }

        synchronized private void handleFileBlockRequestMessage(FileBlockRequestMessage input) {
            RepositoryFile file = repositoryFiles.stream().filter(f -> f.getHash().equals(input.getHash())).findFirst().orElse(null);
            FileBlockAnswerMessage response;

            if (file == null) {
                System.err.println(this.getClass() + ": [" + "sendMessageToConnection()" + ": (error: " + "File not found" + ")]");
                response = null;
            } else {
                response = new FileBlockAnswerMessage(input.getReceiverPort(), input.getReceiverAddress(), input.getSenderPort(), input.getSenderAddress(), input.getHash(), file.getFileBlock(input.getOffset(), input.getLength()));
            }

            try {
                out.writeObject(response);
                out.flush();
            } catch (IOException e) {
                System.err.println(this.getClass() + ": [" + "sendMessageToConnection()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
            }

        }

        synchronized private void handleFileSearchResult(FileSearchResult input) {
            for(FileInfo fileInfo : searchInfo) {
                if(fileInfo.fileName().equals(input.getFileName())) {
                    searchInfo.remove(fileInfo);
                    fileInfo.nodeConnectionThreads().add(this);
                    searchInfo.add(fileInfo);
                    return;
                }
            }
            List<NodeConnectionThread> nodeConnectionThreads = new ArrayList<>();
            nodeConnectionThreads.add(this);
            searchInfo.add(new FileInfo(input.getFileName(), input.getHash(), input.getFileSize(), nodeConnectionThreads));
            SwingUtilities.invokeLater(() -> gui.addToFileList(input.getFileName()));
            System.out.println("[obtained results from: " + input.getWordSearchMessage() + "]");
        }

        synchronized private void handleWordSearchMessage(WordSearchMessage input) {
            for (RepositoryFile file : repositoryFiles) {
                String fileName = file.getFileName();
                int index = fileName.indexOf(input.getKeyword());
                if (index != -1) {
                    FileSearchResult message = new FileSearchResult(connection.getLocalPort(), connection.getLocalAddress(), connection.getPort(), connection.getInetAddress(), input, file.getHash(), file.getSize(), fileName);
                    sendMessageToConnection(message);
                }
            }

        }

        synchronized private void processSearch(String keyword) {
            WordSearchMessage message = new WordSearchMessage(connection.getLocalPort(), connection.getLocalAddress(), connection.getPort(), connection.getInetAddress(), keyword);
            sendMessageToConnection(message);
        }

        public Socket getConnection() {
            return connection;
        }

        public ObjectInputStream getIn() {
            return in;
        }

    }
}
