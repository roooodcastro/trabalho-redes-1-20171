package br.uff.redes1;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rodcastro on 25/06/17.
 */
public class FileUtil {
    public static List<String[]> readFile(File file) {
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
