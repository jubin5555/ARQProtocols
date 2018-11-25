package POJO;

public class ServerPacket {
    private int sequenceNumber;
    private int dataTypeIndicator;
    private int checkSum;
    private String data;

    public void setSequenceNumber(int sequenceNumber){
        this.sequenceNumber=sequenceNumber;
    }
    public  void setdataTypeIndicator(int dataTypeIndicator){
        this.dataTypeIndicator=dataTypeIndicator;
    }
    public  void setCheckSum(int checkSum) {
        this.checkSum=checkSum;
    }
    public void setData(String data){
        this.data=data;
    }
    public int getSequenceNumber(int sequenceNumber){
        return this.sequenceNumber;
    }
    public  int getdataTypeIndicator(int dataTypeIndicator){
        return this.dataTypeIndicator;
    }
    public int getCheckSum(int checkSum) {
       return this.checkSum;
    }
    public String getData(String data){
        return this.data;
    }
}
