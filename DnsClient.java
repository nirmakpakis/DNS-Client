import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class DnsClient {

    public static void main(String args[]) throws Exception {
        DnsClient client = new DnsClient(args);
        client.makeRequest();
    }

    public int timeOut = 500;
    public int maxRetires = 3;
    public int portNumber = 53;
    public byte[] server = new byte[4];
    public String domainName;
    public String serverString;
    public QType qType = QType.A;
    public byte[] responseBytes = new byte[1024];

    public DnsClient(String args[]) {
        this.parseInput(args);
    }

    public void makeRequest() {
        System.out.println("DnsClient sending request for " + domainName);
        System.out.println("Server: " + serverString);
        System.out.println("Request type: " + qType.toString());
        pollRequest(1);
    }

    private void pollRequest(int retryNumber) {
        if (retryNumber > 3) {
            System.out.println("ERROR\tMaximum number of retries " + 3 + " exceeded");
            return;
        }

        try {
            // Create Datagram socket and request object(s)
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(timeOut);
            InetAddress inetaddress = InetAddress.getByAddress(server);
            DnsRequest request = new DnsRequest(domainName, qType);

            byte[] requestBytes = request.createRequest();
            byte[] responseBytes = new byte[1024];

            // int i = 0;
            // for (byte b : requestBytes) {
            // System.out.println("byte " + i + " is :" + Integer.toBinaryString(b & 255 |
            // 256).substring(1));
            // i++;
            // }

            DatagramPacket requestPacket = new DatagramPacket(requestBytes, requestBytes.length, inetaddress,
                    portNumber);
            DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length);

            // Send packet and time response
            long startTime = System.currentTimeMillis();
            socket.send(requestPacket);
            socket.receive(responsePacket);
            // i = 0;
            // for (byte b : responseBytes) {
            // System.out.println("byte " + i + " is :" + Integer.toBinaryString(b & 255 |
            // 256).substring(1));
            // i++;
            // }
            long endTime = System.currentTimeMillis();
            socket.close();

            System.out.println("Response received after " + (endTime - startTime) / 1000. + " seconds " + "("
                    + (retryNumber - 1) + " retries)");

            // DnsResponse response = new DnsResponse(responsePacket.getData(),
            // requestBytes.length, queryType);
            // response.outputResponse();

        } catch (SocketException e) {
            System.out.println("ERROR\tCould not create socket");
        } catch (UnknownHostException e) {
            System.out.println("ERROR\tUnknown host");
        } catch (SocketTimeoutException e) {
            System.out.println("ERROR\tSocket Timeout");
            System.out.println("Reattempting request...");
            pollRequest(++retryNumber);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void parseInput(String[] args) {

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
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
                    String[] addressList = serverString.split("\\.");
                    for (int j = 0; j < addressList.length; j++) {
                        int ipValue = Integer.parseInt(addressList[j]);
                        server[j] = (byte) ipValue;
                    }
                    domainName = args[i + 1];
                }
                break;
            }

        }
    }

}