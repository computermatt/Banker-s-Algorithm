import java.util.ArrayList;

public class Driver 
{
	//Total number of resources the banker can allocate
	private final static int bankerResource = 4;
	
	//Number of client served by banker
	private final static int numClient = 2;
	
	//Max number of resource available for each Client
	private final static int clientResource = 6;
	
	//Number of requests from client
	private final static int clientRequest = 3;
	
	//min time for client to sleep after requesting/releasing resources
	private final static int minSleepMillis = 1000;
	
	//max time for client to sleep after requesting/releasing resources
	private final static int maxSleepMillis = 3000;

	public static void main(String[] args) 
	{

		ArrayList<Client> clients = new ArrayList<Client>(numClient);
		
		// Create the banker object.
		Banker banker = new Banker(bankerResource);
		
		// Create each of the clients based on global vars.
		for (int i = 0; i < numClient; i++) 
		{

			clients.add( 
					new Client ( "Client " + (i + 1),
					banker,
					(int)(clientResource * Math.random()) + 1,
					clientRequest,
					minSleepMillis,
					maxSleepMillis
					) );
		}	
		
		//Start the clients
		for(Client client: clients)
		{
			client.start();
		}
		
		
		//Kill the clients' processes.
		for (Client client: clients)
		{
			try
			{
				client.join();
			} 
			
			catch (InterruptedException ie) 
			{
				ie.printStackTrace();
			}
		}
	}
}
