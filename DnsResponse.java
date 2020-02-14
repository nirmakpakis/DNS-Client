import java.nio.ByteBuffer;

public class DnsResponse {

    byte[] request;
    byte[] response;

    // Request parsing
    public Header headerRequest;
    public Question questionRequest;

    // Response parsing
    public Header headerResponse;
    public Question questionResponse;
    public Record[] answerRecords;
    public Record[] additionalRecords;

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

        Question question_Response = new Question(response);
        this.questionResponse = question_Response;

        Answer answer = new Answer(response, request.length);
        this.answerRecords = answer.getAnswerRecords(headerResponse.ANCount);

        // checkRequestMatchesResponse();
    }

    public void output() {
        System.out.println("here");
        // for (Record record : this.answerRecords) {
        // record.outputRecord();
        // }
    }

}