import java.io.*;
import java.net.*;
import java.util.*;

int MSS = 1000; // a common mss across the sender and the receiver
int ACK =0 ; // ack_number initially 0

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
			
			byte[] data = new byte[1200]; 
			DatagramPacket packet = new DatagramPacket(data, data.length);
			socket.receive(packet);
			String received = new String(packet.getData(), 0, packet.getLength());
			//System.out.println(received);
			
			
			// PARSING THE RECEIVED PACKET
			String[] ss=received.split(",");
			int seq_num = Integer.parseInt(ss[0]);
			int packet_size = Integer.parseInt(ss[1]);
			
			// PROCESSING THE SEQ_NUM
			if (seq_num <= ACK) 
			{
				if (seq_num + packet_size -1 > ACK)/// if the packet contains bytes that have not been received 
				{
					// the receiver accepts those bytes and updates the ACK
					ACK = seq_num + packet_size;
					
					//sends the ACK packet
					String sending=Integer.toString(seq_num)+","+Integer.toString(ACK); 
					byte[] data_send=sending.getBytes();

					InetAddress address_send = packet.getAddress();
					int port_send = packet.getPort();
					packet = new DatagramPacket(data_send, data_send.length, address_send, port_send);
					socket.send(packet);
				}
				else
				{
					// ACK not changed but an ACK packet is sent telling the receiver about the current ACK
					String sending=Integer.toString(seq_num)+","+Integer.toString(ACK); 
					byte[] data_send=sending.getBytes();

					InetAddress address_send = packet.getAddress();
					int port_send = packet.getPort();
					packet = new DatagramPacket(data_send, data_send.length, address_send, port_send);
					socket.send(packet);
				}
			}
			else
			{
				// if seq_num > ACK then there has been a packet loss in between and we will not update ACK but
				// send an ACK packet
				String sending=Integer.toString(seq_num)+","+Integer.toString(ACK); 
					byte[] data_send=sending.getBytes();

					InetAddress address_send = packet.getAddress();
					int port_send = packet.getPort();
					packet = new DatagramPacket(data_send, data_send.length, address_send, port_send);
					socket.send(packet);
			}
			
			


		}
	}
	
	
}
