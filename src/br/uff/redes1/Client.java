package br.uff.redes1;

import br.uff.redes1.ipv4.Datagram;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by rodcastro on 16/06/17.
 */
public class Client extends Thread {
    private int portNumber;
    private ServerSocket listener;
    private volatile boolean running;

    public Client(int portNumber) {
        this.running = true;
        this.portNumber = portNumber;
        try {
            listener = new ServerSocket(portNumber);
        } catch (IOException ex) {
            System.err.println("Não foi possível abrir a porta " + portNumber + " para escuta: " + ex.getMessage());
        }
    }

    public void close() {
        try {
            listener.close();
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
                Socket client = listener.accept();
                String message = getSocketMessage(client.getInputStream());
                String sender = client.getRemoteSocketAddress().toString();
                System.out.println("\n" + sender + " Enviou uma mensagem: " + message.toString());
                client.close();
            } catch (SocketException sex) {
                System.out.println("Cliente terminado na porta " + portNumber);
            } catch (IOException ex) {
                System.err.println("Houve um erro ao receber mensagem na porta " + portNumber + ": " + ex.getMessage());
            }
        }
    }

    private String getSocketMessage(InputStream stream) throws IOException {
        try { Thread.sleep(10); } catch (InterruptedException ex) {}
        if (stream.available() > 0) {
            Datagram message = Datagram.fromInputStream(stream);
            return message.toString();
        }
        return "";
    }
}
