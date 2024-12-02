import app.GUI;
import app.Node;
import utils.RepositoryFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class IscTorrents {

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("please input arguments.");
        }
        int port = Integer.parseInt(args[0]);
        String rep = args[1];

        Node node = new Node(rep, port);
        GUI gui = new GUI(node);
        testFileList(gui, node.getRepositoryFiles()); //TODO REMOVE
        gui.openGUI();
        node.runServer();
    }

    public static void testFileList(GUI gui, List<RepositoryFile> repositoryFiles) {
        List<String> result = new ArrayList<>();
        repositoryFiles.forEach(file -> {
            result.add(file.getFileName());
            System.out.println(file);
        });
        gui.setFileList(result);
    }

}
