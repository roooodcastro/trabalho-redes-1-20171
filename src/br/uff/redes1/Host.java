package br.uff.redes1;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by rodcastro on 16/06/17.
 */
public class Host {
    private String address;
    private int port;
    Socket socket;

    public Host(String address, int port) {
        this.address = address;
        this.port = port;
        this.socket = null;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public boolean isConnected() {
        return socket != null;
    }

    public String toString() {
        return "Host: (" + address + ", " + port + ")";
    }

    public void connect() {
        try {
            this.socket = new Socket(address, port);
        } catch (IOException ex) {
            System.err.println("Houve um erro ao tentar conectar " + toString() + ": " + ex.getMessage());
        }
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException ex) {
            System.err.println("Houve um erro ao tentar desconectar " + toString() + ": " + ex.getMessage());
        }
    }

    public void sendMessage(String message) {
        if (!isConnected()) return;
    }
}
