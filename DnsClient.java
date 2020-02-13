import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class DnsClient {
    public static void main(final String args[]) throws Exception {
        final DnsClient client = new DnsClient(args);
    }

    enum QType {
        A, NS, MX;
    }

    public int timeOut = 5;
    public int maxRetires = 3;
    public int portNumber = 53;
    public byte[] server = new byte[4];
    public String domainName;
    public String serverString;
    public QType qType = QType.A;

    public DnsClient(final String args[]) {
        this.parseInput(args);
        System.out.println("time out:" + timeOut);
        System.out.println("maxRetires:" + maxRetires);
        System.out.println("portNumber:" + portNumber);
        System.out.println("server:" + server.toString());
        System.out.println("domainName:" + domainName);
        System.out.println("serverString:" + serverString);
        System.out.println("qType:" + qType);
    }

    public void parseInput(final String[] args) {

        for (int i = 0; i < args.length; i++) {
            final String arg = args[i];
            switch (arg) {
            case "-t":
                timeOut = Integer.parseInt(args[i + 1]) * 1000;
                i++;
                break;
            case "-r":
                maxRetires = Integer.parseInt(args[i + 1]);
                i++;
                break;
            case "-p":
                portNumber = Integer.parseInt(args[i + 1]);
                i++;
                break;
            case "-mx":
                qType = QType.MX;
                break;
            case "-ns":
                qType = QType.NS;
                break;
            default:
                if (arg.contains("@")) {
                    serverString = arg.substring(1);
                    final String[] addressList = serverString.split("\\.");
                    for (int j = 0; j < addressList.length; j++) {
                        final int ipValue = Integer.parseInt(addressList[j]);
                        server[j] = (byte) ipValue;
                    }
                    domainName = args[i + 1];
                }
                break;
            }

        }
    }

}