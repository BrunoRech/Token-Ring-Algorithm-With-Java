package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Implementação padrão da interface do Usuário usando do Swing.
 * @author Jeferson Penz
 */
public class UserInterface extends JFrame implements IUserInterface{
    
    private static final String PLACEHOLDER_TEXTO = "Digite Aqui.";
    
    private JLabel    nextServerData;
    private JLabel    currentServerData;
    private JLabel    tokenStatus;
    private JTextArea text;
    
    /**
     * @todo Remover
     * @example utilização.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {}
        UserInterface UI = new UserInterface();
        UI.setVisible(true);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(UserInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
        UI.setNextServerData("127.0.0.0", "5051");
        UI.setCurrentServerData("5050");
        boolean tst = true;
        while(true){
            UI.setTokenReceived(tst);
            tst = !tst;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(UserInterface.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Cria uma nova interface de Usuário.
     */
    public UserInterface(){
        super("Algoritmo TokenRing");
        this.initProperties();
        this.initComponents();
    }
    
    /**
     * Inicia as propriedades da tela.
     */
    private void initProperties(){
        this.setSize(600, 400);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout(0, 10));
        Container pane = this.getContentPane();
        pane.setBackground(Color.white);
    }
    
    /**
     * Inicia os componentes da tela.
     */
    private void initComponents(){
        this.addTopPanel();
        this.addMainPanel();
        this.addBottomPanel();
    }
    
    /**
     * Adiciona o painel superior
     */
    private void addTopPanel(){
        this.nextServerData     = new JLabel("Aguardando dados do servidor.");
        this.nextServerData.setFont(new Font("Arial", Font.BOLD, 12));
        this.currentServerData  = new JLabel("Aguardando dados do servidor.");
        this.currentServerData.setFont(new Font("Arial", Font.BOLD, 12));
        JLabel tokenStatusLabel = new JLabel("Estado do Token: ");
        tokenStatusLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        this.tokenStatus        = new JLabel("");
        this.tokenStatus.setFont(new Font("Arial", Font.BOLD, 17));
        this.setTokenReceived(false);
        
        JPanel topPanel = new JPanel(new BorderLayout(0, 0));
        topPanel.setBackground(new Color(220, 245, 255));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.add(topPanel, BorderLayout.NORTH);
        
        JPanel serverDataPanel = new JPanel();
        serverDataPanel.setLayout(new BoxLayout(serverDataPanel, BoxLayout.Y_AXIS));
        serverDataPanel.setBackground(new Color(0, 0, 0, 0));
        serverDataPanel.setOpaque(false);
        serverDataPanel.add(this.nextServerData);
        serverDataPanel.add(this.currentServerData);
        topPanel.add(serverDataPanel, BorderLayout.WEST);
        
        JPanel tokenStatusPanel = new JPanel();
        tokenStatusPanel.setLayout(new FlowLayout(FlowLayout.TRAILING, 0, 0));
        tokenStatusPanel.setBackground(Color.WHITE);
        tokenStatusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 5));
        tokenStatusPanel.setPreferredSize(new Dimension(160, 40));
        tokenStatusPanel.add(tokenStatusLabel);
        tokenStatusPanel.add(this.tokenStatus);
        topPanel.add(tokenStatusPanel, BorderLayout.EAST);
    }
    
    /**
     * Adiciona o painel principal.
     */
    private void addMainPanel(){
        this.text = new JTextArea(PLACEHOLDER_TEXTO);
        this.text.setFont(new Font("Arial", Font.PLAIN, 15));
        this.text.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.text.setForeground(Color.GRAY);
        this.text.addFocusListener(new FocusListener() {
            boolean bVazio = true;
            
            @Override
            public void focusGained(FocusEvent e) {
                // Oculta o placeholder ao digitar.
                if (bVazio) {
                    text.setText("");
                    text.setForeground(Color.BLACK);
                    bVazio = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                // Exibe o placeholder ao limpar.
                if (text.getText().isEmpty()) {
                    text.setForeground(Color.GRAY);
                    text.setText(PLACEHOLDER_TEXTO);
                    bVazio = true;
                }
            }
        });
        
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        this.add(mainPanel);
        
        JScrollPane pane = new JScrollPane(this.text);
        pane.setBorder(null);
        mainPanel.add(pane, BorderLayout.CENTER);
    }
    
    /**
     * Adiciona o painel inferior.
     */
    private void addBottomPanel() {
        JButton sendButton = new JButton("Solicitar Envio");
        sendButton.setFont(new Font("Arial", Font.BOLD, 15));
        sendButton.setPreferredSize(new Dimension(150, 50));
        JButton getButton = new JButton("Buscar Texto");
        getButton.setFont(new Font("Arial", Font.PLAIN, 15));
        getButton.setPreferredSize(new Dimension(125, 50));
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 5, 0));
        bottomPanel.add(getButton);
        bottomPanel.add(sendButton);
        bottomPanel.setBackground(new Color(220, 245, 255));
        this.add(bottomPanel, BorderLayout.SOUTH);
        
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(text.getText().isEmpty() || text.getText().equals(PLACEHOLDER_TEXTO)){
                    JOptionPane.showMessageDialog(null, "Digite um texto...");
                }
                else {
                    text.setEnabled(false);
                    sendButton.setEnabled(false);
                    getButton.setEnabled(false);
                    // @todo enviar o texto.
                }
            }
        });
        
        getButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // @todo solicitar texto do servidor e exibir.
            }
        });
    }

    @Override
    public void setNextServerData(String nextServerIp, String nextServerPort) {
        this.nextServerData.setText("Próximo Servidor:" + nextServerIp + ":" + nextServerPort);
    }

    @Override
    public void setCurrentServerData(String currentServerPort) {
        this.currentServerData.setText("Ouvindo em " + currentServerPort);
    }

    @Override
    public void setTokenReceived(boolean received) {
        String text = null;
        Color color = null;
        if(received){
            text = "Token Recebido";
            color = Color.GREEN;
        }
        else {
            text = "Aguardando Token";
            color = Color.RED;
        }
        this.tokenStatus.setText(text);
        this.tokenStatus.setForeground(color);
    }
    
}
