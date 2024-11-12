import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        ClientServer rep1 = new ClientServer("rep1");
        ClientServer rep2 = new ClientServer("rep2");
        ClientServer rep3 = new ClientServer("rep3");
        List<File> list = rep1.getRepositoryFiles();
        list.addAll(rep2.getRepositoryFiles());
        list.addAll(rep3.getRepositoryFiles());
        GUI gui = new GUI();
        gui.setLista(list);
        gui.abrirGUI();
    }
}
