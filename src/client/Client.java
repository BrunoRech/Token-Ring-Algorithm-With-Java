package client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Client {

    public static void main(String[] args) {

        String serverIp = "127.0.0.1"; //passar por um input

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
        }
        UserInterface UI = new UserInterface();
        UI.setVisible(true);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(UserInterface.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            ClientController client = ClientController.getInstance();
            client.conectToTheServer(serverIp);
            client.openClientListener();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
