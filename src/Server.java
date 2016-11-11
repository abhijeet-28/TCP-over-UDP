/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ANKIT
 */
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    
    public static int port;
    public static InetAddress address;
    public static DatagramSocket socket = null;
    public static DatagramPacket packet;
    public static byte[] data;
    public static byte[] convertBytes(String s,int P)
    {
	byte[] requested=new byte[P];
	byte[] data=s.getBytes();
	int i;
	for(i=0;i<data.length;i++)
	{
		requested[i]=data[i];
	}
	return requested;
    }
    public static byte[] convertBytes(String s)
    {
	byte[] data=s.getBytes();
	return data;
    }
    
    public static void main(String[] args) throws IOException{
        port = 4900;
        socket=new DatagramSocket(port);
        int i =0;
        byte[] data = new byte[256];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        while(true){
            socket.receive(packet);
            System.out.println((new String(packet.getData())).trim());
            String received[] = new String[2];
            received=(new String(packet.getData())).trim().split(":");
            System.out.println("[0] = "+received[0]);
            System.out.println("[1] = "+received[1]);
            socket.setSoTimeout(5000);
            address=InetAddress.getByName("127.0.0.1");
            byte[] data_send;
            data_send=convertBytes(received[0],100);
            InetAddress address_send = packet.getAddress();
            int port_send = packet.getPort();
            packet = new DatagramPacket(data_send, data_send.length, address_send, port_send);
            System.out.println("port_send = "+port_send+", address_send= "+address_send);
            socket.send(packet);
            i++;
            System.out.println("sent! "+i+" times");
        }
    }
}
