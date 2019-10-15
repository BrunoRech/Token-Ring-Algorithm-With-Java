package client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) {
		String endereco = "127.0.0.1";
			int porta = 56000;
		Socket conn = null;
		BufferedReader in = null;
		PrintWriter out;
		Scanner sc = new Scanner(System.in);;
		try {
			System.out.println("Tentando conectar...");
			conn = new Socket(endereco, porta);
			System.out.println("Conectado! Aguardando token");
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			out = new PrintWriter(conn.getOutputStream(), true);
			while (true) {
				String linha = in.readLine(); //resposta do servidor
				if(linha.equalsIgnoreCase("token")) {
					System.out.println("Execute sua ação");
					String message = sc.nextLine();
					out.println(message);
					System.out.println("Mensagem enviada, aguardando resposta do servidor");
					String response = in.readLine();
					linha = null;
					System.out.println("Server: " + response);
				}
			}
		} catch (UnknownHostException e) {
			System.out.println("Host não encontrado");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Erro de entrada/saída ao criar socket");
			e.printStackTrace();
		}/* finally {
			try {// fecha input stream e socket
				if (in != null) {
					in.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (IOException e) {
				System.out.println("Erro ao fechar input stream ou socket");
				e.printStackTrace();
			}
		}*/
	} // fim main()

}
