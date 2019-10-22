package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class Server {
    
    private int porta;
    private int clientCount;
    private ServerSocket server;
    private ReadWriteControl fileControl;
    public static final int NUMERO_CLIENTES = 2;
    public static final String CAMINHO_ARQUIVO = "src/arquivo_servidor_dsd.txt";
    public static final String TOKEN = createToken();
    public static final boolean USA_TOKEN = true;
    
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
        } catch (NoSuchAlgorithmException ex) {}
        System.out.println(token);
        return token;
    }
    
    private static Server instance;
    public static Server getInstance(){
        if(instance == null){
            instance = new Server();
        }
        return instance;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Server.getInstance();
    }

    private Server() {
        try {
            this.porta = 56000;
            this.clientCount = 0;
            this.fileControl = new ReadWriteControl(CAMINHO_ARQUIVO);
            new Thread(this.fileControl).start();
            this.server = new ServerSocket(porta);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        List<Integer> portasDisponiveis = new ArrayList<>();
        PrintWriter out = null;
        BufferedReader in;
        Socket conn = null;
        LinkedList<ClientNode> clients = new LinkedList<>();

        for (int i = 1; i < 6; i++) {
            portasDisponiveis.add((56000 + i));
        }

        while (clientCount < NUMERO_CLIENTES) {
            try {
                server.setReuseAddress(true);
                System.out.println("Aguardando conexao de cliente...");
                conn = server.accept();
                out = new PrintWriter(conn.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                ClientNode node = new ClientNode(out, in, conn.getInetAddress().getHostAddress(), conn.getPort() + "");
                clients.add(node);
                new Thread(node).start();
                clientCount++;

                System.out.println(
                        "Conexao estabelecida." + clientCount + " clientes conectados...");

            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Essa porta j� est� ocupada");
            }
        }

        for (int i = 0; i < clients.size(); i++) {
            if ((i + 1) >= clients.size()) {
                clients.get(i).write(clients.get(0).getIp() + "/" + portasDisponiveis.get(i) + "/" + portasDisponiveis.get(0));
            } else {
                clients.get(i).write(clients.get(i + 1).getIp() + "/" + portasDisponiveis.get(i) + "/" + portasDisponiveis.get(i + 1));
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
        // @todo Exibir em interface grafica.
        System.out.println("Escrevendo: " + message);
        try {
            this.fileControl.addData(message + "\n");
        } catch (IOException ex) {}
        target.write(ClientNode.DONE_MESSAGE);
    }

    public void onReadRequest(ClientNode target) {
        // @todo Exibir em interface grafica.
        String data = this.fileControl.getAllData();
        target.write(ClientNode.DATA_MESSAGE);
        target.write(prepareSendData(data));
        target.write(ClientNode.DATA_END_MESSAGE);
        target.write(ClientNode.DONE_MESSAGE);
    }
    
    protected String prepareSendData(String data){
        return data.replaceAll(ClientNode.DATA_MESSAGE + "|" + ClientNode.DATA_END_MESSAGE, "");
    }

}
