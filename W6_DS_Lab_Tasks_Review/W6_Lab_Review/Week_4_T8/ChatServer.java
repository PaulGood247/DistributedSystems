import java.net.*;
import java.io.*;
import java.util.*;

public class ChatServer implements Runnable
{  
   
   // Array of clients	
	private ChatServerThread clients[] = new ChatServerThread[50];
	private ServerSocket server = null;
	private Thread       thread = null;
	private int clientCount = 0;
	private double currentBid = 0;
	private static String currentItem;
	static int x = 0;
	static ArrayList<Auction> a = new ArrayList<Auction>();

   public ChatServer(int port)
   {
	  try {

		 System.out.println("Binding to port " + port + ", please wait  ...");
         server = new ServerSocket(port);
         System.out.println("Server started: " + server.getInetAddress());
         start();
      }
      catch(IOException ioe)
      {
		  System.out.println("Can not bind to port " + port + ": " + ioe.getMessage());

      }
   }

   public void run()
   {			   	
	  while (thread != null)
      {
		 try{

			System.out.println("Waiting for a client ...");
            addThread(server.accept());

			int pause = (int)(Math.random()*3000);
			Thread.sleep(pause);

         }
         catch(IOException ioe){
			System.out.println("Server accept error: " + ioe);
			stop();
         }
         catch (InterruptedException e){
		 	System.out.println(e);
		 }
      }
   }

  public void start()
    {
		if (thread == null) {
		  thread = new Thread(this);
          thread.start();
       }
    }

   public void stop(){
	   thread = null;

   }

   private int findClient(int ID)
   {
	   for (int i = 0; i < clientCount; i++)
         if (clients[i].getID() == ID)
            return i;
      return -1;
   }

   public synchronized void broadcast(int ID, String input)
   {
	   //System.out.println("BID PERIOD: "+ bidPeriod);
	   	if(ChatServerThread.bidPeriod >= 10){
			System.out.println("TIMEOUT");
		}

	   if (input.equals(".bye")){
		  clients[findClient(ID)].send(".bye");
          remove(ID);
       }
       else
         for (int i = 0; i < clientCount; i++){
			if(clients[i].getID() != ID)
            	//clients[i].send(input); // sends messages to clients
				currentBid = Double.parseDouble(input);
				clients[i].send(currentItem);
				//resetbidperiod
				/*if(bidPeriod == 0){
					clients[i].send("Winner: " + findClient(ID) + " with price of "+ currentBid);
					x++;
				}*/
				
		}
		//System.out.println("Item: "+currentItem+ "\nCurrent Bid: "+ currentBid);
		
		notifyAll();
   }
   public synchronized void remove(int ID)
   {
	  int pos = findClient(ID);
      if (pos >= 0){
		 ChatServerThread toTerminate = clients[pos];
         System.out.println("Removing client thread " + ID + " at " + pos);

         if (pos < clientCount-1)
            for (int i = pos+1; i < clientCount; i++)
               clients[i-1] = clients[i];
         clientCount--;

         try{
			 toTerminate.close();
	     }
         catch(IOException ioe)
         {
			 System.out.println("Error closing thread: " + ioe);
		 }
		 toTerminate = null;
		 System.out.println("Client " + pos + " removed");
		 notifyAll();
      }
   }

   private void addThread(Socket socket)
   {
	  if (clientCount < clients.length){

		 System.out.println("Client accepted: " + socket);
         clients[clientCount] = new ChatServerThread(this, socket);
         try{
			clients[clientCount].open();
            clients[clientCount].start();
			clients[clientCount].send("Start: "+currentItem+ "\nCurrent Bid: "+ currentBid);
            clientCount++;
         }
         catch(IOException ioe){
			 System.out.println("Error opening thread: " + ioe);
		  }
	  }
      else
         System.out.println("Client refused: maximum " + clients.length + " reached.");
   }


   public static void main(String args[]) {
		
		
	   for (int i = 1; i <= 5; i++){
		   Auction au = new Auction("item"+i, (int) Math.floor(Math.random() * 101) , 0);
		   a.add(au);
	   }
	   System.out.println("Auction 5: "+a.get(4).item);
	   ChatServer server = null;
	   currentItem = a.get(x).item;
      if (args.length != 1)
         System.out.println("Usage: java ChatServer port");
      else
         server = new ChatServer(Integer.parseInt(args[0]));
   }

}