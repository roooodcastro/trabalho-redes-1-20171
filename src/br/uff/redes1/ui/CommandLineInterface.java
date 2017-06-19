package br.uff.redes1.ui;

import br.uff.redes1.server.Neighbour;
import br.uff.redes1.NetworkSimulator;

import java.util.Scanner;

/**
 * Created by rodcastro on 16/06/17.
 */
public class CommandLineInterface {
    private int state;
    private NetworkSimulator simulator;
    private Scanner scanner;
    private CommandLineMenu actionsMenu;

    private String[] actionLabels = new String[] {
            "Conectar a um vizinho",
            "Listar vizinhos conectados",
            "Enviar mensagem a um vizinho",
            "Terminar a execução"
    };

    public static final int STATE_STARTUP = 0;
    public static final int STATE_GET_ACTION = 1;
    public static final int STATE_CONNECT = 2;
    public static final int STATE_SEND_MESSAGE = 3;
    public static final int STATE_FINISHED = 4;

    public static final int ACTION_CONNECT = 1;
    public static final int ACTION_PRINT_CONNECTED = 2;
    public static final int ACTION_SEND = 3;
    public static final int ACTION_TERMINATE = 4;

    public CommandLineInterface(NetworkSimulator simulator) {
        this.state = 0;
        this.simulator = simulator;
        this.scanner = new Scanner(System.in);
        this.actionsMenu = new CommandLineMenu("O que você quer fazer?", actionLabels, false);
    }

    public boolean isFinished() {
        return state == STATE_FINISHED;
    }

    public void start() {
        while(!isFinished()) {
            switch(state) {
                case STATE_STARTUP:
                    printStartMessages();
                    break;
                case STATE_GET_ACTION:
                    processNextAction();
                    break;
                case STATE_CONNECT:
                    getTargetToConnect();
                    break;
                case STATE_SEND_MESSAGE:
                    getMessageToSend();
            }
        }
    }

    private void printStartMessages() {
        System.out.println("Hello user!");
        this.state = STATE_GET_ACTION;
    }

    private void processNextAction() {
        int action = actionsMenu.show();
        switch(action) {
            case ACTION_CONNECT:
                this.state = STATE_CONNECT;
                break;
            case ACTION_PRINT_CONNECTED:
                printConnectedNeighbours();
                break;
            case ACTION_SEND:
                this.state = STATE_SEND_MESSAGE;
                break;
            case ACTION_TERMINATE:
                this.state = STATE_FINISHED;
                System.out.println("Fechando conexões abertas e saindo...");
                break;
        }
    }

    /**
     * Pede ao usuário qual vizinho ele quer se conectar, e tenta a conexão.
     */
    private void getTargetToConnect() {
        // Exibe o menu com os vizinhos para serem selecionados
        CommandLineMenu neighboursMenu = new CommandLineMenu("Selecione um vizinho para se conectar",
                simulator.getNeighboursNames(), true);
        int result = neighboursMenu.show();
        if (result == -1) return; // Não fazemos nada se o usuário digitou qualquer coisa (continua tentando)
        if (result > 0) { // Se ele cancelou (opção 0), não tenta conectar e só muda o estado e volta ao menu principal
            Neighbour neighbour = simulator.getNeighbour(result - 1); // Diminuímos 1 pq as opções do menu começam em 1
            connectNeighbour(neighbour);
        }
        this.state = STATE_GET_ACTION; // Depois de conectar, volta ao menu principal
    }

    /**
     * Requere a conexão com o vizinho ao simulador e imprime o resultado de acordo com o código retornado pelo
     * simulador.
     *
     * @param neighbour O vizinho que se quer conectar.
     */
    private void connectNeighbour(Neighbour neighbour) {
        int result = simulator.connectNeighbour(neighbour);
        switch(result) {
            case 0:
                System.out.println(neighbour.toString() + " conectado");
                break;
            case 1:
                System.out.println("Não foi possível se conectar ao " + neighbour.toString());
                break;
            case -1:
                System.out.println(neighbour.toString() + " já está conectado");
                break;
            case -2:
                System.out.println(neighbour.toString() + " não existe!");
                break;
        }
    }

    private void getMessageToSend() {
        CommandLineMenu neighboursMenu = new CommandLineMenu("Selecione um vizinho para enviar a mensagem",
                simulator.getConnectedNeighboursNames(), true);
        int result = neighboursMenu.show();
        if (result == -1) return; // Não fazemos nada se o usuário digitou qualquer coisa (continua tentando)
        if (result > 0) { // Se ele cancelou (opção 0), não tenta enviar e só muda o estado e volta ao menu principal
            Neighbour neighbour = simulator.getNeighbour(result - 1); // Diminuímos 1 pq as opções do menu começam em 1
            readAndSendMessage(neighbour);
        }
        this.state = STATE_GET_ACTION;
    }

    private void printConnectedNeighbours() {
        boolean anyConnected = false;
        for (Neighbour neighbour : simulator.getNeighbours()) {
            if (neighbour.isConnected()) {
                System.out.println(neighbour.toString());
                anyConnected = true;
            }
        }
        if (!anyConnected) {
            System.out.println("Não há vizinhos conectados.");
        }
    }

    private void readAndSendMessage(Neighbour neighbour) {
        System.out.printf("Escreva sua mensagem > ");
        if (neighbour.sendMessage(scanner.nextLine())) {
            System.out.println("Mensagem enviada para " + neighbour.toString());
        }
    }
}
