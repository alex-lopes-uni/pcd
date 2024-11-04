import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Repository {
    private final File repository;
    private List<File> files;

    public Repository(File repository) {
        this.repository = repository;
        setFiles(repository.getPath());
    }

    public File getRepository() {
        return repository;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(String path) {
        List<File> result = new ArrayList<>();
        File[] temp = new File(path).listFiles();
        if (temp != null) {
            for (File file : temp) {
                if (!file.isDirectory()) {
                    result.add(file);
                }
            }
        } else {
            System.out.println("No files found");
        }
        files = result;
    }
}
