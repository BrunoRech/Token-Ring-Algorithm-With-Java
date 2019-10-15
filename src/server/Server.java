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
		ServerSocket server = new ServerSocket(porta);
		server.setReuseAddress(true);
		PrintWriter out = null;
		BufferedReader in;
		Socket conn = null; // socket para comunicar com o cliente
		LinkedList<PrintWriter> clients = new LinkedList<>();
		while (true) {
			try {
				System.out.println("Aguardando conexao de cliente...");
				conn = server.accept();
				out = new PrintWriter(conn.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				clients.add(out);

				System.out.println(
						"Conexao estabelecida. " + conn.getInetAddress().getHostAddress() + " Enviando dados...");
				// Criar um PrintWriter para escrever no output do Socket (true = autoflush)
				
					PrintWriter writer = clients.get(counter);
					counter++;
					if(counter >= clients.size()) {
						counter = 0;
					}
					writer.println("token");
			
				// }
					
					
					try {
						String message = in.readLine();
						out.println("Resposta: " + message);
					} catch (Exception e) {
						// TODO: handle exception
					}
			}catch(IOException ex) {
				ex.printStackTrace();
				System.out.println("Essa porta já está ocupada");
			} finally {
				// fecha conexão e output stream
				conn.close();
				if (out != null) {
					out.close();
				}
				System.out.println("Conexao fechada.");
				// server.close();
			}
		}

	}
	// o server.close() foi comentado por ser programa teste
	// mas em uma app real, precisa fechar senão conexões/portas
	// ficarão abertas e pode causar erro no sistema
}
