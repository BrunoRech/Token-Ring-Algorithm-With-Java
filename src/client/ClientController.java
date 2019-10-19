package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ClientController implements Observado {

    private int portaServidor = 56000;
    private boolean hasToken = false;
    private Socket connServidor = null;
    private BufferedReader serverIn = null;
    private PrintWriter serverOut;
    private BufferedReader neighborIn;
    private PrintWriter neighborOut;
    private String neighborIp = null, neighborPort = null;
    private String listenerPort = null;
    private List<Observador> observadores = new ArrayList<>();
    private IMessageQuery messageQuery;
    private static ClientController instance;

    public static ClientController getInstance() {
        if (instance == null) {
            instance = new ClientController();
        }
        return instance;
    }

    private ClientController() {
        this.messageQuery = new MessageQuery();
    }

    public void conectToTheServer(String serverIp) throws IOException {
        connServidor = new Socket(serverIp, portaServidor);
        System.out.println("Tentando conectar...");
        serverIn = new BufferedReader(new InputStreamReader(connServidor.getInputStream()));
        serverOut = new PrintWriter(connServidor.getOutputStream(), true);
        while (neighborIp == null || neighborPort == null || listenerPort == null) {
            String[] linha = serverIn.readLine().split("/");
            neighborIp = linha[0];
            neighborPort = linha[1];
            listenerPort = linha[2];
            this.notifyCurrentServerData(listenerPort);
            this.notifyNextServerData(neighborIp, neighborPort);
            
            String token = serverIn.readLine();
            messageQuery.onMessageReceived(token);
        }
    }

    public void openClientListener() throws NumberFormatException, IOException {
        ServerSocket clientServer = new ServerSocket(Integer.parseInt(listenerPort));

        Socket neighbor = new Socket(neighborIp, Integer.parseInt(neighborPort));
        Socket anterior = clientServer.accept();
        neighborOut = new PrintWriter(neighbor.getOutputStream(), true);
        neighborIn = new BufferedReader(new InputStreamReader(anterior.getInputStream()));
        neighborOut.println("Eu sou o " + listenerPort + " , Ol� vizinho " + neighborPort);
        new Thread(this.messageQuery).start();

        while (true) {
            String message = neighborIn.readLine();
            messageQuery.onMessageReceived(message);
        }
    }
    
    public void querySendMessage(String message){
        this.messageQuery.queryMessage(message);
    }

    @Override
    public void addObservador(Observador obs) {
        observadores.add(obs);
    }

    public void sendToken() {
        System.out.println("Enviando o token...");
        neighborOut.println("token");
    }

    public void sendNextMessage(String message) {
        serverOut.println(message);
    }
    
    public void notifyCurrentServerData(String port){
        this.observadores.forEach((obs) -> {
            obs.setCurrentServerData(port);
        });
    }
    
    public void notifyNextServerData(String ip, String port){
        this.observadores.forEach((obs) -> {
            obs.setNextServerData(ip, port);
        });
    }
    
    public void notifyTokenStatus(boolean status){
        this.observadores.forEach((obs) -> {
            obs.setTokenReceived(status);
        });
    }
    
    public void notifyMessageDataSent(){
        this.observadores.forEach((obs) -> {
            obs.onMessageDataSent();
        });
    }

}
