package br.uff.redes1.ipv4;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rodcastro on 16/06/17.
 */
public class Datagram {
    private String message;

    public Datagram(String message) {
        this.message = message;
    }

    public byte[] getBytes() {
        return message.getBytes();
    }

    public static Datagram fromInputStream(InputStream in) {
        try {
            String message = "";
            while (in.available() > 0) {
                message += (char) in.read();
            }
            in.close();
            return new Datagram(message);
        } catch (IOException ioex) {
            System.err.println("Erro ao receber mensagem: " + ioex.getMessage());
        }
        return null;
    }

    public String toString() {
        return message;
    }
}
