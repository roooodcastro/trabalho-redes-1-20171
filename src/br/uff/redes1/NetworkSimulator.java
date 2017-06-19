package br.uff.redes1;

import br.uff.redes1.server.Neighbour;
import br.uff.redes1.server.Listener;
import br.uff.redes1.ui.CommandLineInterface;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by rodcastro on 16/06/17.
 */
public class NetworkSimulator {
    private int listenerPort;
    private File neighboursFile;
    private File interfacesFile;
    private CommandLineInterface cli;
    private List<Neighbour> neighbours;
    private Listener listener;

    public NetworkSimulator(int listenerPort, String neighboursFilePath, String interfacesFilePath) {
        this.listenerPort = listenerPort;
        this.neighboursFile = new File(neighboursFilePath);
        this.interfacesFile = new File(interfacesFilePath);
        this.cli = new CommandLineInterface(this);
        this.neighbours = new ArrayList<Neighbour>();
    }

    public void start() {
        loadNeighbours(); // Load neighbours
        loadInterfaces(); // Assign simulated interface addresses to them
        startListener();
        cli.start();
        for (Neighbour neighbour : neighbours) {
            if (neighbour.isConnected()) neighbour.disconnect();
        }
        stopListener();
    }

    public Neighbour getNeighbour(int index) {
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
    public int connectNeighbour(Neighbour neighbour) {
        if (neighbour == null) return -2;
        if (neighbour.isConnected()) return -1;
        neighbour.connect();
        return neighbour.isConnected() ? 0 : 1;
    }

    public List<Neighbour> getNeighbours() {
        return neighbours;
    }

    public List<Neighbour> getConnectedNeighbours() {
        List<Neighbour> connectedNeighbours = new ArrayList<>(neighbours);
        Predicate<Neighbour> neighbourPredicate = h-> !h.isConnected();
        connectedNeighbours.removeIf(neighbourPredicate);
        return connectedNeighbours;
    }

    public String[] getNeighboursNames() {
        return getNeighboursNames(neighbours);
    }

    public String[] getConnectedNeighboursNames() {
        return getNeighboursNames(getConnectedNeighbours());
    }

    public String[] getNeighboursNames(List<Neighbour> neighbours) {
        String[] names = new String[neighbours.size()];
        for (int i = 0; i < neighbours.size(); i++) {
            names[i] = neighbours.get(i).toString();
        }
        return names;
    }

    private void startListener() {
        // Abrir socket TCP na porta do receiver (em outra thread)
        this.listener = new Listener(listenerPort);
        listener.start();
    }

    private void stopListener() {
        listener.close();
    }

    private void loadNeighbours() {
        try {
            List<String[]> lines = readFile(neighboursFile);
            for (String[] parts : lines) {
                neighbours.add(new Neighbour(parts[0], Integer.parseInt(parts[1])));
            }
        } catch (NumberFormatException nfex) {
            System.err.println("Houve um erro na leitura do arquivo \"" + neighboursFile.getAbsolutePath() +
                    "\". Verifique se as definições das portas estão corretas.");
        }
    }

    private void loadInterfaces() {
        List<String[]> lines = readFile(interfacesFile);
        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i);
            Neighbour neighbour = neighbours.get(i);
            neighbour.setAddress(parts[0]);
            neighbour.setSubnetMask(parts[1]);
        }
    }

    private List<String[]> readFile(File file) {
        List<String[]> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line.split(" "));
            }
            return lines;
        } catch (FileNotFoundException fnfex) {
            System.err.println("O arquivo \"" + file.getAbsolutePath() + "\" não foi encontrado");
        } catch (IOException ioex) {
            System.err.println("Houve um erro ao tentar abrir o arquivo \"" + file.getAbsolutePath() +
                    "\" para leitura");
        }
        return new ArrayList<>(); // Se não conseguiu ler, retorna uma lista vazia. Nada será processado.
    }
}
