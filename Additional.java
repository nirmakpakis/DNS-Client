
public class Additional {

    public byte[] response;
    public int offset;
    public Header header;
    public Question question;
    public int numberOfRecords;

    public Record[] records;

    public Additional(byte response[], int offset, Header header, Question question, int numberOfRecords) {
        this.response = response;
        this.offset = offset;
        this.header = header;
        this.question = question;
        this.numberOfRecords = numberOfRecords;
        this.getAnswerRecords();
    }

    public void getAnswerRecords() {
        Record[] records = new Record[this.numberOfRecords];
        for (int i = 0; i < this.numberOfRecords; i++) {
            Record tempRecord = new Record(this.response, this.offset, header.AA, question.qType);
            records[i] = tempRecord;
            this.offset += records[i].byteLength;
        }
        this.records = records;
    }

}