package server.auxiliar;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class ClientNode {
	private PrintWriter output;
	private BufferedReader input;
	private String ip;
	private String port;

	public ClientNode(PrintWriter pw, BufferedReader br, String ip, String port) {
		this.output = pw;
		this.input = br;
		this.ip = ip;
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void write(String message) {
		output.println(message);
	}

}
