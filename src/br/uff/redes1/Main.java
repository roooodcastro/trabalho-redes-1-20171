package br.uff.redes1;

import br.uff.redes1.server.NetworkSimulator;

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

        // Pega e nomeia os argumentos
        int listenerPort = Integer.parseInt(args[0]);
        String neighboursFilePath = args[1];
        String interfacesFilePath = args[2];
        String routingFilePath = args[3];

        // Instancia um simulador e tenta iniciá-lo (caso o arquivo de roteamento seja válido)
        NetworkSimulator simulator = new NetworkSimulator(listenerPort, neighboursFilePath, interfacesFilePath,
                routingFilePath);

        simulator.start();
        if (!simulator.getListener().isValid()) {
            System.err.println("Não foi possível iniciar o pois o arquivo de roteamento contém entradas inválidas");
        }
    }

    /**
     * Verifica se o número de parâmetros está certo e se o primeiro e segundo arquivos têm o mesmo número de linhas.
     * @param args Os argumentos de execução do programa
     * @return Se os argumentos são válidos
     */
    private static boolean validateArguments(String[] args) {
        if (args.length != 4) { return false; }
        return countLinesInFile(args[1]) == countLinesInFile(args[2]);
    }

    private static void printHelp() {
        System.out.println("Parâmetros errados. Especifique a porta e os arquivos corretamente.");
    }

    /**
     * Conta e retorna o número de linhas em um arquivo
     */
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
