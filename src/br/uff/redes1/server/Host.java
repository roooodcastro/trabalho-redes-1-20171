package br.uff.redes1.server;

import br.uff.redes1.ipv4.Datagram;

import java.io.IOException;
import java.io.OutputStream;
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

    public boolean sendMessage(String message) {
        if (!isConnected()) return false;
        try {
            OutputStream out = socket.getOutputStream();
            Datagram datagram = new Datagram(message, socket.getLocalAddress().getHostAddress(), address);
            out.write(datagram.getBytes());
            out.flush();
            return true;
        } catch (IOException ioex) {
            System.err.println("Houve um erro ao enviar uma mensagem para " + toString() + ": " + ioex.getMessage());
            return false;
        }
    }
}
