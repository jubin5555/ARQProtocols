package ClientHelpers;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class ClientHelper {
    //the method is used to convert the data to packet
    public static byte[] finalPacketFrames(byte[] data,int MSSSize,int sequenceNumber){
        byte[] finalPacket = new byte[data.length+8];
        byte[] checkSumByte = checksum(data);
        byte [] sequenceNumberBytes = sequenceNumberBytes(sequenceNumber);
        byte[] dataTypeIndicator =getDataTypeIndicatorByte(21845);
        System.arraycopy(sequenceNumberBytes,0,finalPacket,0,4);
        System.arraycopy(checkSumByte,0,finalPacket,4,2);
        System.arraycopy(dataTypeIndicator,0,finalPacket,6,2);
        System.arraycopy(data,0,finalPacket,8,data.length);
       /* System.out.println(finalPacket);
        System.out.println("finalPacketSize"+finalPacket.length);*/
        return finalPacket;
    }
    //this method is used to convert the entire data into equal sized MSS
    public static byte[][] chunkArray(byte[] array, int chunkSize) {
        int numOfChunks = (int)Math.ceil((double)array.length / chunkSize);
        byte[][] output = new byte[numOfChunks][];
        for(int i = 0; i < numOfChunks; ++i) {
            int start = i * chunkSize;
            int length = Math.min(array.length - start, chunkSize);
            byte[] temp = new byte[length];
            System.arraycopy(array, start, temp, 0, length);
            output[i] = temp;
        }
        return output;
    }
    //used to calculate checksum
     public  static byte[] checksum(byte data[]) {
        int sum=0;
        int nob=0;
         BigInteger totalSum ;
         byte[] checksumByte= new byte[2];
        int[] c_data= new int[100];
         for(int i=0;i<data.length;i++){

         // Complementing the entered data
         // Here we find the number of bits required to represent
         // the data, like say 8 requires 1000, i.e 4 bits
         nob = (int)(Math.floor(Math.log(data[i]) / Math.log(2))) + 1;

         // Here we do a XOR of the data with the number 2^n -1,
         // where n is the nob calculated in previous step
         c_data[i] = ((1 << nob) - 1) ^ data[i];

         // Adding the complemented data and storing in sum
         sum += c_data[i];
    }
    totalSum=BigInteger.valueOf(sum);
         checksumByte= totalSum.toByteArray();
    return checksumByte;
    }
    //convert sequence number into byte sequence
    public static byte[] sequenceNumberBytes(int number)
    {
        return ByteBuffer.allocate(4).putInt(number).array();
    }
    //convert dataTypeIndicator to byte sequence
    public static byte[] getDataTypeIndicatorByte(int number){
        BigInteger dataInt = BigInteger.valueOf(number);
        byte[] dataType =dataInt.toByteArray();
        return dataType;
    }

    }

