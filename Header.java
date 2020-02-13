import java.nio.ByteBuffer;
import java.util.Random;

public class Header {

    public ByteBuffer header = ByteBuffer.allocate(12);
    public byte[] headerArray;
    public byte[] ID = new byte[2];
    public boolean QR;
    public int OPcode = 0;
    public boolean AA;
    public boolean TC;
    public boolean RD;
    public boolean RA;
    public int Z = 1;
    public int DCount; // 2 bytes
    public int ANCount; // 2 bytes
    public int ARCount; // 2 bytes

    public Header() {
    }

    public byte[] createHeaderArray() {

        // Create Random ID
        byte[] randomID = new byte[2];
        new Random().nextBytes(randomID);
        header.put(randomID);

        // Set RD to 1
        byte current = header.get(2);
        current |= 1 << 0;
        header.put(2, current);

        // Set QDCount
        header.put(5, (byte) 0x01);

        // Set byte array
        return header.array();
    }

}