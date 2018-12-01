package Server;
import POJO.ServerPacket;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SelectiveRepeatServer
{
    public static Set<Integer> currentWindow = new HashSet<>();
    public static Map<Integer,ServerPacket> currentWindowData = new HashMap<>();
    public static Integer TOTALPACKETLOSSCOUNT=0;
    public static String fileName;
    public static Integer windowSize=0;
    public static Integer expectedWindowSize=0;

    public static void receiveData(int portNumber,double probabilityPacketLoss) throws IOException, InterruptedException {

        DatagramSocket ds = new DatagramSocket(portNumber);
        byte[] b1 =new byte[1024];
        fileName="C:\\Users\\jajubina\\Desktop\\SOCProject5\\project\\CSC573P2\\src\\ServerFiles\\rfc1";
        FileOutputStream writer = new FileOutputStream(fileName);
        writer.write(("").getBytes());
        writer.close();

        while(true) {
            DatagramPacket dp = new DatagramPacket(b1, b1.length);
            ds.receive(dp);
            ServerPacket currentPacket = ServerHelper.decipherPacket(b1);
            int sequenceNumber =currentPacket.getSequenceNumber();
            //check if the correct packet is received or else dont send an acknowledgment

            System.out.println(sequenceNumber+"received sequencenumber");
            if(sequenceNumber<=expectedWindowSize && ServerHelper.dropPacket(probabilityPacketLoss).equals(Boolean.FALSE)) {
                byte[] acknowledgmentNumberBytes = ServerHelper.getAcknowledgmentNumber(sequenceNumber);
                DatagramPacket dp2 = new DatagramPacket(acknowledgmentNumberBytes, acknowledgmentNumberBytes.length, dp.getAddress(), dp.getPort());
                TimeUnit.MILLISECONDS.sleep(50);
                ds.send(dp2);
                currentWindowData.put(sequenceNumber,currentPacket);
                if(currentWindowData.size()==windowSize){writeToFile();}
            }
            else{
                System.out.println("Packet loss, sequence number:" +sequenceNumber );
                TOTALPACKETLOSSCOUNT=TOTALPACKETLOSSCOUNT+1;
                System.out.println("TotalPacketLoss: "+ TOTALPACKETLOSSCOUNT);
            }
        }
    }

    public static void writeToFile() throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
     for(Integer iterator:currentWindowData.keySet()) {
         out.write(currentWindowData.get(iterator).getData().trim());
         out.flush();
     }
        out.close();
        currentWindowData.clear();
        expectedWindowSize=expectedWindowSize+windowSize;
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        int portNumber;
        double probability;

        if(args.length!=4){
            System.out.println("The total number of arguments required are 4");
            System.out.println("The arguments are portNumber,fileName ,probability for packet loss, windowsize");
        }
        else{
            portNumber=Integer.parseInt(args[0]);
            fileName =args[1];
            probability = Double.parseDouble(args[2]);
            windowSize=Integer.parseInt(args[3]);
            expectedWindowSize=Integer.parseInt(args[3]);
            receiveData(portNumber,probability);
        }
    }
}


