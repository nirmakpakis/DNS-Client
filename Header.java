import java.nio.ByteBuffer;
import java.util.Random;

public class Header {

    public ByteBuffer header = ByteBuffer.allocate(12);
    public byte[] headerArray;
    public int ID;
    public boolean QR;
    public boolean AA;
    public boolean TC;
    public boolean RD;
    public boolean RA;
    public int RCode;
    public int QDCount;
    public int ANCount;
    public int NSCount;
    public int ARCount;

    public Header() {
    }

    public Header(byte[] response) {
        parseHeader(response);
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

    public void parseHeader(byte[] response) {

        // ID
        this.ID = getInt(response[0], response[1]);

        // QR
        this.QR = toBoolean((response[2] >> 7) & 1);

        // AA
        this.AA = toBoolean((response[2] >> 2) & 1);

        // TC
        this.TC = toBoolean((response[2] >> 1) & 1);

        // RD
        this.RD = toBoolean((response[2] >> 0) & 1);

        // RA
        this.RA = toBoolean((response[3] >> 7) & 1);

        // RCODE
        this.RCode = response[3] & 0x0F;

        // QDCount;
        this.QDCount = getInt(response[4], response[5]);

        // ANCount
        this.ANCount = getInt(response[6], response[7]);

        // NSCount
        this.NSCount = getInt(response[8], response[9]);

        // ARCount
        this.ARCount = getInt(response[10], response[11]);

    }

    public int getInt(byte byte1, byte byte2) {
        byte[] temp = new byte[2];
        temp[0] = byte1;
        temp[1] = byte2;
        ByteBuffer wrapped = ByteBuffer.wrap(temp);
        return wrapped.getShort();
    }

    public boolean toBoolean(int i) {
        if (i == 1)
            return true;
        return false;
    }

}