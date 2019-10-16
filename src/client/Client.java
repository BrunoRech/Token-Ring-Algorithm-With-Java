package client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
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
		String neighborIp = null, neighborPort = null;
		String listenerPort = null;
		Scanner sc = new Scanner(System.in);
		try {
			System.out.println("Tentando conectar...");
			conn = new Socket(endereco, porta);
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			out = new PrintWriter(conn.getOutputStream(), true);
			while (neighborIp == null || neighborPort == null || listenerPort == null) {
				String[] linha = in.readLine().split("/");
				neighborIp = linha[0];
				neighborPort = linha[1];
				listenerPort = linha[2];
				/*
				if(linha.equalsIgnoreCase("token")) {
					System.out.println("Execute sua ação");
					String message = sc.nextLine();
					out.println(message);
					System.out.println("Mensagem enviada, aguardando resposta do servidor");
					String response = in.readLine();
					linha = null;
					System.out.println("Server: " + response);
				}
				*/
			}
			System.out.println("Abrindo server porta " + listenerPort);
			ServerSocket server = new ServerSocket(Integer.parseInt(listenerPort));
			System.out.println("Conectando com o vizinho " + neighborIp+":"+neighborPort);
			Socket neighbor = new Socket(neighborIp, Integer.parseInt(neighborPort));
			Socket anterior = server.accept();
			out = new PrintWriter(neighbor.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(anterior.getInputStream()));
			out.println("Eu sou o " +listenerPort +" , Olá vizinho " + neighborPort);
			while (true) {
				String message = in.readLine();
				System.out.println("->> "+ message);
				
			}
			
			
		} catch (UnknownHostException e) {
			System.out.println("Host não encontrado");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Erro de entrada/saída ao criar socket");
			e.printStackTrace();
		}/* finally {
					in.close();
					conn.close();
			} catch (IOException e) {
				System.out.println("Erro ao fechar input stream ou socket");
				e.printStackTrace();
			}
		}*/
	} // fim main()

}
