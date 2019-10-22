package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Classe para comunicação com o cliente.
 * @author Bruno Galeazzi Rech, Jeferson Penz
 */
public class ClientNode implements Runnable {

    private PrintWriter output;
    private BufferedReader input;
    private String ip;
    private String port;
    /**
     * Mensagem para indicar Escrita.
     */
    public static final String WRITE_MESSAGE = "WRITE>";
    /**
     * Mensagem para indicar que o listener do cliente está aberto.
     */
    public static final String LISTENER = "LISTENER>";
    /**
     * Mensagem para indicar Leitura.
     */
    public static final String READ_MESSAGE = "READ>";
    /**
     * Mensagem para indicar a conclusão.
     */
    public static final String DONE_MESSAGE = "DONE>";
    /**
     * Mensagem para indicar transferencia de Dados.
     */
    public static final String DATA_MESSAGE = "BEGIN_DATA>";
    /**
     * Mensagem para indicar fim da transferencia de Dados.
     */
    public static final String DATA_END_MESSAGE = "<END_DATA";
    /**
     * Mensagem para indicar o TOKEN.
     */
    public static final String TOKEN_MESSAGE = "TOKEN>";

    public ClientNode(PrintWriter pw, BufferedReader br, String ip, String port) {
        this.output = pw;
        this.input = br;
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public void write(String message) {
        output.println(message);
    }

    @Override
    public void run() {
        while (true) {
            String message;
            try {
                message = input.readLine();
                if (message.startsWith(WRITE_MESSAGE)) {
                    message = message.replaceFirst("^" + WRITE_MESSAGE, "");
                    ServerController.getInstance().onMessageReceived(this, message);
                } else if (message.startsWith(READ_MESSAGE)) {
                    ServerController.getInstance().onReadRequest(this);
                } else if (message.startsWith(LISTENER)) {
                    ServerController.getInstance().increaseClientReady();
                }
            } catch (IOException ex) {
            }
        }
    }

}
