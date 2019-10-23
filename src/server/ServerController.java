package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.swing.SwingUtilities;

/**
 * Controle para a tela do servidor.
 * @author Bruno Galeazzi Rech, Jeferson Penz
 */
public class ServerController {
    
    private int porta;
    private int clientCount;
    private int clientsReady = 0;
    private ServerSocket server;
    private ReadWriteControl fileControl;
    public static final int NUMERO_CLIENTES = 5;
    public static final String CAMINHO_ARQUIVO = "arquivo_servidor_dsd.txt";
    public static final String TOKEN = createToken();
    public static final boolean USA_TOKEN = true;
    private List<ServerObserver> observers;
    LinkedList<ClientNode> clients;
    
    /**
     * Cria o token para troca e comunicação.
     * @return 
     */
    public static String createToken(){
        String token = "";
        Random rnd = new Random();
        int firstRand = rnd.nextInt(999999);
        int secondRand = rnd.nextInt(999999);
        token += String.format("%06d", firstRand);
        token += "tokenTESTE";
        token += String.format("%06d", secondRand);
        try {
            byte[] bytes = token.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("MD5");
            BigInteger bigInt = new BigInteger(1, md.digest(bytes));
            token = bigInt.toString(16);
            while(token.length() < 32 ){
                token = "0" + token;
            }
        } catch (NoSuchAlgorithmException ex) {
        ex.printStackTrace();
        }
        return token;
    }
    
    private static ServerController instance;
    public static ServerController getInstance(){
        if(instance == null){
            instance = new ServerController();
        }
        return instance;
    }

    private ServerController() {
        this.observers = new ArrayList<>();
        this.clients = new LinkedList<>();
        this.porta = 56000;
        this.clientCount = 0;
        try {
            this.fileControl = new ReadWriteControl(CAMINHO_ARQUIVO);
            new Thread(this.fileControl).start();
            this.server = new ServerSocket(porta);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void openConnection(){
        List<Integer> portasDisponiveis = new ArrayList<>();
        PrintWriter out;
        BufferedReader in;
        Socket conn;

        for (int i = 1; i < 6; i++) {
            portasDisponiveis.add((56000 + i));
        }
        try {
            this.notifyServerIp("" + InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            this.notifyServerIp("Desconhecido");
            ex.printStackTrace();
        }
        this.notifyPortStatus("" + this.porta);

        while (clientCount < NUMERO_CLIENTES) {
            try {
                server.setReuseAddress(true);
                this.notifyMessageReceived("Aguardando conexao do cliente " + (clientCount + 1) + ".");
                conn = server.accept();
                out = new PrintWriter(conn.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                System.out.println(conn.getLocalAddress().getHostAddress() + " " +  conn.getInetAddress().getHostAddress());
                ClientNode node = new ClientNode(out, in, conn.getInetAddress().getHostAddress(), conn.getPort() + "");
                clients.add(node);
                new Thread(node).start();
                clientCount++;

                this.notifyMessageReceived("Conexao estabelecida na porta. " + clientCount + " clientes conectados...");

            } catch (IOException ex) {
                this.notifyMessageReceived("Houve um erro na conexão com o cliente: " + ex.getMessage());
                break;
            }
        }

        for (int i = 0; i < clients.size(); i++) {
            if ((i + 1) >= clients.size()) {
                clients.get(i).write(clients.get(0).getIp() + "/" + portasDisponiveis.get(0) + "/" + portasDisponiveis.get(i));
                System.out.println(clients.get(i).getIp() + " --> "+clients.get(0).getIp() + "/" + portasDisponiveis.get(i) + "/" + portasDisponiveis.get(0));
            } else {
                clients.get(i).write(clients.get(i + 1).getIp() + "/" + portasDisponiveis.get(i + 1) + "/" + portasDisponiveis.get(i));
                System.out.println(clients.get(i).getIp() + " --> "+clients.get(i+1).getIp() + "/" + portasDisponiveis.get(i) + "/" + portasDisponiveis.get(i+1));
            }
            if(USA_TOKEN){
                if ((i + 1) >= clients.size()) {
                    clients.get(i).write(ClientNode.TOKEN_MESSAGE + TOKEN);
                } else {
                    clients.get(i).write(ClientNode.TOKEN_MESSAGE + "notoken");
                }
            }
            else {
                clients.get(i).write(ClientNode.TOKEN_MESSAGE + "nouse");
            }
        }
    }
    
    public void onMessageReceived(ClientNode target, String message){
        this.notifyMessageReceived("Recebeu e escreveu a mensagem: " + message + " de " + target.getIp() + ":" + target.getPort());
        try {
            this.fileControl.addData(message + "\n");
        } catch (IOException ex) {}
        target.write(ClientNode.DONE_MESSAGE);
    }

    public void onReadRequest(ClientNode target) {
        this.notifyMessageReceived("Recebeu requisição de leitura de " + target.getIp() + ":" + target.getPort());
        String data = this.fileControl.getAllData();
        target.write(ClientNode.DATA_MESSAGE);
        target.write(prepareSendData(data));
        target.write(ClientNode.DATA_END_MESSAGE);
        target.write(ClientNode.DONE_MESSAGE);
    }
    
    protected String prepareSendData(String data){
        return data.replaceAll(ClientNode.DATA_MESSAGE + "|" + ClientNode.DATA_END_MESSAGE, "");
    }
    
    /**
     * Notifica os observadores do estado da porta.
     * @param ip
     */
    protected void notifyServerIp(String ip){
        SwingUtilities.invokeLater(() -> {
            this.observers.forEach((observer) -> {
                observer.setServerIpData(ip);
            });
        });
    }
    
    /**
     * Notifica os observadores do estado da porta.
     * @param port
     */
    protected void notifyPortStatus(String port){
        SwingUtilities.invokeLater(() -> {
            this.observers.forEach((observer) -> {
                observer.setServerPortData(port);
            });
        });
    }
    
    /**
     * Notifica os observadores que uma mensagem foi recebida.
     * @param message 
     */
    protected void notifyMessageReceived(String message){
        SwingUtilities.invokeLater(() -> {
            this.observers.forEach((observer) -> {
                observer.onMessageReceived("Mensagem: " + message);
            });
        });
    }
    
    /**
     * Adiciona um observador para o servidor.
     * @param observer 
     */
    public void addObserver(ServerObserver observer){
        this.observers.add(observer);
    }

    public void increaseClientReady() {
        this.clientsReady++;
        if (this.clientsReady >= this.clientCount) {
            for (int i = 0; i < clients.size(); i++) {
                clients.get(i).write(ClientNode.LISTENER);
            }
        }
    }

}
