package old;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

public class AlexGUI {
    private static final String MAIN_FRAME_TITLE = "IscTorrents";
    private static final String CONNECTION_FRAME_TITLE = "Ligar a no"; //TODO add accent
    private static final String SEARCH_LABEL_TEXT = "Texto a procurar: ";
    private static final String SEARCH_BUTTON_TEXT = "Procurar";
    private static final String DOWNLOAD_BUTTON_TEXT = "Descarregar";
    private static final String CONNECTION_BUTTON_TEXT = "Ligar a no"; //TODO add accent
    private static final String ADDRESS_LABEL_TEXT = "Endereço: ";
    private static final String ADDRESS_TEXT_FIELD_DEFAULT_VALUE = "localhost";
    private static final String PORT_LABEL_TEXT = "Porta: ";
    private static final String PORT_TEXT_FIELD_DEFAULT_VALUE = "8081";
    private static final String CANCEL_BUTTON_TEXT = "Cancelar";
    private static final String OK_BUTTON_TEXT = "Ok";
    private static final int SEARCH_TEXT_FIELD_SIZE = 20;

    private JFrame masterFrame;
    private JFrame connectionFrame;

    private JList<String> list;
    private DefaultListModel<String> listModel;
    private JTextField search;
    private JTextField address;
    private JTextField port;

    public AlexGUI() {
        createMasterFrame();
        createConnectionFrame();
    }

    public void startGUI() { openFrame(masterFrame); }

    public JFrame createFrame(String title) {
        JFrame frame = new JFrame(title);
        frame.setLocationRelativeTo(null);
        return frame;
    }

    public void openFrame(JFrame frame) {
        frame.pack();
        frame.setVisible(true);
    }

    public void createMasterFrame() {
        masterFrame = createFrame(MAIN_FRAME_TITLE);
        masterFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        masterFrame.setLayout(new BorderLayout());

        //panel creation
        //master frame's panels
        JPanel searchBar = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));

        //search panel
        //master frame search bar
        JLabel searchLabel = new JLabel(SEARCH_LABEL_TEXT);
        searchBar.add(searchLabel, BorderLayout.WEST);

        search = new JTextField(SEARCH_TEXT_FIELD_SIZE);
        searchBar.add(search, BorderLayout.CENTER);

        JButton searchButton = new JButton(SEARCH_BUTTON_TEXT);
        searchButton.addActionListener(this::searchButtonClicked);
        searchBar.add(searchButton, BorderLayout.EAST);

        masterFrame.add(searchBar, BorderLayout.NORTH);

        //button panel
        JButton downloadButton = new JButton(DOWNLOAD_BUTTON_TEXT);
        downloadButton.addActionListener(this::downloadButtonClicked);
        buttonPanel.add(downloadButton);

        JButton connectionButton = new JButton(CONNECTION_BUTTON_TEXT);
        connectionButton.addActionListener(e -> openFrame(connectionFrame));
        buttonPanel.add(connectionButton);

        masterFrame.add(buttonPanel, BorderLayout.EAST);

        // file list
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        masterFrame.add(new JScrollPane(list), BorderLayout.CENTER);
    }

    public void createConnectionFrame() {
        connectionFrame = createFrame(CONNECTION_FRAME_TITLE);
        connectionFrame.setLayout(new FlowLayout());

        // connection address
        JLabel addressLabel = new JLabel(ADDRESS_LABEL_TEXT);
        connectionFrame.add(addressLabel);

        address = new JTextField(ADDRESS_TEXT_FIELD_DEFAULT_VALUE);
        connectionFrame.add(address);

        // connection port
        JLabel portLabel = new JLabel(PORT_LABEL_TEXT);
        connectionFrame.add(portLabel);

        port = new JTextField(PORT_TEXT_FIELD_DEFAULT_VALUE);
        connectionFrame.add(port);

        // connection frame buttons
        JButton cancelButton = new JButton(CANCEL_BUTTON_TEXT);
        cancelButton.addActionListener(e -> connectionFrame.dispose());
        connectionFrame.add(cancelButton);

        JButton okButton = new JButton(OK_BUTTON_TEXT);
        okButton.addActionListener(this::connectionOkButtonClicked);
        connectionFrame.add(okButton);
    }

    public void finishedDownloadDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    //TODO
    public void searchButtonClicked(ActionEvent e) { System.out.println(search.getText()); }

    //TODO
    public void downloadButtonClicked(ActionEvent e) {
        String message = """
                Download completo.
                Fornecedor [endereço=/127.0.0.1, porta=8082]:253
                Fornecedor [endereço=/127.0.0.1, porta=8081]:251
                Tempo decorrido:8s""";
        finishedDownloadDialog(message);
    }

    //TODO
    public void connectionOkButtonClicked(ActionEvent e) {
        System.out.println("Endereço: " + address.getText() + " Porta: " + port.getText());
        connectionFrame.dispose();
    }

    public JList<String> getList() {
        return list;
    }

    public void setList(String[] items) {
        listModel.clear();
        for (String item : items) {
            listModel.addElement(item);
        }
    }

    public void setList(List<File> files) {
        listModel.clear();
        files.forEach(file -> listModel.addElement(file.getName()));
    }
}
