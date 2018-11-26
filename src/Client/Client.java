package Client;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;


import static ClientHelpers.ClientHelper.*;


public class Client
{
    public static void sendPacketToServer(/*String hostName,int serverPort,String fileName,int windowSize,int MSS*/) throws IOException, InterruptedException {
        while(true) {
            Path path = Paths.get("C:\\Users\\jajubina\\Desktop\\SOCProject5\\project\\CSC573P2\\src\\ClientFiles\\SampleTextFile_1000kb.txt");
            byte[] byteArray = Files.readAllBytes(path);
            /*System.out.println(byteArray);*/
            byte[][] byteArray2;
            InetAddress serverAdress = InetAddress.getLocalHost();
            byteArray2 = chunkArray(byteArray, 100);
            DatagramSocket ds = new DatagramSocket();
            for (int i = 0; i < byteArray2.length; i++) {
                byte[] udpPacket = finalPacketFrames(byteArray2[i], 100, i);
                DatagramPacket dp = new DatagramPacket(udpPacket, udpPacket.length, serverAdress, 7735);
                ds.send(dp);
                TimeUnit.SECONDS.sleep(1);
               /* System.out.println(udpPacket.length);*/
                byte[] sequenceNumberBytes = new byte[4];
                DatagramPacket receivedDatagram = new DatagramPacket(sequenceNumberBytes,sequenceNumberBytes.length);
                ds.receive(receivedDatagram);
                int sequenceNumber =new BigInteger(sequenceNumberBytes).intValue();
                System.out.println("Sequence Number : "+sequenceNumber);
            }



        }
    }

    public static void main(String args[]) throws IOException, InterruptedException {
      /*  String serverHostName;
        int serverPortNumber;
        String fileName;
        int windowSize;
        int maximumSegmentSize;
        if(args.length==5){
            serverHostName=args[0];
            serverPortNumber=Integer.parseInt(args[1]);
            fileName=args[2];
            windowSize =Integer.parseInt(args[3]);
            maximumSegmentSize=Integer.parseInt(args[4]);*/
            sendPacketToServer(/*serverHostName,serverPortNumber,fileName,windowSize,maximumSegmentSize*/);

       /* }
        else{
            System.out.println("Missing Command Line Arguments");
            System.out.println("The arguments should be ServerHostName,ServerPort,Filename,Window Size,Maximum Segment Size.");
        }*/
    }

}

