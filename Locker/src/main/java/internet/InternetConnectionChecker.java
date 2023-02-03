package internet;

import java.util.concurrent.SubmissionPublisher;

public class InternetConnectionChecker 
{
	//Used to publish the current network status as boolean.
	private SubmissionPublisher<Boolean> publisher;
	private static InternetConnectionChecker instance;
	private InternetConnectionChecker()
	{
		publisher = new SubmissionPublisher<>();
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
	
	private boolean checkConnection()
	{
		try {
			InetAddress address = InetAddress.getByName("www.google.com");
			return address.isReachable(3500);
		}catch (UnknownHostException  e) {
			System.out.println("Host not found when checking connection.");
		}catch (Exception e){
			System.out.println("No internet to reach the given host.");
		}
		return false;
	}
}