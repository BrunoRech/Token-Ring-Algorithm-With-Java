package client;

/**
 * Interface com as capacidades da Tela de comunicação com o Usuário.
 * @author Jeferson Penz
 */
public interface Observador {

    /**
     * Adiciona os dados do próximo servidor.
     * 
     * @param nextServerIp
     * @param nextServerPort
     */
    public void setNextServerData(String nextServerIp, String nextServerPort);

    /**
     * Adiciona os dados do servidor rodando localmente.
     * 
     * @param currentServerPort
     */
    public void setCurrentServerData(String currentServerPort);

    /**
     * Define se o token foi recebido ou não.
     * 
     * @param received
     */
    public void setTokenReceived(boolean received);

    /**
     * Exibe a tela (implementação de JFrame.setVisible).
     * 
     * @param visible
     */
    public void setVisible(boolean visible);

    /**
     * Indica que os dados que foram solicitados para envio foram enviados.
     */
    public void onMessageDataSent();

    /**
     * Indica que os dados que houve um retorno do servidor diferente do TOKEN.
     */
    public void onMessageDataReceived(String data);

}
