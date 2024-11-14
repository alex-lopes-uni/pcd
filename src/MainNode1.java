public class MainNode1 {

    public static void main(String[] args) {
        startProgram(8081, "rep1");
    }

    public static void startProgram(int port, String rep) {
        AppNode appNode = new AppNode(rep, port);
        GUI gui = new GUI(appNode);
        gui.setFileList(appNode.getRepositoryFiles()); //TODO REMOVE
        gui.openGUI();
        appNode.runServer();
    }
}
