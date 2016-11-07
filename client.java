import java.io.*;
import java.net.*;
import java.util.*;




public class client

{
	public static int port;
	public static InetAddress address;
	public static DatagramSocket socket = null;
	public static DatagramPacket packet;
	public static byte[] data;
	public static int P;
	public static int count=0;

	public static void main(String[] args) throws IOException


	{
		if (args.length != 3) {
    	System.out.println("Please enter the port number and packet size only");
    	return;
	}
	else
	{
String out=args[2];

		

	String ss="";

for(int k=0;k<50;k++)
		{
	count=0;

	
		boolean success=true;




		try
		{
		String arg1=args[0];
		String T=args[1];
		

		port=4574;
		P=Integer.parseInt(arg1);
//System.out.println("P=" +P);
//System.out.println("T="+T);
System.out.println(k);
		socket = new DatagramSocket();
		socket.setSoTimeout(15000);
		long startTime = System.nanoTime();
		
		String strt=Long.toString(startTime);

		
		String sending="Counter:"+T+","+strt;

		data=convertBytes(sending,P);
		//data=sending.getBytes();
		
		address=InetAddress.getByName("10.192.54.141");
		packet = new DatagramPacket(data, data.length,address, port);
		socket.send(packet); 

	

		while(success==true)
		{

			byte[] receiver=new byte[P];
		DatagramPacket packet1 = new DatagramPacket(receiver, receiver.length);
		try{
		socket.receive(packet1);
	
	
		String received = (new String(packet1.getData())).trim();
		String[] s1=received.split(",")[0].split(":");
		//System.out.println(received);
		count++;
		
		int counter=Integer.parseInt(s1[1])-1;





		 /*long endTime1 = System.nanoTime();
		 	String stTime1=received.split(",")[1].trim();
		 	long st1=Long.parseLong(stTime1);
		 	long TimeTaken1=endTime1-st1;
		 	System.out.println("Total time taken is : "+TimeTaken1);

*/






		if(counter>0)
		{
			String send="Counter:"+Integer.toString(counter)+","+received.split(",")[1];
			//System.out.println(send);

		//data=send.getBytes();
		byte[]	data1=convertBytes(send,P);
		
		address=InetAddress.getByName("10.192.54.141");
		DatagramPacket packet2 = new DatagramPacket(data1, data1.length,address, port);
		socket.send(packet2); 
		
		}
		else
		{
			long endTime = System.nanoTime();
			String stTime=received.split(",")[1].trim();
			long st=Long.parseLong(stTime);

			long TimeTaken=endTime-st;
			TimeTaken/=1000;
			ss+=Long.toString(TimeTaken);
			ss+="\n";
			
			System.out.println("Total time taken is : "+TimeTaken);
			success=false;
		}


	}
	catch(SocketTimeoutException e)
	{
		System.out.print("Timed out");
		System.out.println("No of packet received: "+count);
		break;
	}


	}



	}
	catch(IOException e)
	{
		System.out.println("error");
	}





Writer writer = null;

try {
    writer = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(out), "utf-8"));
    writer.write(ss);
    //writer.write(s);
} catch (IOException ex) {
  // report
} finally {
   try {writer.close();} catch (Exception ex) {/*ignore*/}
}








	}















}

}

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


}
