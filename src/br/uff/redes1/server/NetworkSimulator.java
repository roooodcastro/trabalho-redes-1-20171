package br.uff.redes1.server;

import br.uff.redes1.FileUtil;
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
    private File routingFile;
    private CommandLineInterface cli;
    private List<Neighbour> neighbours;
    private Listener listener;

    /**
     * Cria uma nova instância do simulador de redes
     * @param listenerPort A porta por onde o Listener vai receber pacotes de vizinhos
     * @param neighboursFilePath O caminho para o arquivo de vizinhos
     * @param interfacesFilePath O caminho para o arquivo de interfaces
     * @param routingFilePath O caminho para o arquivo de roteamento
     */
    public NetworkSimulator(int listenerPort, String neighboursFilePath, String interfacesFilePath,
                            String routingFilePath) {
        this.listenerPort = listenerPort;
        this.neighboursFile = new File(neighboursFilePath);
        this.interfacesFile = new File(interfacesFilePath);
        this.routingFile = new File(routingFilePath);
        this.cli = new CommandLineInterface(this);
        this.neighbours = new ArrayList<Neighbour>();
    }

    /**
     * Tenta initiar o simulador. Carrega os arquivos, e caso o arquivo de roteamento esteja válido, inicia o Listener e
     * a interface de linha de comando.
     *
     * A CLI roda na thread principal do programa, logo, quando o método cli.start() retornar, significa que o usuário
     * decidiu terminar a execução. Então fechamos todas as conexões com os vizinhos e paramos o Listener.
     *
     * Caso o arquivo de roteamento tenha alguma inconsistência, não inicia nada e retorna sem executar o programa.
     * Uma mensagem será exibida para o usuário posteriormente informando o motivo
     */
    public void start() {
        loadNeighbours();   // Load neighbours
        loadInterfaces();   // Assign simulated interface addresses to them
        if (startListener() ) {
            cli.start();
            for (Neighbour neighbour : neighbours) {
                if (neighbour.isConnected()) neighbour.disconnect();
            }
            stopListener();
        }
    }

    public List<Neighbour> getNeighbours() {
        return neighbours;
    }

    public Listener getListener() {
        return listener;
    }

    public List<Neighbour> getAvailableDestinations() {
        return listener.getRouter().getAvailableHosts();
    }

    public String[] getAvailableDestinationsNames() {
        return getNeighboursNames(listener.getRouter().getAvailableHosts());
    }

    /**
     * Retorna um array de strings com os nomes (IP, máscara e porta) dos vizinhos passados no parâmetro.
     */
    public String[] getNeighboursNames(List<Neighbour> neighbours) {
        String[] names = new String[neighbours.size()];
        for (int i = 0; i < neighbours.size(); i++) {
            names[i] = neighbours.get(i).toString();
        }
        return names;
    }

    /**
     * Tenta iniciar o Listener. Caso o arquivo de roteamento não seja válido, o Listener não será iniciado.
     * @return True se o listener for iniciado, ou false se não for.
     */
    private boolean startListener() {
        // Abrir socket TCP na porta do receiver (em outra thread)
        this.listener = new Listener(listenerPort, neighbours, routingFile);
        if (listener.isValid()) {
            listener.start();
        }
        return listener.isValid();
    }

    private void stopListener() {
        listener.close();
    }

    /**
     * Carrega o arquivo de vizinhos, preenchendo uma lista de vizinhos com os vizinhos recém-criados
     */
    private void loadNeighbours() {
        try {
            List<String[]> lines = FileUtil.readFile(neighboursFile);
            for (String[] parts : lines) {
                neighbours.add(new Neighbour(parts[0], Integer.parseInt(parts[1])));
            }
        } catch (NumberFormatException nfex) {
            System.err.println("Houve um erro na leitura do arquivo \"" + neighboursFile.getAbsolutePath() +
                    "\". Verifique se as definições das portas estão corretas.");
        }
    }

    /**
     * Carrega o arquivo de interfaces e percorre a lista de vizinhos completando as informações em cada um deles.
     */
    private void loadInterfaces() {
        List<String[]> lines = FileUtil.readFile(interfacesFile);
        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i);
            Neighbour neighbour = neighbours.get(i);
            neighbour.setAddress(parts[0]);
            neighbour.setSubnetMask(parts[1]);
        }
    }
}
