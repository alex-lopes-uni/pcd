import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

public class GUI {
    private static final String MAIN_FRAME_TITLE = "IscTorrents";
    private static final String CONNECTION_FRAME_TITLE = "Ligação"; //TODO add accent
    private static final String SEARCH_LABEL_TEXT = "Texto a procurar: ";
    private static final String SEARCH_BUTTON_TEXT = "Procurar";
    private static final String DOWNLOAD_BUTTON_TEXT = "Descarregar";
    private static final String CONNECTION_BUTTON_TEXT = "Ligar a no"; //TODO add accent
    private static final String ADDRESS_LABEL_TEXT = "Endereço: ";
    private static final String ADDRESS_TEXT_FIELD_DEFAULT_VALUE = "localhost";
    private static final String PORT_LABEL_TEXT = "Porta: ";
    private static final String PORT_TEXT_FIELD_DEFAULT_VALUE = "8081";
    private static final String CANCEL_BUTTON_TEXT = "Cancelar";
    private static final String OK_BUTTON_TEXT = "OK";

    private JFrame mainFrame;
    private JFrame connectionFrame;

    private JList<String> list;
    private DefaultListModel<String> listModel;
    private JTextField search;
    private JTextField address;
    private JTextField port;

    public GUI() {
        createMainFrame();
    }

    public void createConnectionFrame() {
        connectionFrame = new JFrame(CONNECTION_FRAME_TITLE);
        connectionFrame.setLocationRelativeTo(null);
        connectionFrame.setLayout(new GridLayout(1, 4));
        
        JLabel addressLabel = new JLabel(ADDRESS_LABEL_TEXT);
        address = new JTextField(ADDRESS_TEXT_FIELD_DEFAULT_VALUE);

        JLabel portLabel = new JLabel(PORT_LABEL_TEXT);
        port = new JTextField(PORT_TEXT_FIELD_DEFAULT_VALUE);

        JButton cancelButton = new JButton(CANCEL_BUTTON_TEXT);
        cancelButton.addActionListener(_ -> connectionFrame.dispose());
        
        JButton okButton = new JButton(OK_BUTTON_TEXT);
        okButton.addActionListener(this::connectionOkButtonClicked);

        connectionFrame.add(addressLabel);
        connectionFrame.add(address);
        connectionFrame.add(portLabel);
        connectionFrame.add(port);
        connectionFrame.add(cancelButton);
        connectionFrame.add(okButton);
        
        connectionFrame.pack();
        connectionFrame.setVisible(true);
    }
    
    public void createMainFrame() {
        mainFrame = new JFrame(MAIN_FRAME_TITLE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setSize(300, 200);
        mainFrame.setResizable(true);

        //criar panels
        JPanel searchPanel= new JPanel(new GridLayout(1, 3));
        JPanel buttonsPanel= new JPanel(new GridLayout(2,1));

        //criar elementos
        JLabel searchLabel = new JLabel(SEARCH_LABEL_TEXT);
        search = new JTextField();
        JButton searchButton = new JButton(SEARCH_BUTTON_TEXT);
        searchButton.addActionListener(this::searchButtonClicked);

        JButton downloadButton = new JButton(DOWNLOAD_BUTTON_TEXT);
        downloadButton.addActionListener(this::downloadButtonClicked);

        JButton connectionButton = new JButton(CONNECTION_BUTTON_TEXT);
        connectionButton.addActionListener(_ -> createConnectionFrame());

        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);

        //adicionar elementos
        mainFrame.add(searchPanel, BorderLayout.NORTH);
        searchPanel.add(searchLabel);
        searchPanel.add(search);
        searchPanel.add(searchButton);
        mainFrame.add(buttonsPanel, BorderLayout.EAST);
        buttonsPanel.add(downloadButton);
        buttonsPanel.add(connectionButton);
        mainFrame.add(new JScrollPane(list), BorderLayout.CENTER);
        
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void openGUI() {
        mainFrame.pack();
        mainFrame.setVisible(true);
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
        JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
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
