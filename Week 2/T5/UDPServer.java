import java.net.*;
import java.io.*;
import java.util.*;

public class UDPServer{
    public static void main(String args[]){
    	DatagramSocket aSocket = null;
		String s;
		final int x = (int) (Math.random()*100);
		try{
	    	aSocket = new DatagramSocket(6789);
					// create socket at agreed port
			byte[] buffer = new byte[1000];
 			while(true){
 				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
  				aSocket.receive(request);

				int num = buffer[3];
				System.out.println(Arrays.toString(buffer));
				
				System.out.println("Random:"+x + " .....Input:"+num );
				
				if(num==x){
					s="CORRECT";
				}else if(num<x){
					s="HIGHER";
				}else{
					s="LOWER";
				}
				
				byte [] answer = s.getBytes();
				
    			DatagramPacket reply = new DatagramPacket(answer, answer.length,
    				request.getAddress(), request.getPort());
    			aSocket.send(reply);
    		}
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e) {System.out.println("IO: " + e.getMessage());
		}finally {if(aSocket != null) aSocket.close();}
    }
}
