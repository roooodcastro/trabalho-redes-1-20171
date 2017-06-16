package br.uff.redes1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by rodcastro on 16/06/17.
 */
public class Client extends Thread {
    private int portNumber;
    private ServerSocket listener;

    public Client(int portNumber) {
        this.portNumber = portNumber;
        try {
            listener = new ServerSocket(portNumber);
        } catch (IOException ex) {
            System.err.println("Could not open port " + portNumber + " for listening");
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Listening to packets on port " + portNumber);
        while (true) {
            try {
                Socket client = listener.accept();
                String message = client.getOutputStream().toString();
                String sender = client.getRemoteSocketAddress().toString();
                System.out.printf(sender + " sent a message: " + message);
                client.close();
            } catch (IOException ex) {}
        }
    }
}
