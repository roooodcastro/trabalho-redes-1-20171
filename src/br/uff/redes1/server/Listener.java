package br.uff.redes1.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rodcastro on 17/06/17.
 */
public class Listener extends Thread {
    private int portNumber;
    private ServerSocket listener;
    private volatile boolean running;
    private List<Client> clients;
    private List<Neighbour> interfaces;
    private Router router;

    public Listener(int portNumber, List<Neighbour> interfaces, File routingFile) {
        this.running = true;
        this.portNumber = portNumber;
        this.clients = new ArrayList<>();
        this.interfaces = interfaces;
        this.router = new Router(interfaces, routingFile);
        try {
            listener = new ServerSocket(portNumber);
        } catch (IOException ex) {
            System.err.println("Não foi possível abrir a porta " + portNumber + " para escuta: " + ex.getMessage());
        }
    }

    /**
     * Checks if the routes are valid.
     * @return True if routes are valid, false otherwise.
     */
    public boolean isValid() {
        return router.areRoutesValid();
    }

    public Router getRouter() {
        return router;
    }

    public void close() {
        try {
            listener.close();
            for (Client client : clients) {
                client.close();
            }
            this.running = false;
        } catch (IOException ex) {
            System.err.println("Houve um erro ao desconectar o client na porta " + portNumber + ": " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.println("Listening to packets on port " + portNumber);
        while (running) {
            try {
                Client client = new Client(this, listener.accept());
                clients.add(client);
                client.start();
            } catch (SocketException sex) {
                System.out.println("Cliente terminado na porta " + portNumber);
            } catch (IOException ex) {
                System.err.println("Houve um erro ao receber mensagem na porta " + portNumber + ": " + ex.getMessage());
            }
        }
    }
}
