package client;

/**
 * Interface com as capacidades da Tela de comunicação com o Usuário - Cliente.
 * @author Jeferson Penz
 */
public interface IUserInterface {
    
    /**
     * Adiciona os dados do próximo servidor.
     * @param nextServerIp
     * @param nextServerPort 
     */
    public void setNextServerData(String nextServerIp, String nextServerPort);
    
    /**
     * Adiciona os dados do servidor rodando localmente.
     * @param currentServerPort 
     */
    public void setCurrentServerData(String currentServerPort);
    
    /**
     * Define se o token foi recebido ou não.
     * @param received 
     */
    public void setTokenReceived(boolean received);
    
    /**
     * Exibe a tela (implementação de JFrame.setVisible).
     * @param visible 
     */
    public void setVisible(boolean visible);
    
}
