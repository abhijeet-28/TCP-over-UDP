import java.io.*;
import java.net.*;
import java.util.*;

public class echo
{
	public static void main(String[] args)
	{
		try{
		dataserver response=new dataserver();
		response.run();
	}
		catch(IOException e)
		{
			System.out.println("error");
		}
	}
}

class dataserver 
{
	DatagramSocket socket;
	int port;
	public dataserver() throws IOException
	{
		Random random=new Random();
		 while(socket==null)
		 { 
		 port=4040+random.nextInt(1000);

		 socket=new DatagramSocket(port);


		 }
		 System.out.println(port);

		//socket=new DatagramSocket(4422);
		
	}
	boolean success=true;
	public void run() throws IOException
	{
		while(success==true)
		{
			
			byte[] data = new byte[256];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			socket.receive(packet);
			String received = new String(packet.getData(), 0, packet.getLength());
			//System.out.println(received);

			String[] ss=received.split(",")[0].split(":");
			int co=Integer.parseInt(ss[1]);
			String sending="Counter:"+Integer.toString(co)+","+received.split(",")[1]; 
		

			byte[] data_send=sending.getBytes();

			InetAddress address_send = packet.getAddress();
			int port_send = packet.getPort();
			packet = new DatagramPacket(data_send, data_send.length, address_send, port_send);
			socket.send(packet);


		}
	}
	
	
}
