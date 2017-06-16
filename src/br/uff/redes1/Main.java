package br.uff.redes1;

public class Main {
    public static void main(String[] args) {
        if (!validateArguments(args)) {
            printHelp();
            return;
        }

	    int listenerPort = Integer.parseInt(args[0]);
        String neighboursFilePath = args[1];

        NetworkSimulator simulator = new NetworkSimulator(listenerPort, neighboursFilePath);
        simulator.start();
    }

    private static boolean validateArguments(String[] args) {
        return args.length == 2;
    }

    private static void printHelp() {
        System.out.println("Parâmetros errados. Especifique a porta e o arquivo de vizinhos.");
    }
}
