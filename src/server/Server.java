package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server {
    
    private static Server instance;
    private ServerSocket server;
    private ReadWriteControl fileControl;
    public static final String CAMINHO_ARQUIVO = "/src/arquivo.txt";
    
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
        int porta = 56000;
        int nClients = 2;
        List<Integer> portasDisponiveis = new ArrayList<>();
        this.fileControl = new ReadWriteControl(CAMINHO_ARQUIVO);
        try {
            this.server = new ServerSocket(porta);
        } catch (IOException ex) {}
        PrintWriter out = null;
        BufferedReader in;
        Socket conn = null;
        LinkedList<ClientNode> clients = new LinkedList<>();
        int clientCount = 0;

        for (int i = 1; i < 6; i++) {
            portasDisponiveis.add((56000 + i));
        }

        while (clientCount < nClients) {
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
            }/* finally {
				// fecha conex�o e output stream
				conn.close();
				if (out != null) {
					out.close();
				}
				System.out.println("Conexao fechada.");
				// server.close();
			}*/
        }

        for (int i = 0; i < clients.size(); i++) {
            if ((i + 1) >= clients.size()) {
                clients.get(i).write(clients.get(0).getIp() + "/" + portasDisponiveis.get(i) + "/" + portasDisponiveis.get(0) + "\ntoken");
            } else {
                clients.get(i).write(clients.get(i + 1).getIp() + "/" + portasDisponiveis.get(i) + "/" + portasDisponiveis.get(i + 1) + "/\nnoToken");
            }
        }
    }
    
    public void onMessageReceived(String message){
        System.out.println(message);
        try {
            this.fileControl.addData(message);
        } catch (IOException ex) {}
    }

}
