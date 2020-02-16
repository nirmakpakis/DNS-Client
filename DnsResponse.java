public class DnsResponse {

    byte[] request;
    byte[] response;

    // Request parsing
    public Header headerRequest;
    public Question questionRequest;

    // Response parsing
    public Header headerResponse;
    public Question questionResponse;
    public Answer answerResponse;
    public Additional additionalRecords;

    public boolean noRecords;
    public int offset;

    public DnsResponse(byte[] request, byte[] response) {
        // Request and Response
        this.request = request;
        this.response = response;

        // Request parsing
        Header header_Request = new Header(response);
        this.headerRequest = header_Request;

        Question question_Request = new Question(response);
        this.questionRequest = question_Request;

        // Response parsing
        Header header_Response = new Header(response);
        this.headerResponse = header_Response;

        this.validateHeaderResponse();

        Question question_Response = new Question(response);
        this.questionResponse = question_Response;

        this.validateResponseMatchesRequest();

        this.offset = request.length;
        Answer answer_Response = new Answer(response, this.offset, headerResponse, questionResponse,
                header_Response.ANCount);
        this.answerResponse = answer_Response;

        this.offset = answerResponse.offset;

        // ns count even though we don't do anything
        Answer NS_Response = new Answer(response, this.offset, headerResponse, questionResponse,
                header_Response.ANCount);

        this.offset = NS_Response.offset;

        Additional additional_records = new Additional(response, this.offset, headerResponse, questionResponse,
                header_Response.ARCount);
        this.additionalRecords = additional_records;
    }

    public void outputResponse() {

        System.out.println();
        if (headerResponse.ANCount <= 0) {
            System.out.println("No Answer");
            return;
        }

        System.out.println("***Answer Section (" + headerResponse.ANCount + " Records)***");

        for (Record record : this.answerResponse.records) {
            this.printResponse(record);
        }

        System.out.println();

        System.out.println("***Additional Section (" + headerResponse.ARCount + " Records)***");
        if (headerResponse.ARCount > 0) {
            for (Record record : additionalRecords.records) {
                this.printResponse(record);
            }
        }

    }

    public void printResponse(Record record) {
        String aa;
        if (record.AA) {
            aa = "auth";
        } else {
            aa = "nonauth";
        }
        switch (record.qType) {
        case A:
            System.out.println("IP\t" + record.domainName + "\t" + record.ttl + "\t" + aa);
            break;
        case NS:
            System.out.println("NS\t" + record.domainName + "\t" + record.ttl + "\t" + aa);
            break;
        case MX:
            System.out.println("MX\t" + record.domainName + "\t" + record.mxPreference + "\t" + record.ttl + "\t" + aa);
            break;
        case CName:
            System.out.println("CNAME\t" + record.domainName + "\t" + record.ttl + "\t" + aa);
            break;
        default:
            break;
        }
    }

    public void validateResponseMatchesRequest() {
        if (this.questionRequest.qType != this.questionResponse.qType)
            throw new RuntimeException("Error: The request type doesn't match response type");
    }

    public void validateHeaderResponse() {
        if (!this.headerResponse.QR)
            throw new RuntimeException("Error: this packet is not type Response");
        if (!this.headerResponse.RA)
            throw new RuntimeException("Error: this packet doesn't support recursive queries");
        switch (this.headerResponse.RCode) {
        case 0:
            break;
        case 1:
            throw new RuntimeException("Format error: the name server was unable to interpret the query");
        case 2:
            throw new RuntimeException(
                    "Server failure: the name server was unable to process this query due to a problem with the name server");
        case 3:
            throw new RuntimeException(
                    "Name error: meaningful only for responses from an authoritative name server, this code signifies that the domain name referenced in the query does not exist");
        case 4:
            throw new RuntimeException("Not implemented: the name server does not support the requested kind of query");
        case 5:
            throw new RuntimeException(
                    "Refused: the name server refuses to perform the requested operation for policy reasons");
        }

    }

}