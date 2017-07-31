import java.io.*;
import java.net.*;
import java.util.*;

public class Client
{
	private static InetAddress host;
	private static final int PORT = 1234;

	public static void main(String[] args)
	{
		try
		{
			host = InetAddress.getLocalHost();
		}
		catch(UnknownHostException uhEx)
		{
			System.out.println("\nHost ID not found!\n");
			System.exit(1);
		}
		sendMessages();
	}

	private static void sendMessages()
	{
		Socket socket = null;

		try
		{
			socket = new Socket(host,PORT);

			System.out.println("HERE");
			//Scanner networkInput =new Scanner(socket.getInputStream());
			//PrintWriter networkOutput =new PrintWriter(socket.getOutputStream(),true);
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

			//Set up stream for keyboard entry...
			Scanner userEntry = new Scanner(System.in);

			String message, response;
			Auction a;
			int x=0;
			System.out.println("HERE");
			do{
				System.out.println("HERE2");
				try{
					a = (Auction) ois.readObject();
					ois.reset();
					System.out.println("Test: ");
				}catch(Exception ex){
					System.out.println("Ex: "+ex);
				}
				x++;
			}while(x==0);
			do
			{
				System.out.println("HERE3");
				/*System.out.print(
							"Enter message ('QUIT' to exit): ");
				message =  userEntry.nextLine();
				networkOutput.println(message);
				response = networkInput.nextLine();
				System.out.println("\nSERVER> " + response);*/
				message="b";
			}while (!message.equals("QUIT"));
		}
		catch(IOException ioEx)
		{
			ioEx.printStackTrace();
		}

		finally
		{
			try
			{
				System.out.println("\nClosing connection...");
				socket.close();
			}
			catch(IOException ioEx)
			{
				System.out.println("Unable to disconnect!");
				System.exit(1);
			}
		}
	}
}

