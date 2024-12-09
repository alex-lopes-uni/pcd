package app;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import static utils.Constants.*;

public class GUI {
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
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                node.closeAllConnections();
                System.exit(0);
            }
        });

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
        setFileList(new ArrayList<>());
        node.search(searchField.getText());
    }

    // action to be done when download button is clicked
    // starts the download process for all selected files
    public void downloadButtonClicked(ActionEvent e) {

        if (getSelectedFiles() == null || getSelectedFiles().isEmpty()) {
            String message = "Não selecionou nenhum ficheiro";
            JOptionPane.showMessageDialog(mainFrame, message, "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> selected = getSelectedFiles();
        List<String> temp = new ArrayList<>();
        node.getRepositoryFiles().forEach(file -> temp.add(file.getFileName()));
        for (String s : selected) {

            if (temp.contains(s)) {
                String message = "Ficheiro " + s + " já existe localmente";
                JOptionPane.showMessageDialog(mainFrame, message, "Erro", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            node.download(s);

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

    // shows the download info after the download is completed
    public void showDownloadInfo(String fileName, long time, List<String> downloadInfo) {

        if (downloadInfo == null || downloadInfo.isEmpty()) {
            String message = "Ocorreu um erro ao fazer download de " + fileName;
            JOptionPane.showMessageDialog(mainFrame, message, "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        StringBuilder message = new StringBuilder(fileName + "\n" + "Download completo\n");
        for (int i = 2; i < downloadInfo.size(); i++) {
            message.append("Fornecedor: ").append(downloadInfo.get(i)).append("\n");
        }
        message.append("Tempo decorrido: ").append(time).append("s\n");
        JOptionPane.showMessageDialog(mainFrame, message.toString(), "Info", JOptionPane.INFORMATION_MESSAGE);

    }

    public List<String> getSelectedFiles() {
        return fileList.getSelectedValuesList();
    }

    public void setFileList(List<String> list) {
        listModel.clear();
        listModel.addAll(list);
    }

    public synchronized void addToFileList(String fileName) {
        listModel.addElement(fileName);
    }

}