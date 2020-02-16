import java.net.*;

public class DnsClient {

    public int timeOut = 500;
    public int maxRetires = 3;
    public int portNumber = 53;
    public byte[] server = new byte[4];
    public String domainName;
    public String serverString;
    public QType qType = QType.A;
    public byte[] response_bytes = new byte[1024];

    public static void main(String args[]) throws Exception {
        DnsClient client = new DnsClient(args);
        client.makeRequest();
    }

    public DnsClient(String args[]) {
        this.parseInput(args);
    }

    public void makeRequest() {
        System.out.println("DnsClient sending request for " + domainName);
        System.out.println("Server: " + serverString);
        System.out.println("Request type: " + qType.toString());
        pollRequest(1);
    }

    private void pollRequest(int numOfRetries) {

        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(this.timeOut);
            InetAddress i_add = InetAddress.getByAddress(this.server);
            DnsRequest request = new DnsRequest(domainName, qType);
            byte[] request_bytes = request.createRequest();

            DatagramPacket request_packet = new DatagramPacket(request_bytes, request_bytes.length, i_add, portNumber);
            DatagramPacket response_packet = new DatagramPacket(this.response_bytes, response_bytes.length);

            long start = System.currentTimeMillis();

            socket.send(request_packet);
            socket.receive(response_packet);

            long end = System.currentTimeMillis();

            socket.close();

            System.out.println("Response received after " + (end - start) / 1000. + " seconds " + "("
                    + (numOfRetries - 1) + " retries)");

            DnsResponse response = new DnsResponse(request_bytes, response_bytes);
            response.outputResponse();

        } catch (UnknownHostException e) {
            System.out.println("Error:Host is not known");
        } catch (SocketException e) {
            System.out.println("Error:Can't create socket");
        } catch (SocketTimeoutException e) {
            System.out.println("Error:Socket has timed out. Trying again ...");
            pollRequest(numOfRetries + 1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (numOfRetries + 1 > this.maxRetires) {
            System.out.println("Error:Max retries " + this.maxRetires + " passed");
            return;
        }
    }

    public void parseInput(String[] inputArray) {

        for (int i = 0; i < inputArray.length; i++) {
            String arg = inputArray[i];
            switch (arg) {
            case "-t":
                timeOut = Integer.parseInt(inputArray[i + 1]) * 1000;
                i++;
                break;
            case "-r":
                maxRetires = Integer.parseInt(inputArray[i + 1]);
                i++;
                break;
            case "-p":
                portNumber = Integer.parseInt(inputArray[i + 1]);
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
                    domainName = inputArray[i + 1];
                }
                break;
            }

        }
    }

}
