import java.nio.ByteBuffer;

public class Question {

    public String domainName;
    public QType qType;

    public Question(String domainName, QType qType) {
        this.domainName = domainName;
        this.qType = qType;
    }

    public byte[] createQuestionArray() {
        ByteBuffer questionArray = ByteBuffer.allocate(getQNameLength() + 1 + 2 + 2);
        questionArray.put(createQNameArray());
        questionArray.put(createQTypeArray());
        questionArray.put(createQClassArray());
        return questionArray.array();
    }

    public byte[] createQNameArray() {
        ByteBuffer qName = ByteBuffer.allocate(getQNameLength() + 1);
        String[] items = domainName.split("\\.");
        for (String item : items) {
            qName.put((byte) item.length());
            for (int j = 0; j < item.length(); j++) {
                qName.put((byte) ((int) item.charAt(j)));
            }
        }
        qName.put((byte) 0x00);
        return qName.array();
    }

    public byte[] createQTypeArray() {
        byte[] qType = new byte[2];
        switch (this.qType) {
        case A:
            qType = hexStringToByteArray("0001");
            break;
        case NS:
            qType = hexStringToByteArray("0002");
            break;
        case MX:
            qType = hexStringToByteArray("000f");
            break;
        case CName:
            qType = hexStringToByteArray("0005");
            break;
        }
        return qType;
    }

    public byte[] createQClassArray() {
        ByteBuffer qClass = ByteBuffer.allocate(2);
        qClass.put((byte) 0x00);
        qClass.put((byte) 0x0001);
        return qClass.array();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public int getQNameLength() {
        int count = 0;
        String[] items = domainName.split("\\.");
        for (String item : items) {
            count += item.length() + 1;
        }
        return count;
    }

}