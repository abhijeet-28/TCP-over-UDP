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
    public static int t_val=1000;
    
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
        Queue<Long[]> timer = new LinkedList<Long[]>();
        //ArrayList
        port = 4900;
        P=100;
        socket = new DatagramSocket();
        address=InetAddress.getByName("127.0.0.1");
        int count=0;
        while(true){
            socket.setSoTimeout(5000);
            packet_info = getPacketInfo(free,lsps,lspl);
            for(int[] num_pkts : packet_info ){
                System.out.println(num_pkts[0]+":"+num_pkts[1]);
                data=convertBytes(num_pkts[0]+":"+num_pkts[1],num_pkts[1]);
                packet = new DatagramPacket(data, data.length, address, port);
                socket.send(packet); 
                Long[] timer_data = new Long[3];
                timer_data[0] = System.currentTimeMillis();
                timer_data[1] = (long)(num_pkts[0]);
                timer_data[2] = (long)(num_pkts[1]);
                lsps = num_pkts[0];
                lspl = num_pkts[1];
                timer.add(timer_data);
                System.out.println("timer_data[0] = "+timer_data[0]+"timer_data[1](lsps) = "+timer_data[1]+"timer_data[2](lspl) = "+timer_data[2]);
            }
            byte[] receiver=new byte[P];
            DatagramPacket packet1 = new DatagramPacket(receiver, receiver.length);
            while(true){
                try{
                    socket.receive(packet1);
                    String received[] = new String[2];
                    received = (new String(packet1.getData())).trim().split(":");
                    System.out.println("received[0](seq) = "+received[0]+", received[1](len) = "+received[1]+", currenttime = "+System.currentTimeMillis());
                    if(System.currentTimeMillis()-timer.peek()[0]<t_val){
                        if(timer.peek()[1]==Long.parseLong(received[0])){
                            lasr=timer.peek()[1].intValue();
                            lalr=timer.peek()[2].intValue();
                            free=timer.peek()[2].intValue() + mss*mss/W;
                            W = W+mss*mss/W;
                            timer.remove();
                            System.out.println("timer ack matches received ack; lasr = "+lasr+", free = "+free+", W = "+W);
                            break;
                        }
                        else{

                        }
                    }
                    else{
                        timer.clear();
                        W = mss;
                        free = mss;
                        //lar unchanged
                        System.out.println("timeout; lasr = "+lasr+", free = "+free+", W = "+W);
                        break;
                    }
                }
                catch(SocketTimeoutException e){
                    System.out.println(e.getMessage()+"\n The process will exit!");
                    System.exit(0);
                }
        
            }
        }
    }
}
