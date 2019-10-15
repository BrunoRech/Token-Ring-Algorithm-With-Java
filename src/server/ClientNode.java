package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ClientNode {
	private PrintWriter output;
	private BufferedReader input;
	
	public ClientNode (PrintWriter pw, BufferedReader br) {
		this.output = pw;
		this.input = br;
	}

	public void write(String message) {
		output.println(message);
	}
	
	public String read() {
		try {
			return input.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public PrintWriter getOutput() {
		return output;
	}

	public void setOutput(PrintWriter output) {
		this.output = output;
	}

	public BufferedReader getInput() {
		return input;
	}

	public void setInput(BufferedReader input) {
		this.input = input;
	}
	
	
}
