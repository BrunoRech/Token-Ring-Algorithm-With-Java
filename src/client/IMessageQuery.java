package client;

/**
 *
 * @author Jeferson Penz
 */
interface IMessageQuery extends Runnable {

    public void onMessageReceived(String message);

    public void queryMessage(String message);

}
