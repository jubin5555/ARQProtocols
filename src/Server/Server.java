package Server;

import POJO.ServerPacket;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.TimeUnit;

public class Server
{
    public static Integer CURRENTSEQUENCENUMBER=0;
    public static Integer TOTALPACKETLOSSCOUNT=0;
   /* public static PrintWriter writer;*/

    public static void receiveData(int portNumber, String file,double probabilityPacketLoss) throws IOException, InterruptedException {
        DatagramSocket ds = new DatagramSocket(portNumber);
        byte[] b1 =new byte[1024];

        String fileName=System.getProperty("user.dir")+"\\ServerFiles\\"+file.trim()+".txt";

        PrintWriter writer= new PrintWriter(fileName,"UTF-8");

        while(true) {

            DatagramPacket dp = new DatagramPacket(b1, b1.length);
            ds.receive(dp);
            ServerPacket currentPacket = ServerHelper.decipherPacket(b1);
            int sequenceNumber =currentPacket.getSequenceNumber();
            //check if the correct packet is received or else dont send an acknowledgment

            if(sequenceNumber==CURRENTSEQUENCENUMBER && ServerHelper.dropPacket(probabilityPacketLoss).equals(Boolean.FALSE)) {
                BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));

                writer.println(currentPacket.getData());
                out.close();
                byte[] acknowledgmentNumberBytes = ServerHelper.getAcknowledgmentNumber(CURRENTSEQUENCENUMBER);
                DatagramPacket dp2 = new DatagramPacket(acknowledgmentNumberBytes, acknowledgmentNumberBytes.length, dp.getAddress(), dp.getPort());
                TimeUnit.MILLISECONDS.sleep(50);
                ds.send(dp2);
                CURRENTSEQUENCENUMBER++;
            }
            else{
                System.out.println("Packet loss, sequence number:" +CURRENTSEQUENCENUMBER );
            }
        }

    }
    public static void main(String[] args) throws IOException, InterruptedException {
        int portNumber;
        String fileName;
        double probability;
        if(args.length!=3){
            System.out.println("The total number of arguments required are 2");
            System.out.println("The arguments are portNumber,fileName and probability for packet loss");
        }
        else{
            portNumber=Integer.parseInt(args[0]);
            fileName =args[1];
            probability = Double.parseDouble(args[2]);
            receiveData(portNumber,fileName,probability);
            /*writer.close();*/
        }

    }
}

