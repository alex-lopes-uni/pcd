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
        PATH = System.getProperty("user.dir") + "\\" + folderName;
        // Sets the list of files in the repository
        setRepositoryFiles();
        // Initializes the GUI
        gui = new GUI(this);
    }

    // Gets the list of repository files
    public List<RepositoryFile> getRepositoryFiles() {
        return this.repositoryFiles;
    }

    // Gets the port on which the node is running
    public int getPort() {
        return PORT;
    }

    // Sets the repository files from the specified directory
    public void setRepositoryFiles() {
        File dir = new File(PATH);
        List<RepositoryFile> result = new ArrayList<>();

        // Check if the directory exists and is valid
        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println(this.getClass() + ": [" + "setRepositoryFiles()" + ": (error: " + "Invalid main directory path" + ")]");
            System.err.println();
            return;
        }

        // Add each file in the directory to the list of repository files
        File[] temp = dir.listFiles();
        if (temp != null) {
            for (File file : temp) {
                if (!file.isDirectory() && file.getName().endsWith(".mp3")) {
                    result.add(new RepositoryFile(file));
                }
            }
        } else {
            System.err.println(this.getClass() + ": [" + "setRepositoryFiles()" + ": (error: " + "No files found" + ")]");
        }

        // Update the repository files list
        repositoryFiles = result;
    }

    // Starts the server and opens the GUI
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

    // Waits for incoming connections and creates a new thread for each connection
    public void waitForConnections() {
        while (running) {
            try {
                connection = ss.accept();

                // Create a handler for the connection
                NodeConnectionThread handler = new NodeConnectionThread(connection);
                // Add the handler to the list of threads
                synchronized (threads) {
                    threads.add(handler);
                }
                // Start the handler thread
                threads.get(threads.indexOf(handler)).start();
            } catch (IOException e) {
                System.err.println(this.getClass() + ": [" + "waitForConnections()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
            }

        }
    }

    // Stops the server and closes all active connections
    public void stopServer() {
        // Set the server state to not running
        running = false;
        try {
            // Interrupt each active connection thread
            for (NodeConnectionThread thread : threads) thread.interrupt();

            // Close the ServerSocket
            if (connection != null) connection.close();
            if (ss != null)  ss.close();

        } catch (IOException e) {
            System.err.println(this.getClass() + ": [" + "stopServer()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
        }
        System.out.println("[server stopped]");
    }

    // Connects to another node using the provided address and port
    public String connectToNode(String connectionAddress, int connectionPort) {
        try {
            // Create a new socket connection
            connection = new Socket(InetAddress.getByName(connectionAddress), connectionPort);
            // Create a handler for the connection
            NodeConnectionThread handler = new NodeConnectionThread(connection);
            // Add the handler to the list of threads
            synchronized (threads) {
                threads.add(handler);
            }
            threads.get(threads.indexOf(handler)).start();
            return "success"; // Return success if the connection is established so that the GUI knows the connection status
        } catch (IOException e) {
            System.err.println(this.getClass() + ": [" + "connectToNode()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
            return e.getMessage(); // Return the error message if the connection fails
        }

    }

    // Closes all active connections to other nodes
    public void closeAllConnections() {
        for (NodeConnectionThread thread : threads) {
            if (thread.isAlive()) {
                thread.closeConnection();
                thread.interrupt();
            }
        }
    }

    // Starts the search process for a given keyword
    public void search(String keyword) {
        searchInfo.clear();
        // Process the search on each connection thread
        for (NodeConnectionThread thread : threads) {
            thread.processSearch(keyword);
        }

    }

    // Starts the download process for a given file
    public void download(String fileName) {
        FileInfo fileInfo = searchInfo.stream().filter(file -> file.fileName().equals(fileName)).findFirst().orElse(null);

        if (fileInfo == null) {
            System.err.println(this.getClass() + ": [" + "download()" + ": (error: " + "The file info for downloading was not found" + ")]");
            return;
        }

        // Start the download task
        DownloadTaskManager downloadTaskManager = new DownloadTaskManager(fileInfo, PATH);
        long startTime = System.currentTimeMillis();
        downloadTaskManager.start();

        // Wait for the download task to finish
        try {
            downloadTaskManager.join();
        } catch (InterruptedException e) {
            System.err.println(this.getClass() + ": [" + "download()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
        }

        // Update the repository files list
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        setRepositoryFiles();

        // Show download info in the GUI
        gui.showDownloadInfo(fileName, time, downloadTaskManager.getDownloadInfo());

    }

    // Inner class for handling connections with other nodes
    public class NodeConnectionThread extends Thread {
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private final Socket connection;
        private DownloadTaskManager.DownloadThread downloadThread;

        public NodeConnectionThread(Socket connection) {
            this.connection = connection;
        }

        // Adds a download thread to the connection
        public void addDownloadThread(DownloadTaskManager.DownloadThread downloadThread) {
            this.downloadThread = downloadThread;
        }

        // Gets the connection socket
        public Socket getConnection() {
            return connection;
        }

        @Override
        public void run() {
            getStreams();
            processMessages();
        }

        // Sets up the input and output streams for the connection
        private void getStreams() {
            try {
                // Initialize the output stream and flush it
                out = new ObjectOutputStream(connection.getOutputStream());
                out.flush();
                // Initialize the input stream for reading incoming messages
                in = new ObjectInputStream(connection.getInputStream());
                // Sends message to acknowledge the connection
                NewConnectionRequest newConnectionRequest = new NewConnectionRequest(connection.getLocalPort(), connection.getLocalAddress(), connection.getPort(), connection.getInetAddress());
                sendMessageToConnection(newConnectionRequest);
                System.out.println("[streams successfully created]");
            } catch (IOException e) {
                System.err.println(this.getClass() + ": [" + "getStreams()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
            }
        }

        // Sends a message to the other side to indicate the connection should be closed
        public void closeConnection() {
            CloseConnectionRequest message = new CloseConnectionRequest(connection.getLocalPort(), connection.getLocalAddress(), connection.getPort(), connection.getInetAddress());
            sendMessageToConnection(message);
            close();
        }

        // Synchronized method to close the connection and streams
        synchronized private void close() {
            try {
                // Close input, output streams, and the connection socket
                if (in != null) in.close();
                if (out != null) out.close();
                if (connection != null && !connection.isClosed()) connection.close();

            } catch (IOException e) {
                System.err.println(this.getClass() + ": [" + "close()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
            }
            // Interrupt the thread
            System.out.println("[closed connection]");
            this.interrupt();

        }

        // Synchronized method to send a message to the connected node
        public synchronized void sendMessageToConnection(Message message) {
            try {
                out.writeObject(message);
                out.flush();
            } catch (IOException e) {
                System.err.println(this.getClass() + ": [" + "sendMessageToConnection()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
            }
            System.out.println("[sent: " + message + "]");
        }

        // Process incoming messages until a close connection request is sent or the server is stopped
        private void processMessages() {
            while (true) {
                try {
                    Object object = in.readObject();
                    System.out.println("[received: " + object + "]");
                    switch (object) {
                        case FileBlockAnswerMessage message:
                            handleFileBlockAnswerMessage(message);
                            break;
                        case FileBlockRequestMessage message:
                            handleFileBlockRequestMessage(message);
                            break;
                        case FileSearchResult message:
                            handleFileSearchResult(message);
                            break;
                        case WordSearchMessage message:
                            handleWordSearchMessage(message);
                            break;
                        case NewConnectionRequest _:
                            System.out.println("[connection established: " + connection.getLocalAddress() + ":" + connection.getLocalPort() + "]");
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

        // Handles the FileBlockAnswerMessage
        private void handleFileBlockAnswerMessage(FileBlockAnswerMessage message) {
            if(downloadThread == null) {
                System.err.println(this.getClass() + ": [" + "download()" + ": (error: " + "There is no download thread" + ")]");
                return;
            }
            // unlocks download thread and passes the message to the download thread for processing
            downloadThread.messageAnswer(message);
        }

        // Handles the FileBlockRequestMessage
        synchronized private void handleFileBlockRequestMessage(FileBlockRequestMessage input) {
            // Find the requested file in the repository using its hash
            RepositoryFile file = repositoryFiles.stream().filter(f -> f.getHash().equals(input.getHash())).findFirst().orElse(null);
            FileBlockAnswerMessage response;

            // Prepare a file block answer message with the requested file block
            if (file == null) {
                System.err.println(this.getClass() + ": [" + "sendMessageToConnection()" + ": (error: " + "File not found" + ")]");
                response = null;
            } else {
                response = new FileBlockAnswerMessage(input.getReceiverPort(), input.getReceiverAddress(), input.getSenderPort(), input.getSenderAddress(), input.getHash(), file.getFileBlock(input.getOffset(), input.getLength()));
            }

            // Send the file block answer message
            try {
                out.writeObject(response);
                out.flush();
            } catch (IOException e) {
                System.err.println(this.getClass() + ": [" + "sendMessageToConnection()" + ": (exception: " + e.getClass().getName() + ", error: " + e.getMessage() + ")]");
            }

        }

        // Handles the FileSearchResult message
        synchronized private void handleFileSearchResult(FileSearchResult input) {
            // Check if the file already exists in the search results
            // If found, update the node connection list
            for(FileInfo fileInfo : searchInfo) {
                if(fileInfo.fileName().equals(input.getFileName())) {
                    searchInfo.remove(fileInfo);
                    fileInfo.nodeConnectionThreads().add(this);
                    searchInfo.add(fileInfo);
                    return;
                }
            }

            // Add a new file to the search results if not already present
            List<NodeConnectionThread> nodeConnectionThreads = new ArrayList<>();
            nodeConnectionThreads.add(this);
            searchInfo.add(new FileInfo(input.getFileName(), input.getHash(), input.getFileSize(), nodeConnectionThreads));

            // Update the GUI with the new search result
            SwingUtilities.invokeLater(() -> gui.addToFileList(input.getFileName()));
            System.out.println("[obtained results from: " + input.getWordSearchMessage() + "]");
        }

        // Handles the WordSearchMessage
        synchronized private void handleWordSearchMessage(WordSearchMessage input) {
            // Iterate over repository files and check if they contain the keyword
            // If keyword found, send a search result message
            for (RepositoryFile file : repositoryFiles) {
                String fileName = file.getFileName();
                int index = fileName.indexOf(input.getKeyword());
                if (index != -1) {
                    FileSearchResult message = new FileSearchResult(connection.getLocalPort(), connection.getLocalAddress(), connection.getPort(), connection.getInetAddress(), input, file.getHash(), file.getSize(), fileName);
                    sendMessageToConnection(message);
                }
            }

        }

        // Process the search for a keyword by sending a WordSearchMessage
        synchronized private void processSearch(String keyword) {
            WordSearchMessage message = new WordSearchMessage(connection.getLocalPort(), connection.getLocalAddress(), connection.getPort(), connection.getInetAddress(), keyword);
            sendMessageToConnection(message);
        }


    }
}
