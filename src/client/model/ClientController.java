package client.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import client.controller.Observado;
import client.controller.Observador;

public class ClientController implements Observado{
	
    private int portaServidor = 56000;
    private Socket connServidor = null;
    private BufferedReader in = null;
    private PrintWriter out;
    private String neighborIp = null, neighborPort = null;
    private String listenerPort = null;
    private Scanner sc = new Scanner(System.in);
	private boolean hasToken = false;
	private List<Observador> observadores = new ArrayList<>();
	private static ClientController instance;
	
	public static ClientController getInstance() {
		if(instance == null) {
			instance = new ClientController();
		}
		return instance;
	}
	
	private ClientController() {}
	
	public void conectToTheServer(String serverIp) throws IOException {
		connServidor = new Socket(serverIp, portaServidor);
		System.out.println("Tentando conectar...");
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
		}
	}
	
	public void openClientListener() throws NumberFormatException, IOException {
		ServerSocket clientServer = new ServerSocket(Integer.parseInt(listenerPort));

		Socket neighbor = new Socket(neighborIp, Integer.parseInt(neighborPort));
		Socket anterior = clientServer.accept();
		PrintWriter outNeighbor = new PrintWriter(neighbor.getOutputStream(), true);
		BufferedReader inNeighbor = new BufferedReader(new InputStreamReader(anterior.getInputStream()));
		outNeighbor.println("Eu sou o " + listenerPort + " , Ol� vizinho " + neighborPort);

		while (true) {
			String message = inNeighbor.readLine();
			if (message.equalsIgnoreCase("token") || this.hasToken) {
				System.out.println("Recebi o token, agora vou repassar para o vizinho");
				for (Observador obs : observadores) {
					obs.notifyTokenReceived();
				}
			} else {
				System.out.println("->> " + message);
			}

			if (hasToken) {
				System.out.println("Execute sua a��o");
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
	}

	@Override
	public void addObservador(Observador obs) {
		observadores.add(obs);
	}

}
