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
    private Listener listener;
    private DataInputStream inputStream;

    public Client(Listener listener, Socket socket) {
        this.listener = listener;
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
            while (socket.isConnected()) { readNextDatagram(); }
            socket.close();
        } catch (IOException ex) {
            if (ex.getMessage() != null) {
                System.err.println("Houve um erro ao receber mensagem de " + socket.getRemoteSocketAddress() +
                        ": " + ex.getMessage());
            }
        }
        System.out.println("Desconectado de " + socket.getRemoteSocketAddress());
    }

    private void readNextDatagram() throws IOException {
        byte[] array = new byte[20];
        inputStream.readFully(array);
        Header header = Header.fromBytes(array);
        // Espera até ter o número de bytes do cabeçalho
        while (inputStream.available() < header.getLength()) {
            try { Thread.sleep(1); } catch (InterruptedException ex) {}
        }
        processDatagram(new Datagram(header, inputStream));
    }

    private void processDatagram(Datagram datagram) {
        Router router = listener.getRouter();
        if (router.isFinalDestination(datagram)) {
            // Display message to user and be done with it
            System.out.println("\n" + datagram.getHeader().getSourceIp() + " Enviou uma mensagem: " +
                    datagram.toString());
        } else {
            Neighbour nextJump = router.findNextJump(datagram);
            if (nextJump != null) {
                System.out.println("Redirecionando mensagem de " + datagram.getHeader().getSourceIp() + " para " +
                        nextJump.getAddress() + " (destino final: " + datagram.getHeader().getDestinationIp() + ")");
                if (datagram.decrementTtl()) {
                    nextJump.sendMessage(datagram);
                } else {
                    System.err.println("Não foi possível redirecionar pacote pois seu TTL chegou a zero");
                }
            } else {
                System.err.println("Não foi possível encontrar uma rota para " +
                        datagram.getHeader().getDestinationIp());
            }
        }
    }
}
