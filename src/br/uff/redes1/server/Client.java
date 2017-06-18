package br.uff.redes1.server;

import br.uff.redes1.ipv4.Datagram;
import br.uff.redes1.ipv4.Header;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by rodcastro on 16/06/17.
 */
public class Client extends Thread {
    private Socket socket;
    private DataInputStream inputStream;

    public Client(Socket socket) {
        this.socket = socket;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException ex) {
            System.err.println("Houve um erro ao desconectar " + socket.getRemoteSocketAddress() +
                    ": " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.println("Conectado com " + socket.getRemoteSocketAddress());
        try {
            this.inputStream = new DataInputStream(socket.getInputStream());
            while (socket.isConnected()) { readNextMessage(); }
            socket.close();
        } catch (IOException ex) {
            System.err.println("Houve um erro ao receber mensagem de " + socket.getRemoteSocketAddress() +
                    ": " + ex.getMessage());
        }
        System.out.println("Desconectado de " + socket.getRemoteSocketAddress());
    }

    private void readNextMessage() {
        try {
            byte[] array = new byte[20];
            inputStream.readFully(array);
            Header header = Header.fromBytes(array);
            // Espera até ter o número de bytes do cabeçalho
            while (inputStream.available() < header.getLength()) {
                try { Thread.sleep(1); } catch (InterruptedException ex) {}
            }
            Datagram datagram = new Datagram(header, inputStream);
            System.out.println("\n" + datagram.getHeader().getSourceIp() + " Enviou uma mensagem: " +
                    datagram.toString());
        } catch (IOException ex) {
            System.err.println("Houve um erro ao receber mensagem de " + socket.getRemoteSocketAddress() +
                    ": " + ex.getMessage());
        }
    }
}
