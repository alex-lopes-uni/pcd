import app.Node;

public class IscTorrents {

    public static void main(String[] args) {
        //  Making sure it sends 2 arguments
        if (args.length != 2) {
            throw new IllegalArgumentException("please input arguments.");
        }
        // Creates node based on the port and the repository
        int port = Integer.parseInt(args[0]);
        String rep = args[1];
        Node node = new Node(rep, port);
        // Starts running the node
        node.runServer();
    }

}
