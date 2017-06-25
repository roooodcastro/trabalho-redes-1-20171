package br.uff.redes1.server;

import br.uff.redes1.ipv4.Datagram;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by rodcastro on 16/06/17.
 */
public class Neighbour {
    private String realAddress; // "Real" IP address (127.0.0.1 if running on localhost)
    private String address;     // Simulated IP address
    private String subnetMask;  // Subnet mask
    private int port;           //
    Socket socket;              // Socket from host (the simulator on this instance) to this neighbour

    public Neighbour(String realAddress, int port) {
        this.realAddress = realAddress;
        this.address = "0.0.0.0";            // Default value, will be changed later
        this.subnetMask = "255.255.255.255"; // Default value, will be changed later
        this.port = port;
        this.socket = null; // Will be opened when the simulator connects to it.
    }

    public Neighbour(String interfaceAddress, String subnetMask) {
        this.address = address;
        this.subnetMask = subnetMask;
    }

    public String getRealAddress() {
        return realAddress;
    }

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
    }

    public boolean isConnected() {
        return socket != null;
    }

    public String toString() {
        return "Neighbour: (IP: " + address + ", Mask: " + subnetMask + ", Port: " + port + ")";
    }

    public void connect() {
        try {
            this.socket = new Socket(realAddress, port);
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
        Datagram datagram = new Datagram(message, realAddress, address);
        return sendMessage(datagram);
    }

    public boolean sendMessage(Datagram datagram) {
        if (!isConnected()) connect();
        try {
            OutputStream out = socket.getOutputStream();
            out.write(datagram.getBytes());
            out.flush();
            return true;
        } catch (IOException ioex) {
            System.err.println("Houve um erro ao enviar uma mensagem para " + toString() + ": " + ioex.getMessage());
            return false;
        }
    }
}
