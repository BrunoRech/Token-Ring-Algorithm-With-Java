package server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Interface de Usuário para o servidor.
 * @author Jeferson Penz
 */
public class ServerInterface extends JFrame implements ServerObserver{
    
    private static final long serialVersionUID = 1L;
    private JLabel serverIp;
    private JLabel serverPort;
    private JTextArea textAreaDados;
    private ServerController controller;
    
    /**
     * Cria uma nova interface para o servidor.
     */
    public ServerInterface() {
        super("Algoritmo TokenRing - Server");
        this.initProperties();
        this.initComponents();
    }
    
    /**
     * Inicia o servidor.
     */
    public void beginServer(){
        this.controller.openConnection();
    }

    /**
     * Inicia as propriedades da tela.
     */
    private void initProperties() {
        this.controller = ServerController.getInstance();
        this.controller.addObserver(this);
        this.setSize(800, 600);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout(0, 10));
        Container pane = this.getContentPane();
        pane.setBackground(Color.white);
    }

    /**
     * Inicia os componentes da tela.
     */
    private void initComponents() {
        this.addTopPanel();
        this.addMainPanel();
    }

    /**
     * Adiciona o painel superior
     */
    private void addTopPanel() {
        this.serverIp = new JLabel("Aguardando informações");
        this.serverIp.setFont(new Font("Arial", Font.BOLD, 16));
        this.serverPort = new JLabel("Aguardando informações");
        this.serverPort.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel tokenStatus = new JLabel();
        tokenStatus.setFont(new Font("Arial", Font.BOLD, 25));
        if(ServerController.USA_TOKEN){
            tokenStatus.setText("O servidor está utilizando token...");
            tokenStatus.setForeground(Color.GREEN);
        }
        else {
            tokenStatus.setText("O servidor não está utilizando token...");
            tokenStatus.setForeground(Color.RED);
        }

        JPanel topPanel = new JPanel(new BorderLayout(0, 0));
        topPanel.setBackground(new Color(220, 245, 255));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.add(topPanel, BorderLayout.NORTH);

        JPanel portDataPanel = new JPanel();
        portDataPanel.setLayout(new BoxLayout(portDataPanel, BoxLayout.Y_AXIS));
        portDataPanel.setBackground(new Color(0, 0, 0, 0));
        portDataPanel.setOpaque(false);
        portDataPanel.add(this.serverIp);
        portDataPanel.add(this.serverPort);
        topPanel.add(portDataPanel, BorderLayout.WEST);

        JPanel tokenStatusPanel = new JPanel();
        tokenStatusPanel.setLayout(new FlowLayout(FlowLayout.TRAILING, 0, 0));
        tokenStatusPanel.setBackground(Color.WHITE);
        tokenStatusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 5));
        tokenStatusPanel.add(tokenStatus);
        topPanel.add(tokenStatusPanel, BorderLayout.EAST);
    }

    /**
     * Adiciona o painel principal.
     */
    private void addMainPanel() {
        this.textAreaDados = new JTextArea("Informações do Servidor...\n");
        this.textAreaDados.setFont(new Font("Arial", Font.PLAIN, 15));
        this.textAreaDados.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.textAreaDados.setEnabled(false);
        this.textAreaDados.setForeground(Color.BLACK);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        this.add(mainPanel);

        JScrollPane pane = new JScrollPane(this.textAreaDados);
        pane.setBorder(null);
        mainPanel.add(pane, BorderLayout.CENTER);
    }
    
    @Override
    public void setServerIpData(String ip) {
        this.serverIp.setText("Ouvindo no Ip: " + ip);
    }
    @Override
    public void setServerPortData(String port) {
        this.serverPort.setText("na Porta: " + port);
    }

    @Override
    public void onMessageReceived(String message) {
        this.textAreaDados.setText(this.textAreaDados.getText() + "\n" + message);
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
        }
        ServerInterface UI = new ServerInterface();
        UI.setVisible(true);
        UI.beginServer();
    }
}
