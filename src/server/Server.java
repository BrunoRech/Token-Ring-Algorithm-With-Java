package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.ListIterator;

public class Server {
	public static void main(String[] args) throws IOException, InterruptedException {
		int porta = 56000;
		int counter = 0;
		int nClients = 2;
		ServerSocket server = new ServerSocket(porta);
		server.setReuseAddress(true);
		PrintWriter out = null;
		BufferedReader in;
		Socket conn = null; // socket para comunicar com o cliente
		LinkedList<ClientNode> clients = new LinkedList<>();
		int clientCount = 0;
		while (clientCount < nClients) {
			try {
				System.out.println("Aguardando conexao de cliente...");
				conn = server.accept();
				out = new PrintWriter(conn.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				ClientNode node = new ClientNode(out, in);
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
				clients.get(i).setNeighbor(clients.get(0).getOutput());
				clients.get(0).write("token");
			}
			clients.get(i).setNeighbor(clients.get(i+1).getOutput());
		}
		
		while(true) {
			try {
			/*
			 * ClientNode client = clients.get(counter);
			counter++;
			if(counter >= clients.size()) {
				counter = 0;
			}
			client.write("token");
			
				String message = client.read();
				client.write("Resposta: " + message);
			 */
			
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	// o server.close() foi comentado por ser programa teste
	// mas em uma app real, precisa fechar senão conexões/portas
	// ficarão abertas e pode causar erro no sistema
}
