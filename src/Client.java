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

public class Client {
    public static int port;
    public static InetAddress address;
    public static DatagramSocket socket = null;
    public static DatagramPacket packet;
    public static byte[] data;
    public static int P;
    public static int count=0;
    public static int mss =1000;
    public static int t_val=10;
    
    public static byte[] convertBytes(String s,int P){
        byte[] data=s.getBytes();
	byte[] requested=new byte[P+data.length];
	int i;
	for(i=0;i<data.length;i++)
	{
		requested[i]=data[i];
	}
	return requested;
    }
    
    public static int[][] getPacketInfo(int free, int last_ack_seq_rec, int last_ack_len_rec){
        //result[i][0]:: ith pkts's sequence id; result[i][1]:: ith pkt's size
        int[][] result = new int[(free%mss == 0)?((int)free/mss):(((int)free/mss)+1)][2];
        int i =0;
        while(free>0){
            result[i][0] = last_ack_seq_rec+last_ack_len_rec;
            if(free>mss){
                result[i][1]=mss;
            }
            else{
                result[i][1]=free;
            }
            i++;
            last_ack_seq_rec+=mss;
            free-=mss;
        }
        return result;
    }
    
    public static void main(String[] args) throws IOException{
        
        int free = mss;
        int W = mss;
        int lasr = 0;//LastAcknowledgementSeqReceivedfromreceiver
        int lalr = 0;//LastAcknowledgementLengthReceivedfromreceiver
        int lsps = 0;//LastSenrPacketSeq
        int lspl = 0;//LastSentPacketLength
        int[][] packet_info;
        int t_out =1000;
        int lp = 0;
        boolean[] dropping = new boolean[20];
        dropping[0]= true;
        for(int i =1; i<20; i++){
            dropping[i]=false;
        }
        Queue<Long[]> timer = new LinkedList<Long[]>();
        //ArrayList
        port = 4900;
        P=100;
        socket = new DatagramSocket();
        address=InetAddress.getByName("127.0.0.1");
        int count=0;
        boolean timeout = false;
        while(true){
            socket.setSoTimeout(t_out);
            if(timeout){
                packet_info = getPacketInfo(free,lasr,lalr);
            }
            else{
                packet_info = getPacketInfo(free,lsps,lspl);
            }
            
            System.out.println("Sending begins...");
            for(int[] num_pkts : packet_info ){
                System.out.println(num_pkts[0]+":"+num_pkts[1]);
                data=convertBytes(num_pkts[0]+":"+num_pkts[1],num_pkts[1]);
                packet = new DatagramPacket(data, data.length, address, port);
                if(lp==0){
                    socket.send(packet);
                }
                else if(lp==1){
                   if(Math.random()*20!=0){
                       socket.send(packet);
                   }
                }
                else{
                    System.out.println("Wrong loss parameter entered! The system will exit.");
                    System.exit(0);
                }
                 
                Long[] timer_data = new Long[3];
                timer_data[0] = System.currentTimeMillis();
                timer_data[1] = (long)(num_pkts[0]);
                timer_data[2] = (long)(num_pkts[1]);
                lsps = num_pkts[0];
                lspl = num_pkts[1];
                timer.add(timer_data);
                System.out.println("latest sent pkt's timestamp = "+timer_data[0]+",\nlatest sent pkt's seq num = "+timer_data[1]+",\nlatest sent pkt's length = "+timer_data[2]+"\n.............");
            }
            System.out.println("Sending ends.\n");
            byte[] receiver=new byte[P];
            DatagramPacket packet1 = new DatagramPacket(receiver, receiver.length);
            while(true){
                try{
                    System.out.println("Receiving packet...");
                    socket.receive(packet1);
                    System.out.println("Packet received.");
                    String received[] = new String[2];
                    received = (new String(packet1.getData())).trim().split(":");
                    System.out.println("Received cum_ack = "+received[0]);
                    
                    if(timer.peek()[1]+timer.peek()[2]==Long.parseLong(received[0])){
                        lasr=timer.peek()[1].intValue();
                        lalr=timer.peek()[2].intValue();
                        free=timer.peek()[2].intValue() + mss*mss/W;
                        W = W+mss*mss/W;
                        timeout = false;
                        timer.remove();
                        System.out.println("timer ack seq matches received ack's cum num which is "+received[0]+",\nfree = "+free+",\nW = "+W);
                        break;
                    }
                    else{
                        System.out.println("timer ack seq mismatches received ack's cum num which is "+received[0]+",\nfree = "+free+",\nW = "+W);
                    }
                }
                catch(SocketTimeoutException e){
                    timer.clear();
                    timeout = true;
                    W = mss;
                    free = mss;
                    System.out.println("timeout.");
                    break;
                }
        
            }
        }
    }
}
