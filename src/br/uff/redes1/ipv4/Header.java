package br.uff.redes1.ipv4;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * Essa classe representa o cabeçalho de uma requisição IPv4.
 * Os tipos de dados estão representados com um tipo primário sempre maior do que o necessário (ex: campo length tem
 * 2 bytes, mas ao invés de usar o short de Java que tem 2 bytes, usamos o int, que possui 4). O motivo dessa escolha é
 * porque em Java não é possível (até Java 7) usar variáveis unsigned, todos os tipos primários são signed, exceto o
 * char. Por isso, para facilitar, usamos um tipo com capacidade maior para evitar dar overflow e termos problemas na
 * interpretação dos dados. As conversões de e para bytes fazem um bitwise AND para contornar isso e não somar valores
 * que não deviam ser somados acidentalmente (ex, definir o campo length maior que 65535).
 *
 * Created by rodcastro on 16/06/17.
 */
public class Header {
    private int version = 4;    // IP version, always 4 because we're using IPv4
    private int ihl = 5;        // Internet Header Lenght, always 5 as options fields are not used
    private int tos = 0;        // Type of Service, not used
    private int length;         // Length of data
    private int identifier;     //
    private int flagOffset = 0; // Flags and offset, not used
    private int ttl ;           // Time To Live, initially 7
    private int upperLayer = 0;
    private int checksum = 0;
    private long sourceIp;
    private long destinationIp;

    public Header(int length, int identifier, String sourceIp, String destinationIp) {
        this.length = length;
        this.identifier = identifier;
        this.ttl = 7;
        this.sourceIp = ipStringToLong(sourceIp);
        this.destinationIp = ipStringToLong(destinationIp);
    }

    /*
     * Construtor usado apenas internamente para converter bytes em um Header
     */
    private Header(int length, int identifier, int ttl, long sourceIp, long destinationIp) {
        this.length = length;
        this.identifier = identifier;
        this.ttl = ttl;
        this.sourceIp = sourceIp;
        this.destinationIp = destinationIp;
    }

    /**
     * Converte um array de bytes em um cabeçalho IPv4. Nesse simulador, apenas os campos length, identifier, ttl,
     * sourceIp e destinationIp são utilizados, com os demais estando em valores fixos ou zero.
     *
     * @param array O array de bytes que se quer converter em um header
     * @return O novo header criado a partir do array de bytes
     */
    public static Header fromBytes(byte[] array) {
        int length = (byteArrayToShort(Arrays.copyOfRange(array, 2, 4))) & 0xffff;
        int identifier = (byteArrayToShort(Arrays.copyOfRange(array, 4, 6))) & 0xffff;
        int ttl = ((int) array[9]) & 0xff;
        long sourceIp = ((long) byteArrayToInt(Arrays.copyOfRange(array, 12, 16))) & 0xffffffffL;
        long destinationIp = ((long) byteArrayToInt(Arrays.copyOfRange(array, 16, 20))) & 0xffffffffL;
        return new Header(length, identifier, ttl, sourceIp, destinationIp);
    }

    /**
     * Converte esse Header em um array de bytes para envio através de um socket.
     *
     * @return Um array contendo 20 bytes e o conteúdo deste cabeçalho.
     */
    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(buildFirstRow());
        buffer.putInt(buildSecondRow());
        buffer.putInt(buildThirdRow());
        buffer.putInt(buildFourthRow());
        buffer.putInt(buildFifthRow());
        return buffer.array();
    }

    public int getLength() {
        return length;
    }

    public int getIdentifier() {
        return identifier;
    }

    public String getSourceIp() {
        return longToIpString(sourceIp);
    }

    public String getDestinationIp() {
        return longToIpString(destinationIp);
    }

    private int buildFirstRow() {
        long row = version & 0xff;          // 04 bits
        row += (ihl & 0xff) << 4;           // 04 bits
        row += (tos & 0xff) << 8;           // 08 bits
        row += (length & 0xffff) << 16;     // 16 bits
        return (int) row;
    }

    private int buildSecondRow() {
        long row = identifier & 0xffff;     // 16 bits
        row += (flagOffset & 0xffff) << 16; // 16 bits (3 + 13)
        return (int) row;
    }

    private int buildThirdRow() {
        long row = ttl & 0xff;              // 08 bits
        row += (upperLayer & 0xff) << 8;    // 08 bits
        row += (checksum & 0xffff) << 16;   // 16 bits
        return (int) row;
    }

    private int buildFourthRow() {
        return (int) sourceIp;
    }

    private int buildFifthRow() {
        return (int) destinationIp;
    }

    /**
     * Converte uma string de IP em sua representação em bytes, no formato long.
     *
     * @param ip A string de IP
     * @return A representação em bytes no formato long.
     */
    private long ipStringToLong(String ip) {
        String[] pieces = ip.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result += Long.parseLong(pieces[i]) << (i * 8);
        }
        return result;
    }

    private String longToIpString(long ip) {
        String[] pieces = new String[4];
        pieces[3] = ((ip & 0xff000000L) >> 24) + "";
        pieces[2] = ((ip & 0x00ff0000L) >> 16) + "";
        pieces[1] = ((ip & 0x0000ff00L) >> 8) + "";
        pieces[0] = (ip & 0x000000ffL) + "";
        return String.join(".", pieces);
    }

    private static int byteArrayToShort(byte[] array) {
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getShort();
    }

    private static int byteArrayToInt(byte[] array) {
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getInt();
    }
}
