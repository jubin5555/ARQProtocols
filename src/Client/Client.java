package Client;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static ClientHelpers.ClientHelper.chunkArray;
import static ClientHelpers.ClientHelper.goBackNProtocol;

public class Client
{
    //the last packet which was received
    public static int CURRENTACKNOWLEDGEDPACKETNUMBER;
    //a pointer to the beginning of the window
    public static int CURRENTWINDOWPOINTER=0;
    public static Boolean CURRENTACKNOWLEDGEDSTATUS=Boolean.TRUE;
    public static Map<Long,Integer> PACKETSENDTIME= new HashMap<Long, Integer>();

    public static DatagramSocket ds;
    public static void sendPacketToServer(String hostName,int serverPort,String fileName,
                                          int windowSize,int MSS) throws IOException, InterruptedException {
        Path path = Paths.get("ClientFiles//"+fileName+".txt");
        byte[] byteArray = Files.readAllBytes(path);
        byte[][] byteArray2=chunkArray(byteArray, MSS);
        InetAddress serverAdress = InetAddress.getByName(hostName);
        while (CURRENTWINDOWPOINTER <= byteArray2.length - 1 && CURRENTACKNOWLEDGEDSTATUS.equals(Boolean.TRUE)) {
            goBackNProtocol(CURRENTWINDOWPOINTER,windowSize,byteArray2,ds,MSS,serverAdress,serverPort);
           /* if(CURRENTACKNOWLEDGEDPACKETNUMBER>CURRENTWINDOWPOINTER) {
              printInfoLostPackets(CURRENTACKNOWLEDGEDPACKETNUMBER, CURRENTWINDOWPOINTER + windowSize);
              CURRENTWINDOWPOINTER = CURRENTACKNOWLEDGEDPACKETNUMBER;
          }*/
        }
        System.out.println("The process is over.");
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

                    if(CURRENTACKNOWLEDGEDPACKETNUMBER>CURRENTWINDOWPOINTER) {
                        /* printInfoLostPackets(CURRENTACKNOWLEDGEDPACKETNUMBER, CURRENTWINDOWPOINTER + windowSize);*/
                        CURRENTWINDOWPOINTER = CURRENTACKNOWLEDGEDPACKETNUMBER;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static  class checkPacketTime extends Thread {
        public void run(){
            try {
                ds= new DatagramSocket();
                while(true) {
                    if(PACKETSENDTIME.size()>0 && PACKETSENDTIME.containsKey(System.currentTimeMillis()) && PACKETSENDTIME.get(System.currentTimeMillis())>=CURRENTWINDOWPOINTER)
                    {
                        CURRENTACKNOWLEDGEDSTATUS=Boolean.FALSE;
                        PACKETSENDTIME.clear();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String args[]) throws IOException, InterruptedException {
        long startTime = System.currentTimeMillis();
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
            new checkPacketTime().start();
            sendPacketToServer(serverHostName,serverPortNumber,fileName,windowSize,maximumSegmentSize);
        }
        else{
            System.out.println("Missing Command Line Arguments");
            System.out.println("The arguments should be ServerHostName,ServerPort,Filename,Window Size,Maximum Segment Size.");
        }
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Total time in seconds : "+TimeUnit.MILLISECONDS.toSeconds(totalTime));
    }

}


