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
    //the last packet which was received
    public static int CURRENTACKNOWLEDGEDPACKETNUMBER;
    //a pointer to the beginning of the window
    public static int CURRENTWINDOWPOINTER=0;
  /*  //a global socket to send and receive data
    //TODO : Make a singleton class for the socket to avoid multiple instances*/
    public static DatagramSocket ds;
    public static void sendPacketToServer(String hostName,int serverPort,String fileName,
                                          int windowSize,int MSS) throws IOException, InterruptedException {
        Path path = Paths.get("C:\\Users\\jajubina\\Desktop\\SOCProject5\\project\\CSC573P2\\src\\ClientFiles\\SampleTextFile_1000kb.txt");
        byte[] byteArray = Files.readAllBytes(path);
        byte[][] byteArray2=chunkArray(byteArray, MSS);
        InetAddress serverAdress = InetAddress.getByName("192.168.0.14");
        while (CURRENTWINDOWPOINTER <= byteArray2.length - 1) {
            goBackNProtocol(CURRENTWINDOWPOINTER,windowSize,byteArray2,ds,MSS,serverAdress,serverPort);
            TimeUnit.MILLISECONDS.sleep(25);
            if(CURRENTACKNOWLEDGEDPACKETNUMBER>CURRENTWINDOWPOINTER) {
              printInfoLostPackets(CURRENTACKNOWLEDGEDPACKETNUMBER, CURRENTWINDOWPOINTER + windowSize);
              CURRENTWINDOWPOINTER = CURRENTACKNOWLEDGEDPACKETNUMBER;
          }
        }
    }
    //A seperate thread to receive packets on the same socket
    public static  class receivePacket extends Thread {
        public void run(){
                try {
                   ds= new DatagramSocket();
                   while(true) {
                       byte[] sequenceNumberBytes = new byte[4];
                       DatagramPacket receivedDatagram = new DatagramPacket(sequenceNumberBytes, sequenceNumberBytes.length);
                       ds.receive(receivedDatagram);
                       CURRENTACKNOWLEDGEDPACKETNUMBER = Math.max(new BigInteger(sequenceNumberBytes).intValue(), CURRENTACKNOWLEDGEDPACKETNUMBER);
                       System.out.println(new BigInteger(sequenceNumberBytes).intValue() + ": Received number");
                       System.out.println(CURRENTACKNOWLEDGEDPACKETNUMBER + ": CurrentAckNumber");
                   }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
    public static void main(String args[]) throws IOException, InterruptedException {
        String serverHostName;
        int serverPortNumber;
        String fileName;
        int windowSize;
        int maximumSegmentSize;
        if(args.length==5){
            serverHostName=args[0];
            serverPortNumber=Integer.parseInt(args[1]);
            fileName=args[2];
            windowSize =Integer.parseInt(args[3]);
            maximumSegmentSize=Integer.parseInt(args[4]);
            new receivePacket().start();
            System.out.println("besides receiving thread");
            sendPacketToServer(serverHostName,serverPortNumber,fileName,windowSize,maximumSegmentSize);
        }
        else{
            System.out.println("Missing Command Line Arguments");
            System.out.println("The arguments should be ServerHostName,ServerPort,Filename,Window Size,Maximum Segment Size.");
        }
    }

}


