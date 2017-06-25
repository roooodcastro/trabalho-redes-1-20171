package br.uff.redes1;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

public class Main {
    public static void main(String[] args) {
        if (!validateArguments(args)) {
            printHelp();
            return;
        }

        int listenerPort = Integer.parseInt(args[0]);
        String neighboursFilePath = args[1];
        String interfacesFilePath = args[2];
        String routingFilePath = args[3];

        NetworkSimulator simulator = new NetworkSimulator(listenerPort, neighboursFilePath, interfacesFilePath,
                routingFilePath);
        simulator.start();
    }

    /**
     * Verifica se o número de parâmetros está certo e se o primeiro e segundo arquivos têm o mesmo número de linhas.
     * @param args Os argumentos de execução do programa
     * @return Se os argumentos são válidos
     */
    private static boolean validateArguments(String[] args) {
        if (args.length != 3) { return false; }
        return countLinesInFile(args[1]) == countLinesInFile(args[2]);
    }

    private static void printHelp() {
        System.out.println("Parâmetros errados. Especifique a porta e o arquivo de vizinhos.");
    }

    private static int countLinesInFile(String filepath) {
        int numberOfLines = -1;
        try {
            LineNumberReader lnr = new LineNumberReader(new FileReader(new File(filepath)));
            lnr.skip(Long.MAX_VALUE);
            numberOfLines = lnr.getLineNumber() + 1; //Add 1 because line index starts at 0
            lnr.close();
            return numberOfLines;
        } catch (IOException ex) {
            System.err.println("Não foi possível verificar o arquivo \"" + filepath + "\"");
        }
        return numberOfLines;
    }
}
