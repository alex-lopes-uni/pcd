public class Main {

    public static void main(String[] args) {
        LocalRepositories local = new LocalRepositories("rep");
        GUI gui = new GUI();
        gui.setLista(local.getAllFiles());
        gui.abrirGUI();
    }
}
