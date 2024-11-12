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

    public AppNode(String folderName, int PORT) {
        this.PORT = PORT;
        PATH = System.getProperty("user.dir") + "/"+ folderName;
        System.out.println("PATH: " + PATH);
        setRepositoryFiles();
    }

    public List<File> getRepositoryFiles() {
        return repositoryFiles;
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

    public int getPORT(){
        return PORT;
    }

    public void runServer() {
        try {
            ss = new ServerSocket(PORT);
            System.out.println("no criado no porto " + PORT);
            waitForConnections();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(ss != null)
                    ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void waitForConnections() throws IOException {
        while(true) {
            Socket connection =  ss.accept();
            DealWithNode handler = new DealWithNode(connection);
            handler.start();
//			System.out.println("Connection to " + connection.getInetAddress().getHostName() + "is established!");
            System.out.println("Connection is established!");
        }
    }

    public void connectToNode(int connectionPort, String connectionAddress) {
        try {
            connection = new Socket(InetAddress.getByName(connectionAddress), connectionPort);
            System.out.println("Connection is established!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static class DealWithNode extends Thread {
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private Socket connection;

        public DealWithNode(Socket connection) throws IOException {
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
