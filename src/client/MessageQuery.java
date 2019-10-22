package client;

import javax.swing.SwingUtilities;
import server.ClientNode;

/**
 * Fila para o envio de mensagens do servidor.
 * @author Jeferson Penz
 */
public class MessageQuery implements IMessageQuery{
    
    private String token;
    private StringBuilder messageQuery;
    private StringBuilder bufferedMessage;
    private boolean waitingResponse = false;
    private boolean useToken = true;
    public static final int TEMPO_ESPERA_ENVIO_TOKEN = 500; // Força o cliente a segurar o token por um tempo.
    public static final int TEMPO_ESPERA_VERIFICA_TOKEN = 50;
    
    public MessageQuery(){
        this.messageQuery = new StringBuilder();
    }
    
    @Override
    public synchronized void onMessageReceived(String message){
        // @todo Melhorar.
        System.out.println("Recebeu: " + message);
        if (message.startsWith(ClientNode.TOKEN_MESSAGE)) {
            message = message.replaceFirst(ClientNode.TOKEN_MESSAGE, "");
            if(message.equalsIgnoreCase("notoken")){
                SwingUtilities.invokeLater(() -> {
                    ClientController.getInstance().notifyTokenStatus(false);
                });
            }
            else if(message.equalsIgnoreCase("nouse")){
                this.useToken = false;
                SwingUtilities.invokeLater(() -> {
                    ClientController.getInstance().notifyTokenStatus(true);
                });
            }
            else{
                this.token = message;
                SwingUtilities.invokeLater(() -> {
                    ClientController.getInstance().notifyTokenStatus(true);
                });
            }
        }
        else if(bufferedMessage != null){
            if(message.equals(ClientNode.DATA_END_MESSAGE)){
                final String receivedMessage = bufferedMessage.toString();
                SwingUtilities.invokeLater(() -> {
                    ClientController.getInstance().notifyDataReceived(receivedMessage);
                });
                bufferedMessage = null;
            }
            else {
                bufferedMessage.append(message).append("\n");
            }
        }
        else if(message.equals(ClientNode.DATA_MESSAGE)){
            bufferedMessage = new StringBuilder();
        }
        else if(message.equals(ClientNode.DONE_MESSAGE)){
            waitingResponse = false;
        }
    }
    
    @Override
    public void queryMessage(String message){
        this.messageQuery.append(message);
    }

    @Override
    public void run() {
        while(true){
            if(!useToken || this.token != null){
                try {
                    Thread.sleep(TEMPO_ESPERA_ENVIO_TOKEN);
                } catch (InterruptedException ex) {}
                if(this.messageQuery.length() > 0){
                    String[] lines = messageQuery.toString().split("\r\n|\r|\n");
                    this.messageQuery.delete(0, this.messageQuery.length());
                    for (String line : lines) {
                        ClientController.getInstance().sendNextMessage(line);
                        this.waitingResponse = true;
                        while(this.waitingResponse){
                            try {
                                Thread.sleep(TEMPO_ESPERA_VERIFICA_TOKEN);
                            } catch (InterruptedException ex) {}
                        }
                    }
                    SwingUtilities.invokeLater(() -> {
                        ClientController.getInstance().notifyMessageDataSent();
                    });
                }
                if(useToken){
                    ClientController.getInstance().sendToken(this.token);
                    this.token = null;
                    SwingUtilities.invokeLater(() -> {
                        ClientController.getInstance().notifyTokenStatus(this.token != null);
                    });
                }
            }
            try {
                Thread.sleep(TEMPO_ESPERA_VERIFICA_TOKEN);
            } catch (InterruptedException ex) {}
        }
    }
}
