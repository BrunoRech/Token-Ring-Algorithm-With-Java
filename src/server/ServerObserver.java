package server;

/**
 * Interface para capacidades do servidor para comunicação com o usuário.
 * @author Jeferson Penz
 */
public interface ServerObserver {
    
    /**
     * Define o ip de comunicação do servidor.
     * @param port 
     */
    public void setServerIpData(String port);
    
    /**
     * Define a porta de comunicação do servidor.
     * @param port 
     */
    public void setServerPortData(String port);
    
    /**
     * Evento de mensagem recebida.
     * @param message 
     */
    public void onMessageReceived(String message);
    
}
