package br.uff.redes1;

import br.uff.redes1.ui.CommandLineInterface;

import java.io.*;
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
    private Client client;

    public NetworkSimulator(int listenerPort, String neighboursFilePath) {
        this.listenerPort = listenerPort;
        this.neighboursFile = new File(neighboursFilePath);
        this.cli = new CommandLineInterface(this);
        this.neighbours = new ArrayList<Host>();
    }

    public void start() {
        loadNeighbours();
        startListener();
        cli.start();
        for (Host neighbour : neighbours) {
            if (neighbour.isConnected()) neighbour.disconnect();
        }
        stopListener();
    }

    public Host getNeighbour(int index) {
        return neighbours.get(index);
    }

    /**
     * Tenta conectar a um vizinho. Apenas tenta a conexão caso o vizinho ainda não esteja conectado.
     *
     * @param neighbour O vizinho a ser conectado
     * @return Um código de acordo com o resultado da conexão.
     *  -2 significa que o vizinho passado não existe (é nulo)
     *  -1 significa que o vizinho passado já está conectado
     *  0  significa que o vizinho foi conectado corretamente
     *  1  significa que o vizinho não pode ser conectado (um erro ocorreu)
     */
    public int connectNeighbour(Host neighbour) {
        if (neighbour == null) return -2;
        if (neighbour.isConnected()) return -1;
        neighbour.connect();
        return neighbour.isConnected() ? 0 : 1;
    }

    public List<Host> getNeighbours() {
        return neighbours;
    }

    public String[] getNeighboursNames() {
        String[] names = new String[neighbours.size()];
        for (int i = 0; i < neighbours.size(); i++) {
            names[i] = neighbours.get(i).toString();
        }
        return names;
    }

    private void startListener() {
        // Abrir socket TCP na porta do receiver (em outra thread)
        this.client = new Client(listenerPort);
        client.start();
    }

    private void stopListener() {
        client.close();
    }

    private void loadNeighbours() {
        try (BufferedReader br = new BufferedReader(new FileReader(neighboursFile))) {
            String neighbourLine;
            while ((neighbourLine = br.readLine()) != null) {
                String[] parts = neighbourLine.split(" ");
                neighbours.add(new Host(parts[0], Integer.parseInt(parts[1])));
            }
        } catch (FileNotFoundException fnfex) {
            System.err.println("O arquivo \"" + neighboursFile.getAbsolutePath() + "\" não foi encontrado");
        } catch (IOException ioex) {
            System.err.println("Houve um erro ao tentar abrir o arquivo \"" + neighboursFile.getAbsolutePath() +
                    "\" para leitura");
        } catch (NumberFormatException nfex) {
            System.err.println("Houve um erro na leitura do arquivo \"" + neighboursFile.getAbsolutePath() +
                    "\". Verifique se as definições das portas estão corretas.");
        }
    }
}
