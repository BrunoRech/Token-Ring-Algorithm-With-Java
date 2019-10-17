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
		int portaServidor = 56000;
		Socket connServidor = null;
		BufferedReader in = null;
		PrintWriter out;
		String neighborIp = null, neighborPort = null;
		String listenerPort = null;
		Scanner sc = new Scanner(System.in);
		boolean hasToken = false;
		try {
			System.out.println("Tentando conectar...");
			connServidor = new Socket(endereco, portaServidor);
			in = new BufferedReader(new InputStreamReader(connServidor.getInputStream()));
			out = new PrintWriter(connServidor.getOutputStream(), true);
			while (neighborIp == null || neighborPort == null || listenerPort == null) {
				String[] linha = in.readLine().split("/");
				neighborIp = linha[0];
				neighborPort = linha[1];
				listenerPort = linha[2];
				if (linha[3].equalsIgnoreCase("token")) {
					hasToken = true;
				}
				/*
				 * if(linha.equalsIgnoreCase("token")) {
				 * 
				 * linha = null; System.out.println("Server: " + response); }
				 */
			}

			ServerSocket clientServer = new ServerSocket(Integer.parseInt(listenerPort));

			Socket neighbor = new Socket(neighborIp, Integer.parseInt(neighborPort));
			Socket anterior = clientServer.accept();
			PrintWriter outNeighbor = new PrintWriter(neighbor.getOutputStream(), true);
			BufferedReader inNeighbor = new BufferedReader(new InputStreamReader(anterior.getInputStream()));
			outNeighbor.println("Eu sou o " + listenerPort + " , Olá vizinho " + neighborPort);

			while (true) {
				String message = inNeighbor.readLine();
				if (message.equalsIgnoreCase("token")) {
					System.out.println("Recebi o token, agora vou repassar para o vizinho");
					hasToken = true;
				} else {
					System.out.println("->> " + message);
				}

				if (hasToken) {
					System.out.println("Execute sua ação");
					switch (sc.nextLine()) {
					case "1":
						outNeighbor.println("token");
						hasToken = false;
						break;
					default:
						break;
					}
					
				}
			}

		} catch (UnknownHostException e) {
			System.out.println("Host não encontrado");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Erro de entrada/saída ao criar socket");
			e.printStackTrace();
		} /*
			 * finally { in.close(); conn.close(); } catch (IOException e) {
			 * System.out.println("Erro ao fechar input stream ou socket");
			 * e.printStackTrace(); } }
			 */
	} // fim main()

}
