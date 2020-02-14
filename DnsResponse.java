public class DnsResponse {

    // Response parsing
    public Header headerResponse;
    public Question questionResponse;
    public Answer answerResponse;

    // Request parsing
    public Header headerRequest;
    public Question questionRequest;

    public DnsResponse(byte[] request, byte[] response) {
        Header header_Response = new Header(response);
        this.headerRequest = header_Response;
        // this.questionResponse = new Question(response);
        // make an array of answers
        // offset = request.length
        // for i -> ANCOUNT
        // this.answerResponse = new Answer(response, offset);
        // update offset

        // this.headerRequest = new Header(request);
        // this.questionRequest = new Question(request);

        // checkRequestMatchesResponse();
    }

    public void print() {
        // System.out.println(headerResponse.ANCount);
        // System.out.println(questionResponse.qType.toString());
    }

}