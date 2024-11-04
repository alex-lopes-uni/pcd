public class Main {

    public static void main(String[] args) {
        LocalRepositories local = new LocalRepositories("/", "rep");
        GUI gui = new GUI();
        gui.setList(new String[]{"ola", "teste"});
//        gui.setList(local.getAllFiles());
        gui.openGUI();
    }
}
