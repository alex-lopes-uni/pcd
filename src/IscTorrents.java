import app.GUI;
import app.Node;
import utils.RepositoryFile;
import java.util.ArrayList;
import java.util.List;

public class IscTorrents {

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("please input arguments.");
        }
        int port = Integer.parseInt(args[0]);
        String rep = args[1];

        Node node = new Node(rep, port);
//        testFileList(gui, node.getRepositoryFiles());
        node.runServer();
    }

    // shows all files in node repository to test the JList
    public static void testFileList(GUI gui, List<RepositoryFile> repositoryFiles) {
        List<String> result = new ArrayList<>();
        repositoryFiles.forEach(file -> {
            result.add(file.getFileName());
            System.out.println(file);
        });
        gui.setFileList(result);
    }

}
