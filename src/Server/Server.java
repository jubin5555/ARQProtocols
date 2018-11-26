package Server;

// Java program to illustrate Server side
// Implementation using DatagramSocket
import POJO.ServerPacket;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Server
{

    public static void receiveData(/*int portNumber, String fileName,int probablityPacketLoss*/) throws IOException {

        DatagramSocket ds = new DatagramSocket(7735);
        byte[] b1 =new byte[1024];
        String fileName="C:\\Users\\jajubina\\Desktop\\SOCProject5\\project\\CSC573P2\\src\\ServerFiles\\rfc1";
        FileOutputStream writer = new FileOutputStream(fileName);
        writer.write(("").getBytes());
        writer.close();

        while(true) {
            DatagramPacket dp = new DatagramPacket(b1, b1.length);
            ds.receive(dp);
            ServerPacket currentPacket = ServerHelper.decipherPacket(b1);
            int sequenceNumber =currentPacket.getSequenceNumber();
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
            out.write(currentPacket.getData().trim());
            out.close();
            byte[] acknowledgmentNumberBytes= ServerHelper.getAcknowledgmentNumber(sequenceNumber);
            DatagramPacket dp2 = new DatagramPacket(acknowledgmentNumberBytes, acknowledgmentNumberBytes.length, InetAddress.getLocalHost(),dp.getPort());
            ds.send(dp2);
        }
    }
    public static void main(String[] args) throws IOException {
       /* int portNumber;
        String fileName;
        int probability;
        if(args.length!=3){
            System.out.println("The total number of arguments required are 2");
            System.out.println("The arguments are portNumber,fileName and probability for packet loss");
        }*/
       /* else{
            portNumber=Integer.parseInt(args[0]);
            fileName =args[1];
            probability =Integer.parseInt(args[2])
            */
            receiveData(/*portNumber,fileName*/);

        /*}*/

    }
}

