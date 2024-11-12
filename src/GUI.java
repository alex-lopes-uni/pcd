import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

public class GUI {
    // strings for the GUI
    private static final String MAIN_WINDOW_TITLE = "IscTorrents";
    private static final String CONNECTION_WINDOW_TITLE = "Ligação";
    private static final String SEARCH_LABEL_TEXT = "Texto a procurar: ";
    private static final String SEARCH_BUTTON_TEXT = "Procurar";
    private static final String DOWNLOAD_BUTTON_TEXT = "Descarregar";
    private static final String CONNECTION_BUTTON_TEXT = "Ligar a no"; //TODO add accent
    private static final String ADDRESS_LABEL_TEXT = "Endereço: ";
    private static final String DEFAULT_ADDRESS_FIELD_VALUE = "localhost";
    private static final String PORT_LABEL_TEXT = "Porta: ";
    private static final String DEFAULT_PORT_FIELD_VALUE = "8081";
    private static final String CANCEL_BUTTON_TEXT = "Cancelar";
    private static final String OK_BUTTON_TEXT = "OK";

    // frames
    private JFrame mainFrame;
    private JFrame connectionFrame;

    // list
    private JList<String> fileList;
    private DefaultListModel<String> listModel;

    // text fields
    private JTextField searchField;
    private JTextField addressField;
    private JTextField portField;

    private final AppNode client;

    public GUI(AppNode client) {
        this.client = client;
        createMainFrame();
    }

    public void createMainFrame() {
        mainFrame = new JFrame(MAIN_WINDOW_TITLE + ": " + client.getPORT());
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setResizable(true);

        // create panels
        JPanel searchPanel = new JPanel(new GridLayout(1, 3));
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 1));

        // create elements
        JLabel searchLabel = new JLabel(SEARCH_LABEL_TEXT);
        searchField = new JTextField();
        JButton searchButton = new JButton(SEARCH_BUTTON_TEXT);
        searchButton.addActionListener(this::searchButtonClicked);

        JButton downloadButton = new JButton(DOWNLOAD_BUTTON_TEXT);
        downloadButton.addActionListener(this::downloadButtonClicked);

        JButton connectionButton = new JButton(CONNECTION_BUTTON_TEXT);
        connectionButton.addActionListener(_ -> createConnectionFrame());

        // create file list
        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);

        // add elements
        mainFrame.add(searchPanel, BorderLayout.NORTH);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        mainFrame.add(buttonsPanel, BorderLayout.EAST);
        buttonsPanel.add(downloadButton);
        buttonsPanel.add(connectionButton);
        mainFrame.add(new JScrollPane(fileList), BorderLayout.CENTER);

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void createConnectionFrame() {
        connectionFrame = new JFrame(CONNECTION_WINDOW_TITLE);
        connectionFrame.setLocationRelativeTo(null);
        connectionFrame.setLayout(new GridLayout(1, 4));

        JLabel addressLabel = new JLabel(ADDRESS_LABEL_TEXT);
        addressField = new JTextField(DEFAULT_ADDRESS_FIELD_VALUE);

        JLabel portLabel = new JLabel(PORT_LABEL_TEXT);
        portField = new JTextField(DEFAULT_PORT_FIELD_VALUE);

        JButton cancelButton = new JButton(CANCEL_BUTTON_TEXT);
        cancelButton.addActionListener(_ -> connectionFrame.dispose());

        JButton okButton = new JButton(OK_BUTTON_TEXT);
        okButton.addActionListener(this::okButtonClicked);

        connectionFrame.add(addressLabel);
        connectionFrame.add(addressField);
        connectionFrame.add(portLabel);
        connectionFrame.add(portField);
        connectionFrame.add(cancelButton);
        connectionFrame.add(okButton);

        connectionFrame.pack();
        connectionFrame.setVisible(true);
    }

    public void openGUI() {
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    //TODO
    public void searchButtonClicked(ActionEvent e) {
        if (searchField.getText() == null || searchField.getText().isEmpty()) {
            String message = "Escreva algo a procurar";
            JOptionPane.showMessageDialog(mainFrame, message, "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        System.out.println(searchField.getText());
        searchField.setText("");
    }

    //TODO
    public void downloadButtonClicked(ActionEvent e) {
        if (fileList.getSelectedValuesList() == null) {
            String message = "Nao selecionou nenhum ficheiro"; //TODO ADD ACCENT
            JOptionPane.showMessageDialog(mainFrame, message, "Erro", JOptionPane.ERROR_MESSAGE);
        } else {
            List<String> selected = getSelectedFiles();
            for (String s : selected) {
                String message = String.format("""
                        %s
                        Download completo.
                        Fornecedor [endereço=/127.0.0.1, porta=8082]:253
                        Fornecedor [endereço=/127.0.0.1, porta=8081]:251
                        Tempo decorrido:8s
                        """, s);
                JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    //TODO
    public void okButtonClicked(ActionEvent e) {
        int port = Integer.parseInt(portField.getText());
        String address = addressField.getText();
        client.connectToNode(port, address);
        connectionFrame.dispose();
    }

    public List<String> getSelectedFiles() {
        return fileList.getSelectedValuesList();
    }

    public void setFileList(List<File> files) {
        listModel.clear();
        files.forEach(file -> listModel.addElement(file.getName()));
    }
}