public class Record {
    public int ttl, rdLength, mxPreference;
    public String name, domain;
    public byte[] qClass;
    public QType qType;
    public boolean auth;
    public int byteLength;

    public void outputRecord() {
        String authString = this.auth ? "auth" : "nonauth";
        switch (this.qType) {
        case A:
            System.out.println("IP\t" + this.domain + "\t" + this.ttl + "\t" + authString);
            break;
        case NS:
            System.out.println("NS\t" + this.domain + "\t" + this.ttl + "\t" + authString);
            break;
        case MX:
            System.out.println("MX\t" + this.domain + "\t" + mxPreference + "\t" + this.ttl + "\t" + authString);
            break;
        case CName:
            System.out.println("CNAME\t" + this.domain + "\t" + this.ttl + "\t" + authString);
            break;
        default:
            break;
        }
    }
}