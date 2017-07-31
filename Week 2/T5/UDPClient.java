import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

public class UDPClient{
    public static void main(String args[]){
		// args give message contents and destination hostname
		DatagramSocket aSocket = null;
		DatagramPacket reply =null;
		try {
			aSocket = new DatagramSocket();
			//byte [] m = args[0].getBytes();
			Scanner sc = new Scanner(System.in);
			do{
				System.out.print("Enter number: ");
				int x =sc.nextInt();
				
				byte [] m = ByteBuffer.allocate(4).putInt(x).array();
				InetAddress aHost = InetAddress.getByName(args[0]);
				int serverPort = 6789;
				DatagramPacket request =
					new DatagramPacket(m,  m.length, aHost, serverPort);
				aSocket.send(request);
				byte[] buffer = new byte[1000];
				reply = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(reply);
				System.out.println("Reply: " + new String(reply.getData()));
			}while(!new String(reply.getData()).equals("CORRECT"));
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e){System.out.println("IO: " + e.getMessage());
		}finally {if(aSocket != null) aSocket.close();}
	}
}
