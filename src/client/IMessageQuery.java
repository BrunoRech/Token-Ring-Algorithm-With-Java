package client;

/**
 *
 * @author Bruno Galeazzi Rech, Jeferson Penz
 */
interface IMessageQuery extends Runnable {

    public void onMessageReceived(String message);

    public void queryMessage(String message);

}
