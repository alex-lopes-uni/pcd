import java.io.File;
import java.util.*;

public class ClientServer {
    private final String PATH;
    private List<File> repositoryFiles;

    public ClientServer(String folderName) {
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

}
