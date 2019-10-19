package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Server;

public class ClientNode implements Runnable{

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

    @Override
    public void run() {
        while(true){
            String message;
            try {
                message = input.readLine();
                Server.getInstance().onMessageReceived(message);
            } catch (IOException ex) {}
        }
    }

}
