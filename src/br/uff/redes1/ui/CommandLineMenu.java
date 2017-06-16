package br.uff.redes1.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by rodcastro on 16/06/17.
 */
public class CommandLineMenu {
    private List<String> options;
    private String title;
    private Scanner scanner;
    private boolean showCancel;

    public CommandLineMenu(String title, String[] options, boolean showCancel) {
        this.options = Arrays.asList(options);
        this.title = title;
        this.scanner = new Scanner(System.in);
        this.showCancel = showCancel;
    }

    /**
     * Imprime o menu de opções no terminal e espera a resposta do usuário com scanner.readLine().
     * Além das opções fornecidas, também é mostrada uma opção para cancelar caso tenha sido requisitada.
     *
     * @return O índice das opções escolhidas pelo usuário. Retorna -1 caso o usuário digite um valor inválido ou caso
     * ele cancele o menu.
     */
    public int show() {
        System.out.println("\n" + title);
        printOptions();
        return readAnswer();
    }

    private void printOptions() {
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ") " + options.get(i));
        }
        if (showCancel) {
            System.out.println("0) Cancelar");
        }
    }

    private int readAnswer() {
        System.out.printf("Selecione um número > ");
        int answer = -1;
        try {
            answer = Integer.parseInt(scanner.next());
            if ((!showCancel && answer <= 0) || (showCancel && answer < 0) || answer > options.size()) {
                answer = -1;
                System.err.println("Opção inválida. Por favor selecione um número da lista.");
            }
        } catch (NumberFormatException ex) {
            System.err.println("Opção inválida. Por favor digite apenas números.");
        }
        System.out.println("");
        return answer;
    }
}
