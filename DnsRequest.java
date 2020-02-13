public class DnsRequest {

    public String domainName;
    public QType qType = QType.A;
    public byte[] request;

    public DnsRequest(String domainName, QType qType) {
        this.domainName = domainName;
        this.qType = qType;
    }

    // TODO: implement header.java
    public byte[] getRequest() {
        // implement header
        // implement question
    }

}