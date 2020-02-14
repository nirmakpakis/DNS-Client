import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.net.UnknownHostException;

public class Answer {

    public byte[] response;
    public int requestLenth;
    public int offset;

    public Answer(byte response[], int requestLenth) {
        this.response = response;
    }

    public Record[] getAnswerRecords(int numOfRecords) {
        Record[] records = new Record[numOfRecords];
        int offSet1 = requestLenth;
        for (int i = 0; i < numOfRecords; i++) {
            records[i] = this.parseAnswer(offSet1);
            offSet1 += records[i].byteLength;
        }
        this.offset = offSet1;
        return records;
    }

    public Record parseAnswer(int index) {
        Record record = new Record();

        String domain = "";
        int countByte = index;

        RData domainResult = getDomainFromIndex(countByte);
        countByte += domainResult.bytes;
        domain = domainResult.domain;

        // Name
        record.name = domain;

        // TYPE
        byte[] ans_type = new byte[2];
        ans_type[0] = response[countByte];
        ans_type[1] = response[countByte + 1];
        record.qType = getQTYPEFromByteArray(ans_type);
        countByte += 2;

        // CLASS
        byte[] ans_class = new byte[2];
        ans_class[0] = response[countByte];
        ans_class[1] = response[countByte + 1];
        if (ans_class[0] != 0 && ans_class[1] != 1) {
            throw new RuntimeException(("ERROR\tThe class field in the response answer is not 1"));
        }
        record.qClass = ans_class;
        countByte += 2;

        // TTL
        byte[] TTL = { response[countByte], response[countByte + 1], response[countByte + 2], response[countByte + 3] };
        ByteBuffer wrapped = ByteBuffer.wrap(TTL);
        record.ttl = wrapped.getInt();

        countByte += 4;
        // RDLength
        byte[] RDLength = { response[countByte], response[countByte + 1] };
        wrapped = ByteBuffer.wrap(RDLength);
        int rdLength = wrapped.getShort();
        record.rdLength = rdLength;

        countByte += 2;
        switch (record.qType) {
        case A:
            record.domain = (parseATypeRDATA(rdLength, countByte));
            break;
        case NS:
            record.domain = (parseNSTypeRDATA(rdLength, countByte));
            break;
        case MX:
            record.domain = (parseMXTypeRDATA(rdLength, countByte, record));
            break;
        case CName:
            record.domain = (parseCNAMETypeRDATA(rdLength, countByte));
            break;
        }
        record.byteLength = (countByte + rdLength - index);
        return record;
    }

    private RData getDomainFromIndex(int index) {
        RData result = new RData();
        int wordSize = this.response[index];
        String domain = "";
        boolean start = true;
        int count = 0;
        while (wordSize != 0) {
            if (!start) {
                domain += ".";
            }
            if ((wordSize & 0xC0) == (int) 0xC0) {
                byte[] offset = { (byte) (response[index] & 0x3F), response[index + 1] };
                ByteBuffer wrapped = ByteBuffer.wrap(offset);
                domain += getDomainFromIndex(wrapped.getShort()).domain;
                index += 2;
                count += 2;
                wordSize = 0;
            } else {
                domain += getWordFromIndex(index);
                index += wordSize + 1;
                count += wordSize + 1;
                wordSize = response[index];
            }
            start = false;
        }
        result.domain = domain;
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

    public QType getQTYPEFromByteArray(byte[] b_array) {
        byte b = b_array[1];
        switch (b) {
        case 1:
            return QType.A;
        case 2:
            return QType.NS;
        case 15:
            return QType.MX;
        case 5:
            return QType.CName;
        default:
            return QType.Other;
        }
    }

    private String parseATypeRDATA(int rdLength, int countByte) {
        String address = "";
        byte[] byteAddress = { response[countByte], response[countByte + 1], response[countByte + 2],
                response[countByte + 3] };
        try {
            InetAddress inetaddress = InetAddress.getByAddress(byteAddress);
            address = inetaddress.toString().substring(1);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return address;
    }

    private String parseNSTypeRDATA(int rdLength, int countByte) {
        RData result = getDomainFromIndex(countByte);
        String nameServer = result.domain;

        return nameServer;
    }

    private String parseMXTypeRDATA(int rdLength, int countByte, Record record) {
        byte[] mxPreference = { this.response[countByte], this.response[countByte + 1] };
        ByteBuffer buf = ByteBuffer.wrap(mxPreference);
        record.mxPreference = buf.getShort();
        return getDomainFromIndex(countByte + 2).domain;
    }

    private String parseCNAMETypeRDATA(int rdLength, int countByte) {
        RData result = getDomainFromIndex(countByte);
        String cname = result.domain;

        return cname;
    }

}