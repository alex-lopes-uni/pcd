import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalRepositories {
    private final String PATH = System.getProperty("user.dir");
    private final String REPOSITORY_STRING_START;
    private Repository[] repositories;

    public LocalRepositories(String repositoryStringStart) {
        REPOSITORY_STRING_START = repositoryStringStart;
        setRepositories();
    }

    public Repository[] getRepositories() {
        return repositories;
    }

    public Repository getRepository(int index) {
        if (index < 0 || index >= repositories.length) return null;
        return repositories[index];
    }

    public void setRepositories() {
        File projectDirectory = new File(PATH);
        File[] result;

        if (!projectDirectory.exists() || !projectDirectory.isDirectory()) {
            System.out.println("Invalid main directory path");
            return;
        }

        result = projectDirectory.listFiles(f -> f.isDirectory() && f.getName().startsWith(REPOSITORY_STRING_START));

        if (result == null || result.length == 0) {
            System.out.println("No local repositories found or invalid path");
            return;
        }

        repositories = new Repository[result.length];

        for (int i = 0; i < result.length; i++) {
            repositories[i] = new Repository(result[i]);
        }
    }

    public List<File> getAllFiles() {
        List<File> result = new ArrayList<>();
        for (Repository repo : repositories) {
            result.addAll(repo.getFiles());
        }
        return result;
    }

}
