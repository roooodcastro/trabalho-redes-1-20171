package br.uff.redes1;

public class Main {
    public static void main(String[] args) {
        if (!validateArguments(args)) {
            printHelp();
            return;
        }

	    int listenerPort = Integer.parseInt(args[0]);
        String neighbourListFile = args[1];

        // Abrir socket TCP na porta do receiver (em outra thread)
        Client client = new Client(listenerPort);
        client.start();

        // Interagir com o usuário e perguntar oq ele quer fazer
        
    }

    private static boolean validateArguments(String[] args) {
        return args.length == 2;
    }

    private static void printHelp() {
        System.out.println("Parâmetros errados. Especifique a porta e o arquivo de vizinhos.");
    }
}
