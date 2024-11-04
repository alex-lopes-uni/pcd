import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

public class GUI {
    // strings para a GUI
    private static final String TITULO_JANELA_PRINCIPAL = "IscTorrents";
    private static final String TITULO_JANELA_CONEXAO = "Ligação";
    private static final String TEXTO_LABEL_PESQUISA = "Texto a procurar: ";
    private static final String TEXTO_BOTAO_PESQUISA = "Procurar";
    private static final String TEXTO_BOTAO_DOWNLOAD = "Descarregar";
    private static final String TEXTO_BOTAO_CONEXAO = "Ligar a no"; //TODO add accent
    private static final String TEXTO_LABEL_ENDERECO = "Endereço: ";
    private static final String VALOR_PADRAO_CAMPO_ENDERECO = "localhost";
    private static final String TEXTO_LABEL_PORTA = "Porta: ";
    private static final String VALOR_PADRAO_CAMPO_PORTA = "8081";
    private static final String TEXTO_BOTAO_CANCELAR = "Cancelar";
    private static final String TEXTO_BOTAO_OK = "OK";

    //frames
    private JFrame frame;
    private JFrame frameConexao;

    // lista
    // para poder ser atualizada precisa de um DefaultListModel a encapsular
    private JList<String> lista;
    private DefaultListModel<String> listaModel;

    // campos de texto
    // estao aqui para posteriormente podermos chamar as variáveis
    private JTextField pesquisa;
    private JTextField endereco;
    private JTextField porta;

    // como o main em boas práticas deve estar noutra classe,
    // quando se cria uma GUI ele executa a função criarFrame que é a função main que fizeste
    public GUI() {
        criarFrame();
    }

    // cria a frame de ligar a nos
    // e chamada quando se carrega no botão de conexão
    public void criarFrameConexao() {
        frameConexao = new JFrame(TITULO_JANELA_CONEXAO);
        frameConexao.setLocationRelativeTo(null); // para aparecer no centro do ecra em vez de no canto
        frameConexao.setLayout(new GridLayout(1, 4));
        
        JLabel labelEndereco = new JLabel(TEXTO_LABEL_ENDERECO);
        endereco = new JTextField(VALOR_PADRAO_CAMPO_ENDERECO);

        JLabel labelPorta = new JLabel(TEXTO_LABEL_PORTA);
        porta = new JTextField(VALOR_PADRAO_CAMPO_PORTA);

        JButton botaoCancelar = new JButton(TEXTO_BOTAO_CANCELAR);
        botaoCancelar.addActionListener(_ -> frameConexao.dispose()); // quando se carrega no cancelar ele fecha a pagina
        
        JButton botaoOk = new JButton(TEXTO_BOTAO_OK);
        botaoOk.addActionListener(this::botaoOkClicado); // executa a funcao botãoOkClicado

        frameConexao.add(labelEndereco);
        frameConexao.add(endereco);
        frameConexao.add(labelPorta);
        frameConexao.add(porta);
        frameConexao.add(botaoCancelar);
        frameConexao.add(botaoOk);
        
        frameConexao.pack();
        frameConexao.setVisible(true);
    }
    
    public void criarFrame() {
        frame = new JFrame(TITULO_JANELA_PRINCIPAL);
        frame.setLocationRelativeTo(null); // para aparecer no centro do ecra em vez de no canto
        frame.setLayout(new BorderLayout());
        // nao e necessario usar o tamanho visto que se usa a função pack()
        frame.setResizable(true);

        //criar panels
        JPanel painelPesquisa= new JPanel(new GridLayout(1, 3));
        JPanel painelBotoes= new JPanel(new GridLayout(2,1));

        //criar elementos
        JLabel labelPesquisa = new JLabel(TEXTO_LABEL_PESQUISA);
        pesquisa = new JTextField();
        JButton botaoPesquisa = new JButton(TEXTO_BOTAO_PESQUISA);
        botaoPesquisa.addActionListener(this::botaoPesquisaClicado); // executa a funcao botãoPesquisaClicado

        JButton botaoDownload = new JButton(TEXTO_BOTAO_DOWNLOAD);
        botaoDownload.addActionListener(this::botaoDownloadClicado); // executa a funcao botãoDownloadClicado

        JButton botaoConexao = new JButton(TEXTO_BOTAO_CONEXAO);
        botaoConexao.addActionListener(_ -> criarFrameConexao()); // cria a frame conexão

        // criação da lista
        listaModel = new DefaultListModel<>();
        lista = new JList<>(listaModel);

        //adicionar elementos
        frame.add(painelPesquisa, BorderLayout.NORTH);
        painelPesquisa.add(labelPesquisa);
        painelPesquisa.add(pesquisa);
        painelPesquisa.add(botaoPesquisa);
        frame.add(painelBotoes, BorderLayout.EAST);
        painelBotoes.add(botaoDownload);
        painelBotoes.add(botaoConexao);
        frame.add(new JScrollPane(lista), BorderLayout.CENTER);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // nao se pode abrir ja, pois primeiro queremos atualizar a lista com os dados
    }

    // abre a GUI
    public void abrirGUI() {
        frame.pack();
        frame.setVisible(true);
    }

    //TODO
    public void botaoPesquisaClicado(ActionEvent e) {
        if (pesquisa.getText() == null || pesquisa.getText().isEmpty()) {
            String message = "Escreva algo a procurar";
            JOptionPane.showMessageDialog(frame, message, "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        System.out.println(pesquisa.getText());
        pesquisa.setText("");
    }

    //TODO
    public void botaoDownloadClicado(ActionEvent e) {
        if (lista.getSelectedValue() == null) {
            String message = "Nao selecionou nenhum ficheiro"; //TODO ADD ACCENT
            JOptionPane.showMessageDialog(frame, message, "Erro", JOptionPane.ERROR_MESSAGE);
        } else {
            String message = String.format("""
                %s
                Download completo.
                Fornecedor [endereço=/127.0.0.1, porta=8082]:253
                Fornecedor [endereço=/127.0.0.1, porta=8081]:251
                Tempo decorrido:8s
                """, lista.getSelectedValue());
            JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    //TODO
    public void botaoOkClicado(ActionEvent e) {
        System.out.println("Endereço: " + endereco.getText() + " Porta: " + porta.getText());
        frameConexao.dispose();
    }

    public JList<String> getLista() {
        return lista;
    }

    public String getFicheiroEscolhido() {
        return lista.getSelectedValue();
    }

    // dois ‘sets’ lista, pois pode ser atualizada de duas maneiras, com um array de ‘strings’ ou uma lista de ficheiros
    public void setLista(String[] items) {
        listaModel.clear();
        for (String item : items) {
            listaModel.addElement(item);
        }
    }

    public void setLista(List<File> files) {
        listaModel.clear();
        files.forEach(file -> listaModel.addElement(file.getName()));
    }
    
}
