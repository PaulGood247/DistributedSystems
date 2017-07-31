import java.net.*;
import java.io.*;
import java.util.*;

public class AuctionServer implements Runnable
{  
   
   private AuctionServerThread clients[] = new AuctionServerThread[50];
   private ServerSocket server = null;
   private Thread       thread = null;
   private int clientCount = 0;
   private int auctionCount = 0;
   static int seconds=60; 
   static int iSec=0;
   static ArrayList<Auction> a = new ArrayList<Auction>();
   static ArrayList<Auction> a2 = new ArrayList<Auction>();
   
   public void runTimer(){
		
		TimerTask task = new TimerTask() //timer task to handle timing each auction
		{
						
			public void run() //run in background
			{
			   iSec++;

			   if(iSec % seconds == 0){ //do this when timer reaches 0
				   System.out.println("Next Auction"); 
					if(a.get(auctionCount).getHighestBid() < a.get(auctionCount).getReserve()){ //hasnt been sold
						a.get(auctionCount).setSold(false);
					}else{ //has been sold
						a.get(auctionCount).setSold(true); 
						broadcast(a.get(auctionCount).getBuyer(), 
						"SOLD!\n Item: "+a.get(auctionCount).getItem()
						+"\nBuyer : "+ a.get(auctionCount).getBuyer() 
						+ "\nBid : "+ a.get(auctionCount).getHighestBid()+"\n"); //broadcast auction information to everyone but the user who won 
						a2.add(a.get(auctionCount)); //add to sold/temp arraylist
						a.remove(auctionCount); //remove from auction as its been sold
						auctionCount--;
					}
					
					if(auctionCount==a.size()-1){
						auctionCount=-1; //set to -1 as it will be incremented when it goes to next auction so now it will be the 0th element of the arraylist
					}
					
					System.out.println("AUCTIONCOUNT: "+auctionCount);
					System.out.println(Arrays.toString(a.toArray()));
					
					if(a.size()<=0){ //if none left 
						finishAuction(); 
					}else{
					
						auctionCount++;
						
						broadcast(0, "\n--------\nNEXT AUCTION\n--------\n");
						broadcast(0, "Item: "+a.get(auctionCount).getItem());
						broadcast(0,"Reserve Price bid: "+a.get(auctionCount).getReserve());
					}
					
			   }
			   else{
				   System.out.print("Time left:" + (seconds - (iSec %seconds))+"\r" ); //print out the time left to the server
			   }
			}
		};

		if(a.size()!=0){
						
			Timer timer = new Timer();
		
			timer.schedule(task, 10000, 1000); //delay for 10000milis or 10 secs to allow time for clients to connect at first, and go every second
		}
		 
    }
   

   public AuctionServer(int port)
   {
	   if(a.size()!=0){
		this.runTimer();
	   }
	   
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
	
	public void finishAuction(){
		System.out.println("DONE");
		broadcast(0, ".close"); //this will stop all clients
		stop();
		//show items, finsh price and buyers
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
	   System.out.println("INPUT: "+input);
	   if (input.equals(".bye")){ //if this method receives .bye
		  clients[findClient(ID)].send(".bye"); 
          remove(ID); //remove this client
       }else if(input.equals(".close")){
		   for (int i = 0; i < clientCount; i++){ //loop through all clients
			   for(int j =0; j< a2.size(); j++){ //loop through the sold items array
				   if(clients[i].getID() == a2.get(j).getBuyer()){ //if this client is the buyer
					 clients[i].send("You bought "+ a2.get(j).getItem() +" for "+ a2.get(j).getHighestBid()+"euro\n");  
				   }
			   }
		   }
		   
		   for (int i = 0; i < clientCount; i++){
			  clients[i].send(".bye");
		   }
		   
		   System.out.println("Auction has finished. Closing server...");
			thread.stop();
			System.exit(0);
		   
	   }
	   else
	   {	
			for (int i = 0; i < clientCount; i++){
				if(input.contains("SOLD")){ 
					if(clients[i].getID() == ID){ //send this instead of input to the user who won the auction
						clients[i].send("YOU WON THIS AUCTION!\nItem: "+a.get(auctionCount).getItem()
						+"\nBuyer : "+ a.get(auctionCount).getBuyer() 
						+ "\nBid : "+ a.get(auctionCount).getHighestBid()+"\n");
					}else{
						clients[i].send(input); // send message to everyone about who won the auction
					}
				}else if(input.contains("NEXT") || input.contains("Item") || input.contains("Reserve")){
					clients[i].send(input);
				}else{
					//resetbidperiod
					iSec=0;
					a.get(auctionCount).setHighestBid(Double.parseDouble(input)); //reset highestbid
					a.get(auctionCount).setBuyer(ID);
					if(clients[i].getID() == ID){
						
						clients[i].send("My bid: "+a.get(auctionCount).getHighestBid()); //show the users bid
					}else{
						clients[i].send(ID + " bids: " + a.get(auctionCount).getHighestBid()); //some bidded, notify other clients
					}
					clients[i].send("Time Remaining: "+ (seconds - (iSec %seconds))+"seconds"); //tell the user the time remaining
				}
			}
	    }
	     
	notifyAll();		
        
   }
   
   public synchronized void remove(int ID)
   {
	  int pos = findClient(ID);
      if (pos >= 0){
		 AuctionServerThread toTerminate = clients[pos];
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
         clients[clientCount] = new AuctionServerThread(this, socket);
         try{
			clients[clientCount].open();
            clients[clientCount].start();
			clients[clientCount].send("Item: "+a.get(auctionCount).getItem());
			if(a.get(auctionCount).getReserve() >= a.get(auctionCount).getHighestBid()){ //if the reserve is higher than the highestbid
				clients[clientCount].send("Reserve Price bid: "+a.get(auctionCount).getReserve()); //send the reserve
			}else{ //otherwise
				clients[clientCount].send("Current Highest bid: "+a.get(auctionCount).getHighestBid()); //send the highest bid set by another client
			}
			clients[clientCount].send("Time Remaining: "+ (seconds - (iSec %seconds))+"seconds"); //tell the new client what time is left for this auction
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
		   Auction au = new Auction("item"+i, ((int) Math.floor(Math.random() * 101)) , 0); //populate the arraylist
		   a.add(au);
	   }
	   System.out.println(Arrays.toString(a.toArray()));
	   AuctionServer server = null;
      if (args.length != 1)
         System.out.println("Usage: java AuctionServer port");
      else
         server = new AuctionServer(Integer.parseInt(args[0]));
	 
   }

}