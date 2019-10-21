package client;

import javax.swing.SwingUtilities;

/**
 * Fila para o envio de mensagens do servidor.
 * @author Jeferson Penz
 */
public class MessageQuery implements IMessageQuery{
    
    private boolean hasToken;
    private StringBuilder messageQuery;
    public static final int TEMPO_ESPERA_ENVIO_TOKEN = 1000;
    public static final int TEMPO_ESPERA_VERIFICA_TOKEN = 50;
    
    public MessageQuery(){
        this.hasToken = false;
        this.messageQuery = new StringBuilder();
    }
    
    @Override
    public void onMessageReceived(String message){
        if (message.equalsIgnoreCase("token")) {
            System.out.println("Recebi o token, agora vou repassar para o vizinho");
            hasToken = true;
            SwingUtilities.invokeLater(() -> {
                ClientController.getInstance().notifyTokenStatus(this.hasToken);
            });
        }
        else if(message.equalsIgnoreCase("notoken")){
            System.out.println("Não recebi o token.");
            hasToken = false;
            SwingUtilities.invokeLater(() -> {
                ClientController.getInstance().notifyTokenStatus(this.hasToken);
            });
        }
        else {
            System.out.println("->> " + message);
        }
    }
    
    @Override
    public void queryMessage(String message){
        this.messageQuery.append(message);
    }

    @Override
    public void run() {
        while(true){
            if(this.hasToken){
                try {
                    Thread.sleep(TEMPO_ESPERA_ENVIO_TOKEN);
                } catch (InterruptedException ex) {}
                if(this.messageQuery.length() > 0){
                    ClientController.getInstance().sendNextMessage(messageQuery.toString());
                    this.messageQuery.delete(0, this.messageQuery.length());
                    SwingUtilities.invokeLater(() -> {
                        ClientController.getInstance().notifyMessageDataSent();
                    });
                }
                this.hasToken = false;
                ClientController.getInstance().sendToken();
                SwingUtilities.invokeLater(() -> {
                    ClientController.getInstance().notifyTokenStatus(this.hasToken);
                });
            }
            try {
                Thread.sleep(TEMPO_ESPERA_VERIFICA_TOKEN);
            } catch (InterruptedException ex) {}
        }
    }
}
