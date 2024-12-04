package app;

import utils.RepositoryFile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class GUI {
    // strings for the app.GUI
    private static final String MAIN_WINDOW_TITLE = "IscTorrents";
    private static final String CONNECTION_WINDOW_TITLE = "Ligação";
    private static final String SEARCH_LABEL_TEXT = "Texto a procurar: ";
    private static final String SEARCH_BUTTON_TEXT = "Procurar";
    private static final String DOWNLOAD_BUTTON_TEXT = "Descarregar";
    private static final String CONNECTION_BUTTON_TEXT = "Ligar a nó";
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

    private final Node node;

    public GUI(Node node) {
        this.node = node;
        createMainFrame();
    }

    public void createMainFrame() {
        mainFrame = new JFrame(MAIN_WINDOW_TITLE + ": " + node.getPort());
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

    // creates the frame that makes the connection to other nodes
    public void createConnectionFrame() {
        connectionFrame = new JFrame(CONNECTION_WINDOW_TITLE);
        connectionFrame.setLocationRelativeTo(mainFrame);
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

    // action to be done when search button is clicked
    // starts the search process
    public void searchButtonClicked(ActionEvent e) {
        if (searchField.getText() == null || searchField.getText().isEmpty()) {
            String message = "Escreva algo a procurar";
            JOptionPane.showMessageDialog(mainFrame, message, "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<RepositoryFile> result = node.search(searchField.getText());
        if (result.isEmpty()) {
            String message = "Não foram encontrados resultados";
            JOptionPane.showMessageDialog(mainFrame, message, "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        List<String> list = new ArrayList<>();
        result.forEach(file -> list.add(file.getFileName()));
        setFileList(list);
    }

    // action to be done when download button is clicked
    // starts the download process for all selected files
    public void downloadButtonClicked(ActionEvent e) {
        if (fileList.getSelectedValuesList() == null || fileList.getSelectedValuesList().isEmpty()) {
            String message = "Não selecionou nenhum ficheiro";
            JOptionPane.showMessageDialog(mainFrame, message, "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<String> selected = getSelectedFiles();
        for (String s : selected) {
            List<String> temp = new ArrayList<>();
            node.getRepositoryFiles().forEach(file -> temp.add(file.getFileName()));

            if (temp.contains(s)) {
                String message = "Ficheiro " + s + " já existe localmente";
                JOptionPane.showMessageDialog(mainFrame, message, "Erro", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            List<String> result = node.download(s); // [file name, time, [endereço=127.0.0.1, porta=8082]:253 ....]
            if (result.isEmpty()) {
                String message = "Ocorreu um erro ao fazer download de " + s;
                JOptionPane.showMessageDialog(mainFrame, message, "Erro", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            String fileName = result.get(0);
            String time = result.get(1);
            StringBuilder message = new StringBuilder(fileName + "\n" + "Download completo\n");
            for (int i = 2; i < result.size(); i++) {
                message.append("Fornecedor ").append(result.get(i)).append("\n");
            }
            message.append("Tempo decorrido: ").append(time).append("s\n");
            JOptionPane.showMessageDialog(mainFrame, message.toString(), "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // action to be done when ok button in the connection frame is clicked
    // creates the connection between the node and the input node
    public void okButtonClicked(ActionEvent e) {
        int port = Integer.parseInt(portField.getText());
        String address = addressField.getText();
        if (port == node.getPort()) {
            String message = "Não se pode conectar a si próprio";
            JOptionPane.showMessageDialog(mainFrame, message, "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String result = node.connectToNode(address, port);
        if (result.equals("success")) {
            String message = "Sucesso";
            JOptionPane.showMessageDialog(mainFrame, message, "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String message = "Erro: " + result;
            JOptionPane.showMessageDialog(mainFrame, message, "Erro", JOptionPane.ERROR_MESSAGE);
        }
        connectionFrame.dispose();
    }

    public List<String> getSelectedFiles() {
        return fileList.getSelectedValuesList();
    }

    public void setFileList(List<String> list) {
        listModel.clear();
        listModel.addAll(list);
    }

}