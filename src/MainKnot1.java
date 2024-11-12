public class MainKnot1 {

    public static void main(String[] args) {
        startProgram("rep1", 8081);
    }

    public static void startProgram(String rep, int port) {
        AppNode appNode = new AppNode(rep, port);
        GUI gui = new GUI(appNode);
        gui.setFileList(appNode.getRepositoryFiles()); //TODO REMOVE
        gui.openGUI();
        appNode.runServer();
    }
}
