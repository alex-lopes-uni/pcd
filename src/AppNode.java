import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;

public class AppNode {
    private final String PATH;
    private List<File> repositoryFiles;
    private final int PORT;
    private ServerSocket ss;
    private Socket connection;
    private volatile boolean running = true;

    public AppNode(String folderName, int PORT) {
        this.PORT = PORT;
        PATH = System.getProperty("user.dir") + "/"+ folderName;
        setRepositoryFiles();
    }

    public List<String> getRepositoryFiles() {
        List<String> result = new ArrayList<>();
        repositoryFiles.forEach(file -> result.add(file.getName()));
        return result;
    }

    public void setRepositoryFiles() {
        File dir = new File(PATH);
        List<File> result = new ArrayList<>();

        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Invalid main directory path");
            return;
        }

        File[] temp = dir.listFiles();

        if (temp != null) {
            for (File file : temp) {
                if (!file.isDirectory()) {
                    result.add(file);
                }
            }
        } else {
            System.out.println("No files found");
        }
        repositoryFiles = result;
    }

    public int getPort(){
        return PORT;
    }

    public void runServer() {
        try {
            ss = new ServerSocket(PORT);
            System.out.println("no criado no porto " + PORT);
            waitForConnections();
        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        } finally {
            try {
                stopServer();
            } catch (IOException e) {
                System.err.println("An error occurred: " + e.getMessage());
            }
        }
    }

    private void waitForConnections() throws IOException {
        while(running) {
            connection =  ss.accept();
            DealWithNode handler = new DealWithNode(connection);
            handler.start();
			System.out.println("Connection to " + connection.getInetAddress().getHostName() + ":" + connection.getPort() + " is established!");
        }
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

    public void stopServer() throws IOException {
        running = false;
        if (connection != null)
            connection.close();
        if(ss != null)
            ss.close();
        System.out.println("Server stopped");
    }


    public static class DealWithNode extends Thread {
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private Socket connection;

        public DealWithNode(Socket connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
            } catch (IOException e) {
                System.err.println("An error occurred: " + e.getMessage());
            }
        }

    }
}
