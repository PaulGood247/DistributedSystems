import java.net.*;
import java.io.*;
import java.util.Scanner;

public class AuctionClient implements Runnable
{  private Socket socket              = null;
   private Thread thread              = null;
   private BufferedReader  console   = null;
   private DataOutputStream streamOut = null;
   private AuctionClientThread client    = null;
   double currentBid=0;

   public AuctionClient(String serverName, int serverPort)
   {
	  System.out.println("Establishing connection. Please wait ...");

      try{
		 socket = new Socket(serverName, serverPort);
         System.out.println("Connected: " + socket);
         start();
      }
      catch(UnknownHostException uhe){
		  System.out.println("Host unknown: " + uhe.getMessage());
	  }
      catch(IOException ioe){
		  System.out.println("Unexpected exception: " + ioe.getMessage());
	  }
   }
   
   public void run()
   {
	   
	   while (thread != null){
		 try {
			//String message = chatName + " > " + console.readLine();
			Scanner sc =  new Scanner(System.in);
			Double message;
			do{
				//System.out.print("Enter a higher bid: ");
				message = sc.nextDouble();

				if(message <= currentBid){
					System.out.print("Enter a higher bid: ");
				}
				
				
			}while(message <= currentBid ); //ensure higher than current bid 
		
			streamOut.writeUTF(""+message);
			
			streamOut.flush();
			
         }
         catch(IOException ioe)
         {  System.out.println("Sending error: " + ioe.getMessage());
            stop();
         }
      }
   }
   
   public void handle(String msg)
   {
	   //System.out.println("MSG-in: "+msg);
	   if (msg.equals(".bye")) //server sent to instruct end of auction
	   {  
			System.out.println("Auction finshed!");
			stop();
			System.exit(0); //exit program
		}else{
			if(msg.contains("bid")){ //this message contains the new bid amount 
				String splits[] =msg.split(":");
				currentBid = Double.parseDouble(splits[1]); //get the bid from the message
			}else if(msg.contains("SOLD")){
				String splits[] =msg.split(":");
			}
			
			System.out.println("\n"+msg); //print out the message 
		}	
   }


   public void start() throws IOException
   {
	  console = new BufferedReader(new InputStreamReader(System.in));

      streamOut = new DataOutputStream(socket.getOutputStream());
      if (thread == null)
      {  client = new AuctionClientThread(this, socket);
         thread = new Thread(this);
         thread.start();
      }
   }

   public void stop()
   {
      try
      {  if (console   != null)  console.close();
         if (streamOut != null)  streamOut.close();
         if (socket    != null)  socket.close();
      }
      catch(IOException ioe)
      {
		  System.out.println("Error closing ...");

      }
      client.close();
      thread = null;
   }


   public static void main(String args[])
   {  AuctionClient client = null;
      if (args.length != 2)
         System.out.println("Usage: java AuctionClient host port");
      else
         client = new AuctionClient(args[0], Integer.parseInt(args[1]));
   }
}
