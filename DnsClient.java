import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class DnsClient {
    public static void main(final String args[]) throws Exception {
        final DnsClient client = new DnsClient(args);
        client.makeRequest();
    }

    public int timeOut = 5;
    public int maxRetires = 3;
    public int portNumber = 53;
    public byte[] server = new byte[4];
    public String domainName;
    public String serverString;
    public QType qType = QType.A;
    public byte[] responseBytes = new byte[1024];

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

    public void makeRequest() {
        // Get Request to send
        DnsRequest request = new DnsRequest(domainName, qType);
        byte[] requestBytes = request.getRequest();
        int requestSize = requestBytes.length;

        try {

            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(timeOut);
            InetAddress inetaddress = InetAddress.getByAddress(server);

            DatagramPacket requestPacket = new DatagramPacket(requestBytes, requestSize, inetaddress, portNumber);
            DatagramPacket responsePacket = new DatagramPacket(responseBytes, requestSize);

            // Send packet and time response
            long startTime = System.currentTimeMillis();
            socket.send(requestPacket);
            socket.receive(responsePacket);
            long endTime = System.currentTimeMillis();
            socket.close();

            System.out.println("Response received after " + (endTime - startTime) / 1000. + " seconds " + "("
                    + (maxRetires - 1) + " retries)");

            DnsResponse response = new DnsResponse(responsePacket.getData(), requestBytes.length);
            response.outputResponse();
        } catch (SocketException e) {
            System.out.println("ERROR\tCould not create socket");
        } catch (UnknownHostException e) {
            System.out.println("ERROR\tUnknown host");
        } catch (SocketTimeoutException e) {
            System.out.println("ERROR\tSocket Timeout");
            System.out.println("Reattempting request...");
            // pollRequest(++retryNumber);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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