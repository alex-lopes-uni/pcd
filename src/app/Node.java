package app;

import messages.*;
import utils.RepositoryFile;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Node {
    private final String PATH;
    private List<RepositoryFile> repositoryFiles;
    private final int PORT;
    private ServerSocket ss;
    private Socket connection;
    private volatile boolean running = true;
    private final List<NodeConnectionThread> threads = new ArrayList<>();

    public Node(String folderName, int PORT) {
        this.PORT = PORT;
        PATH = System.getProperty("user.dir") + "/" + folderName;
        setRepositoryFiles();
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
            System.out.println("Connection to " + connectionAddress + ":" + connectionPort + " is established!");
            return "success";
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            return e.getMessage();
        }

    }

    synchronized public void sendMessageToAllConnections(Message message) {
        for (NodeConnectionThread thread : threads) {
            thread.sendMessageToConnection(message);
        }
    }

    synchronized private NodeConnectionThread getConnection(String connectionAddress, int connectionPort) {
        for (NodeConnectionThread thread : threads) {
            if (thread.connection.getInetAddress().getHostName().equals(connectionAddress) && thread.connection.getPort() == connectionPort) {
                return thread;
            }
        }
        return null;
    }

    public List<RepositoryFile> search(String keyword) {
        List<RepositoryFile> result = new ArrayList<>();
        for (NodeConnectionThread thread : threads) {
            List<RepositoryFile> searchResult = thread.processSearch(keyword);
            if (searchResult != null) {
                result.addAll(searchResult);
            }
        }
        return result;
    }

    // TODO DOWNLOAD FUNCTION
    public List<String> download(String fileName) {
        List<String> result = new ArrayList<>();
        result.add(fileName);
        result.add("8");
        result.add("[endereço=127.0.0.1, porta=8082]:253");
        result.add("[endereço=127.0.0.1, porta=8082]:253");
        result.add("[endereço=127.0.0.1, porta=8082]:253");
        return result;
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
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                System.out.println("Streams are ready");
            } catch (IOException e) {
                System.err.println("An error occurred: " + e.getMessage());
            }
        }

        private void processMessages() {
            while (true) {
                try {
                    Object object = in.readObject();
                    switch (object) {
                        case FileBlockAnswerMessage message:
                            handleFileBlockAnswerMessage(message);
                            System.out.println("[received message: " + message + "]");
                            break;
                        case FileBlockRequestMessage message:
                            handleFileBlockRequestMessage(message);
                            System.out.println("[received message: " + message + "]");
                            break;
                        case FileSearchResult message:
                            handleFileSearchResult(message);
                            System.out.println("[received message: " + message + "]");
                            break;
                        case WordSearchMessage message:
                            handleWordSearchMessage(message);
                            System.out.println("[received message: " + message + "]");
                            break;
                        case null:
                        default:
                            System.out.println("[received null message]");
                            return;
                    }
                } catch (ClassNotFoundException | IOException e) {
                    System.err.println("An error occurred: " + e.getMessage());
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

        }

        private void closeConnection() {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (connection != null) {
                    synchronized (threads) {
                        threads.remove(this);
                    }
                    connection.close();
                }

            } catch (IOException e) {
                System.err.println("An error occurred: " + e.getMessage());
            }
        }

        // TODO
        private void handleFileBlockAnswerMessage(FileBlockAnswerMessage message) {

        }

        // TODO
        private void handleFileBlockRequestMessage(FileBlockRequestMessage message) {

        }

        // TODO
        private void handleFileSearchResult(FileSearchResult message) {

        }

        // TODO
        private void handleWordSearchMessage(WordSearchMessage message) {

        }

        // TODO SEARCH FUNCTION
        private List<RepositoryFile> processSearch(String keyword) {
            return null;
        }

        // TODO
        private List<String> processDownload(RepositoryFile file) {
            return null;
        }
    }
}
