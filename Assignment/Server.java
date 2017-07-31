import java.io.*;
import java.net.*;
import java.util.*;

public class Server
{
	private static ServerSocket serverSocket;
	private static final int PORT = 1234;

	public static void main(String[] args) throws IOException
	{
		try
		{
			serverSocket = new ServerSocket(PORT);
		}
		catch (IOException ioEx)
		{
			System.out.println("\nUnable to set up port!");
			System.exit(1);
		}

		do
		{
			//Wait for client...
			Socket client = serverSocket.accept();

			System.out.println("\nNew client accepted.\n");

			//Create a thread to handle communication with
			//this client and pass the constructor for this
			//thread a reference to the relevant socket...
			ClientHandler handler = new ClientHandler(client);

			handler.start();//As usual, this method calls run.
		}while (true);
	}
}

class ClientHandler extends Thread
{
	private Socket client;
	private Scanner input;
	private PrintWriter output;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	public static Auction a;
	public static int x;
	public static int i=0;
	public static String[] item = {"Item1","Item2","Item3","Item4", "Item5"};
	public static int[] itemVal = {4,1,3,2, 3};
	public static int currentBid = 0;

	public ClientHandler(Socket socket)
	{
		//Set up reference to associated socket...
		client = socket;
		a = new Auction(item[i], itemVal[i], currentBid);
		
		//a = new Auction("Item", 0 ,0);
		
		try
		{
			//input = new Scanner(client.getInputStream());
			
			//output = new PrintWriter(client.getOutputStream(),true);
			System.out.println("HERE£");
			ois = new ObjectInputStream(client.getInputStream());
			oos = new ObjectOutputStream(client.getOutputStream());
			System.out.println("HERE£");
		}
		catch(IOException ioEx)
		{
			ioEx.printStackTrace();
		}
	}

	public void run()
	{
		String received;
		x=0;
		System.out.println("HERE");
		do{
			System.out.println("HERE");
			try{
				oos.writeObject(a);
				oos.flush();
			}catch(Exception ex){
				System.out.println("Ex: "+ex);
			}

			x++;
		}while(x==0);
		do
		{
			System.out.println("HERE1");
			//Accept message from client on
			//the socket's input stream...
			//received = input.nextLine();
			received="a";
			//System.out.println("Received: "+ received);
			//Echo message back to client on
			//the socket's output stream...
			//output.println("ECHO: " + received);

		//Repeat above until 'QUIT' sent by client...
		}while (!received.equals("QUIT")&& x>0);

		try
		{
			if (client!=null)
			{
				System.out.println(
							"Closing down connection...");
				client.close();
			}
		}
		catch(IOException ioEx)
		{
			System.out.println("Unable to disconnect!");
		}
	}
}
