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
        node.runServer();
    }

}
