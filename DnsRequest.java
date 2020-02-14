import java.nio.ByteBuffer;
import java.util.Random;

public class DnsRequest {

    public String domainName;
    public QType qType = QType.A;
    public byte[] request;

    public DnsRequest(String domainName, QType qType) {
        this.domainName = domainName;
        this.qType = qType;
    }

    public byte[] createRequest() {

        ByteBuffer request = ByteBuffer.allocate(12 + 5 + getQNameLength());

        Header header = new Header();
        request.put(header.createHeaderArray());

        Question question = new Question(domainName, qType);
        request.put(question.createQuestionArray());

        return request.array();
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