package br.uff.redes1;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Created by rodcastro on 16/06/17.
 */
public class CommandLineInterface {
    private int state;
    private NetworkSimulator simulator;
    private Scanner scanner;

    public static final int STATE_STARTUP = 0;
    public static final int STATE_GET_ACTION = 1;
    public static final int STATE_CONNECT = 2;
    public static final int STATE_SEND_MESSAGE = 3;
    public static final int STATE_FINISHED = 4;

    public static final int ACTION_CONNECT = 1;
    public static final int ACTION_PRINT_CONNECTED = 2;
    public static final int ACTION_SEND = 3;

    public CommandLineInterface(NetworkSimulator simulator) {
        this.state = 0;
        this.simulator = simulator;
        this.scanner = new Scanner(System.in);
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
        printActionList();
        int action = getNextAction();
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
            default:
                System.out.println("Opção inválida. Por favor selecione um número da lista.");
        }
    }

    private void getTargetToConnect() {

    }

    private void getTargetToSend() {

    }

    private void getMessageToSend() {

    }

    private void printActionList() {
        System.out.println("\nO que você quer fazer? (digite o número da opção)");
        System.out.println("  1) Conectar a um vizinho");
        System.out.println("  2) Listar vizinhos conectados");
        System.out.println("  3) Enviar mensagem a um vizinho");
        System.out.println("  4) Terminar a execução");
    }

    private void printAllNeighbours() {
        for (Host neighbour : simulator.getNeighbours()) {
            System.out.println(neighbour.toString());
        }
    }

    private int getNextAction() {
        int action = 0;
        try {
            action = Integer.parseInt(scanner.next());
        } catch (NumberFormatException ex) {
            System.out.println("Opção inválida. Por favor digite apenas números.");
        }
        return action;
    }

    private void printConnectedNeighbours() {
        boolean anyConnected = false;
        for (Host neighbour : simulator.getNeighbours()) {
            if (neighbour.isConnected()) {
                System.out.println(neighbour.toString());
                anyConnected = true;
            }
        }
        if (!anyConnected) {
            System.out.println("Não há vizinhos conectados.");
        }
    }
}
