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

public class Server {
	public static void main(String[] args) throws IOException, InterruptedException {
		int porta = 56000;
		int nClients = 2;
		List<Integer> portasDisponiveis = new ArrayList<>();
		ServerSocket server = new ServerSocket(porta);
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
				ClientNode node = new ClientNode(out, in, conn.getInetAddress().getHostAddress(), conn.getPort()+"");
				clients.add(node);
				clientCount++;
				
				System.out.println(
						"Conexao estabelecida." + clientCount + " clientes conectados...");
				
			}catch(IOException ex) {
				ex.printStackTrace();
				System.out.println("Essa porta já está ocupada");
			}/* finally {
				// fecha conexão e output stream
				conn.close();
				if (out != null) {
					out.close();
				}
				System.out.println("Conexao fechada.");
				// server.close();
			}*/
		}
		
		for (int i = 0; i < clients.size(); i++) {
			if((i+1) >= clients.size()) {
				clients.get(i).write(clients.get(0).getIp()+"/"+portasDisponiveis.get(i)+"/"+portasDisponiveis.get(0)+"/token");
			}else {
				clients.get(i).write(clients.get(i+1).getIp()+"/"+portasDisponiveis.get(i)+"/"+portasDisponiveis.get(i+1)+"/noToken");
			}
		}
		
		while(true) {
			try {
			
			
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
