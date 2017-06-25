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
            "Listar vizinhos conectados",
            "Enviar mensagem a um vizinho",
            "Terminar a execução"
    };

    public static final int STATE_STARTUP = 0;
    public static final int STATE_GET_ACTION = 1;
    public static final int STATE_CONNECT = 2;
    public static final int STATE_SEND_MESSAGE = 3;
    public static final int STATE_FINISHED = 4;

    public static final int ACTION_PRINT_CONNECTED = 1;
    public static final int ACTION_SEND = 2;
    public static final int ACTION_TERMINATE = 3;

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

    private void getMessageToSend() {
        CommandLineMenu neighboursMenu = new CommandLineMenu("Selecione um vizinho para enviar a mensagem",
                simulator.getAvailableDestinationsNames(), true);
        int result = neighboursMenu.show();
        if (result == -1) return; // Não fazemos nada se o usuário digitou qualquer coisa (continua tentando)
        if (result > 0) { // Se ele cancelou (opção 0), não tenta enviar e só muda o estado e volta ao menu principal
            Neighbour neighbour = simulator.getAvailableDestinations().get(result - 1);
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

    private void readAndSendMessage(Neighbour destinationNeighbour) {
        System.out.printf("Escreva sua mensagem > ");
        Neighbour nextJump = simulator.getListener().getRouter().findNextJump(destinationNeighbour.getAddress());
        if (nextJump.sendMessage(scanner.nextLine(), destinationNeighbour.getAddress())) {
            System.out.println("Mensagem enviada para " + destinationNeighbour.toString());
        }
    }
}
