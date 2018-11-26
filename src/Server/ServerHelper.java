import POJO.ServerPacket;

import java.math.BigInteger;

public class ServerHelper {
    //this function is used to convert the byte array into the format :-
    //Sequence Number
    //Datatype indicator
    //CheckSum
    public static ServerPacket decipherPacket(byte[] data){
        byte[] sequenceBytes  = new byte[4];
        byte[] checkSum =new byte[2];
        byte[] dataType = new byte[2];
        byte[] extractedData = new byte[data.length-8];
        System.arraycopy(data,0,sequenceBytes,0,4);
        System.arraycopy(data,4,checkSum,0,2);
        System.arraycopy(data,6,dataType,0,2);
        System.arraycopy(data,8,extractedData,0,data.length-8);

        System.out.println("sequence Number : "+new BigInteger(sequenceBytes).intValue());
        System.out.println("checkSum : "+new BigInteger(checkSum).intValue());
        System.out.println("datatype : "+new BigInteger(dataType).intValue());


        int checkSumDecimal =new BigInteger(checkSum).intValue();
        int sequenceNumberDecimal =new BigInteger(sequenceBytes).intValue();
        int dataTypeIndicatorDecimal =new BigInteger(dataType).intValue();
        String dataString = new String(extractedData);

        ServerPacket serverPacket = new ServerPacket();
        serverPacket.setCheckSum(checkSumDecimal);
        serverPacket.setSequenceNumber(sequenceNumberDecimal);
        serverPacket.setdataTypeIndicator(dataTypeIndicatorDecimal);
        serverPacket.setData(dataString);
        return serverPacket;

    }
}
