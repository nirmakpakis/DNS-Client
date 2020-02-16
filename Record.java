import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class Record {

    public byte[] response;
    public int offset;
    public boolean AA;
    public QType questionQtype;

    // Will be parsed
    public String name;
    public QType qType;
    public byte[] qClass = new byte[2];
    public int ttl;
    public int RDlength;
    public String domainName;
    public RData rData;

    // For MX
    public short mxPreference;

    // Length of the record
    public int byteLength;

    public Record(byte[] response, int offset, boolean AA, QType questionQtype) {

        this.response = response;
        this.offset = offset;
        this.AA = AA;
        this.questionQtype = questionQtype;
        this.parseRecord();
    }

    public void parseRecord() {

        // Get Name
        int index = this.offset;
        RData domainRData = getRData(index);
        this.name = domainRData.domain;
        index += domainRData.bytes;

        // Get Type
        byte[] ans_type = new byte[2];
        ans_type[0] = response[index];
        ans_type[1] = response[index + 1];
        getQtype(ans_type);

        index += 2;

        // Get class
        byte[] queryClass = new byte[2];
        queryClass[0] = response[index];
        queryClass[1] = response[index + 1];
        if (qClass[0] != 0 && qClass[1] != 1) {
            throw new RuntimeException(("Error:Class field should store 1"));
        }
        this.qClass = queryClass;

        index += 2;

        // Get time to live
        byte[] timeToLive = { response[index], response[index + 1], response[index + 2], response[index + 3] };
        ByteBuffer wrapped = ByteBuffer.wrap(timeToLive);
        this.ttl = wrapped.getInt();

        index += 4;

        // get RDLength
        byte[] RDLength = { response[index], response[index + 1] };
        wrapped = ByteBuffer.wrap(RDLength);
        this.RDlength = wrapped.getShort();

        index += 2;

        // Get RData

        switch (this.qType) {
        case A:
            this.domainName = getTypeA(this.RDlength, index);
            break;
        case NS:
            this.domainName = getTypeNS(this.RDlength, index);
            break;
        case MX:
            this.domainName = getTypeMX(this.RDlength, index);
            break;
        case CName:
            this.domainName = getTypeCName(this.RDlength, index);
            break;
        case Other:
            break;
        }

        // Update index

        this.byteLength = index + this.RDlength - this.offset;
    }

    private String getTypeA(int rdLength, int index) {
        String address = "";
        byte[] byteAddress = { response[index], response[index + 1], response[index + 2], response[index + 3] };
        try {
            InetAddress i_add = InetAddress.getByAddress(byteAddress);
            address = i_add.toString().substring(1);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return address;

    }

    private String getTypeNS(int rdLength, int index) {
        RData result = getRData(index);
        String name = result.domain;
        return name;
    }

    private String getTypeMX(int rdLength, int index) {
        byte[] mxPreference = { this.response[index], this.response[index + 1] };
        ByteBuffer buffer = ByteBuffer.wrap(mxPreference);
        this.mxPreference = buffer.getShort();
        return getRData(index + 2).domain;
    }

    private String getTypeCName(int rdLength, int index) {
        RData result = getRData(index);
        String cname = result.domain;
        return cname;
    }

    private RData getRData(int index) {
        RData result = new RData();
        int wordSize = response[index];
        String domainName = "";
        boolean start = true;
        int count = 0;
        while (wordSize != 0) {
            if (!start) {
                domainName += ".";
            }
            if ((wordSize & 0xC0) == (int) 0xC0) {
                byte[] offset = { (byte) (response[index] & 0x3F), response[index + 1] };
                ByteBuffer wrapped = ByteBuffer.wrap(offset);
                domainName += getRData(wrapped.getShort()).domain;
                index += 2;
                count += 2;
                wordSize = 0;
            } else {
                domainName += getWordFromIndex(index);
                index += wordSize + 1;
                count += wordSize + 1;
                wordSize = response[index];
            }
            start = false;
        }
        result.domain = domainName;
        result.bytes = count;
        return result;
    }

    private String getWordFromIndex(int index) {
        String word = "";
        int wordSize = response[index];
        for (int i = 0; i < wordSize; i++) {
            word += (char) response[index + i + 1];
        }
        return word;
    }

    private void getQtype(byte[] qType) {
        int type = qType[1];
        switch (type) {
        case 1:
            this.qType = QType.A;
            break;
        case 2:
            this.qType = QType.NS;
            break;
        case 15:
            this.qType = QType.MX;
            break;
        case 5:
            this.qType = QType.CName;
            break;
        default:
            this.qType = QType.Other;
            break;
        }
    }

}