package br.uff.redes1;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rodcastro on 16/06/17.
 */
public class NetworkSimulator {
    private int listenerPort;
    private File neighboursFile;
    private CommandLineInterface cli;
    private List<Host> neighbours;

    public NetworkSimulator(int listenerPort, String neighboursFilePath) {
        this.listenerPort = listenerPort;
        this.neighboursFile = new File(neighboursFilePath);
        this.cli = new CommandLineInterface(this);
        this.neighbours = new ArrayList<Host>();
    }

    public void start() {
        startListener();
        cli.start();
    }

    public List<Host> getNeighbours() {
        return neighbours;
    }

    private void startListener() {
        // Abrir socket TCP na porta do receiver (em outra thread)
        Client client = new Client(listenerPort);
        client.start();
    }

    private void loadNeighbours() {

    }
}
