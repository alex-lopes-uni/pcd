import app.GUI;
import app.Node;

public class IscTorrents {
    public static void startProgram(int port, String rep) {
        Node node = new Node(rep, port);
        GUI gui = new GUI(node);
        gui.setFileList(node.getRepositoryFiles()); //TODO REMOVE
        gui.openGUI();
        node.runServer();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("please input arguments.");
        }
        int port = Integer.parseInt(args[0]);
        String rep = args[1];
        startProgram(port, rep);
    }

}
