package internet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public class InternetConnectionChecker 
{
	//Used to publish the current network status as boolean.
	private SubmissionPublisher<Boolean> publisher;
	private static InternetConnectionChecker instance;
	private Thread networkThread;
	private boolean latestConnectionState;
	
	private InternetConnectionChecker()
	{
		publisher = new SubmissionPublisher<>();
		init();
	}
	
	public static InternetConnectionChecker getInstance()
	{
		if(instance == null)
		{
			instance = new InternetConnectionChecker();
			return instance;
		}
		return instance;
	}
	
	//The checkConnection method is called to check the internet connection and publish the status.
		private void init() 
		{
			networkThread = new Thread(() -> 
			{
				while(!networkThread.isInterrupted())
				{
					boolean isConnected = checkConnection();
					if(isConnected != latestConnectionState)
					{
						latestConnectionState = isConnected;
						publisher.submit(isConnected);
					}
					try {
						Thread.sleep(1000);
					}catch (InterruptedException  e){
						System.out.println("Network thread is interrupted.");
						break;
					}
				}			
			});
			networkThread.start();
		}
	
	private boolean checkConnection()
	{
		try {
			InetAddress address = InetAddress.getByName("www.google.com");
			return address.isReachable(2000);
		}catch (UnknownHostException  e) {
			System.out.println("Host not found when checking connection.");
		}catch (Exception e){
			System.out.println("No internet to reach the given host.");
		}
		return false;
	}
	
	/* The getPublisher method is used to return the SubmissionPublisher instance, 
	which can be used by other classes to subscribe to the connection status updates. */
	public Flow.Publisher<Boolean> getPublisher()
	{
		return publisher;
	}
	
	public void stop()
	{
		//Interrupts the active thread. 
		networkThread.interrupt();
		//Issues onComplete signals to current subscribers informing the service is ended. 
		publisher.close();
	}
}