package br.uff.redes1.ipv4;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rodcastro on 16/06/17.
 */
public class Datagram {
    private String message;
    private Header header;

    public Datagram(String message, String sourceIp, String destinationIp) {
        this.message = message;
        this.header = new Header(message.getBytes().length, 1, sourceIp, destinationIp);
    }

    public Datagram(Header header, InputStream in) {
        this.header = header;
        try {
            String message = "";
            while (in.available() > 0) {
                message += (char) in.read();
            }
            this.message = message;
        } catch (IOException ioex) {
            System.err.println("Erro ao receber mensagem: " + ioex.getMessage());
        }
    }

    public byte[] getBytes() {
        byte[] messageBytes = message.getBytes();
        byte[] headerBytes = header.getBytes();
        byte[] bytes = new byte[headerBytes.length + messageBytes.length];
        for (int i = 0; i < headerBytes.length; i++) {
            bytes[i] = headerBytes[i];
        }
        for (int i = 0; i < messageBytes.length; i++) {
            bytes[headerBytes.length + i] = messageBytes[i];
        }
        return bytes;
    }

    public Header getHeader() {
        return header;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return message;
    }
}
