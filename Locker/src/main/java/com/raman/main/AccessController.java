package com.raman.main;

import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;

import com.raman.FileProtector.prompt.password.PasswordPromptController;
import com.raman.gui.toast.Toast;
import com.raman.gui.toast.Toast.ToastButton;

import database.DatabaseConnector;
import database.User;
import hashing.Hashing;
import hashing.Hashing.Algorithms;
import internet.InternetConnectionChecker;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import raman.Converter.BinarySystemConverter;

public class AccessController implements EventHandler<ActionEvent>
{
	private PasswordPromptController passwordController;
	private Hashing hasher;
	private Toast toast;
	private boolean isInternetAvaliable;
	private DatabaseConnector database;
	private String admin;
	
	public AccessController(Stage primaryStage)
	{	
		//Initiate the toast message
		toast = Toast.getInstance();
		toast.setParentSatge(primaryStage, new ToastActionHandler());
		//Initiate the internet listener
		InternetConnectionChecker networkChecker = InternetConnectionChecker.getInstance();
		networkChecker.getPublisher().subscribe(new InternetConnectionSnitcher());
		//Esnure there is an internet conenction prior launching the program.
		checkNetworkConnection();
		//Connect and create an instance of database.
		database = new DatabaseConnector();
		//Once internet is granted, load the password pormpt.
		passwordController = new PasswordPromptController(primaryStage, this);
		hasher = new Hashing(Algorithms.SHA_512);
		//Before running any database queries, must check the connection state.
		checkDatabaseConnection();
		//Generate list of passwords
		//generatePasswords();
		//Retrive admin credentials
		getAdminCredential();
	}
	
	public Scene getScene()
	{
		return passwordController.getScene();
	}
	
	@Override
	public void handle(ActionEvent event) 
	{
		Button button = (Button) event.getSource();
		if(button == passwordController.getCancelButton())
			System.exit(0);
		if(button == passwordController.getConfirmButton())
			checkUserPasswordAndLoadApplication();
	}
	
	/**
	 * <h1> Analyzing and Responding to User Password </h1>
	 * <p> 
	 * 		Once the user provided a valid password in the prompt and click confirm, this method has the duty of validation.
	 * 		On password correction it loads the program main entry screen otherwise an toasted error message is thrown.
	 * </p>
	 * @param String The plain text password.
	 * @return boolean Whether the password is valid or not.
	 */
	private void checkUserPasswordAndLoadApplication()
	{
		String getUserPassword = passwordController.getPassword();
		//Hashing user password.
		String hashedPassword = hashPassword(getUserPassword);
		//Validate user password.
		if(verfiyPassword(hashedPassword))
		{
			//Check if the password has any attempts remaining;
			if(database.getPasswordAttempts(hashedPassword) > 0)
			{
				//Decrease the password attempt by 1;
				if(!database.updatePasswordAttempt(hashedPassword))
				{
					showToastMessage("Attempts Update", "An error occured, please try relaunching with trying with same password if "
							+ "same error showed then contact the developer.", new ToastButton[] {ToastButton.OK});
					System.exit(0);
					return;
				}
			}else {
				showToastMessage("Expired Credtional", "You have used enough of the granted privilege, seek the developer for an extension.",
						new ToastButton[]{ToastButton.OK});
				passwordController.cleanUp();
				return;
			}
			//Password is correct start the application
			Application_Entry.getInstance().initaiteApplication();
			return;
		}
		//Clean the current user inputs
		passwordController.cleanUp();
		//Define the details of the toast message.
		showToastMessage("Incorrect Password", "The password entered doesn't seem to be valid re-try or seek the developer.",
				new ToastButton[]{ToastButton.RERTRY});
	}	

	/**
	 * <h1> Password Verification </h1>
	 * <p> 
	 * 		A plain text is required for this method to compute and validate the given password correction.
	 * </p>
	 * @param String The plain text password.
	 * @return boolean Whether the password is valid or not.
	 */
	private boolean verfiyPassword(String password)
	{	
		//If given password is Admin authority.
		if(isAdmin(password))
			return true;
		if(database.checkIfPasswordExist(password))
			return true;
		return false;
	}
	
	private void checkNetworkConnection()
	{
		try {
			Thread.sleep(2000);
			while(!isInternetAvaliable)
				showToastMessage("Require Internet Connection", "You are offline, internet conenction is required to processed.",
						new ToastButton[]{ToastButton.RERTRY});
			} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	private void checkDatabaseConnection()
	{
		while(!database.isConnected())
		{
			showToastMessage("Server is down!", "You are offline or issue occured in the server, try later or contact the developer.",
					new ToastButton[]{ToastButton.OK});
			System.exit(0);
		}
	}
	
	private void getAdminCredential()
	{
		if(database.isConnected())
			return;
		admin = database.getAdminPassword();
		if(admin.isEmpty() || admin == null)
		{
			checkNetworkConnection();
			getAdminCredential();
		}
	}
	
	private boolean isAdmin(String password)
	{
		return password.equals(admin);
	}
	
	/**
	 * <h1> Hashing Plain Password </h1>
	 * <p> 
	 * 		On every password hashing task is method is triggered, it takes in a plain text and return a hexa hashed value.
	 * 		The initial hashing process returns an array of byte[] which is then converted into hex format.
	 * </p>
	 * @param String The plain text password.
	 * @return String The hexadecimal value of the hash.
	 */
	private String hashPassword(String userPassword)
	{
		return BinarySystemConverter.convert_bytes_array_to_hex(hasher.get_hash(userPassword.strip()));
	}
	
	private void showToastMessage(String title, String message, ToastButton[] buttons)
	{
		//Define the details of the toast message.
		toast.loadToast("Privacy Locker", message, buttons);
		toast.show();
	}
	
	// ############################# Toast Handler #############################
	class ToastActionHandler implements EventHandler<ActionEvent>
	{
		@Override
		public void handle(ActionEvent event) 
		{
			Button button = (Button) event.getSource();
			if((!isInternetAvaliable || database == null || !database.isConnected()) && toast.getExitButton() == button)
				System.exit(0);
			else
				if(toast.getExitButton() == button || toast.getRetryButton() == button || toast.getOKButton() == button)
					toast.hideToast();
		}
	}
	
	// ############################# Will receive the connection status updates from SubmissionPublisher. #############################
	class InternetConnectionSnitcher implements Flow.Subscriber<Boolean>
	{
		@Override
		public void onSubscribe(Subscription subscription) 
		{
			subscription.request(Long.MAX_VALUE);
		}

		//On receival of status update, the onNext method is called
		@Override
		public void onNext(Boolean isConnected) 
		{
			isInternetAvaliable = isConnected;
			System.out.println(isConnected);
			if(!isConnected)
			{
				Platform.runLater(()->{
					checkNetworkConnection();
				});
			}
		}

		@Override
		public void onError(Throwable throwable) {}

		@Override
		public void onComplete() {}	
	}
	
	private void generatePasswords()
	{
		String[] passwords = new String[]{"Delegator.4861", "Raman231!", "Aras231!", "Raskon231!", "Ramon231!", "Kristo231!",
				"Omar231!", "Ahmad231!", "Adam231!", "Mohammad231!", "Coder231!", "Privacy231!",
				"Monday231!", "Helen231!", "Jaber231!", "Java231!", "Salford231!", "Manchester231!",
				"London231!", "Syria231!", "Kurd231!", "University231!","Sara231!", "Kotlin231!", "PHPP231!", "Swift231!", "Test231!"};
		
		String chipherText = "";
		
		for(int i = 0; i < passwords.length; i++)
		{
			chipherText = hashPassword(passwords[i]);
			if(!database.insertNewUser(new User(passwords[i], chipherText)))
			{
				System.out.println("Issue with adding new user");
				return;
			}			
		}		
	}
}


/*
 * ############################### Plain Text old solution ##################################
	
		public AccessController(Stage primaryStage)
	{	
		//Initiate the toast message
		toast = Toast.getInstance();
		toast.setParentSatge(primaryStage, new ToastActionHandler());
		//Initiate the internet listener
		InternetConnectionChecker networkChecker = InternetConnectionChecker.getInstance();
		networkChecker.getPublisher().subscribe(new InternetConnectionSnitcher());
		//Esnure there is an internet conenction prior launching the program.
		checkNetworkConnection();
		//Once internet is granted, load the password pormpt.
		passwordController = new PasswordPromptController(primaryStage, this);
		hasher = new Hashing(Algorithms.SHA_512);
		//Create list of allowed passwords
		createPasswordFiles();
		//Load hashedValues;
		listHashedPasswords = getHashesFromFile();

	}
	

		 * <h1> Password Verification </h1>
	 * <p> 
	 * 		A plain text is required for this method to compute and validate the given password correction.
	 * </p>
	 * @param String The plain text password.
	 * @return boolean Whether the password is valid or not.
	private boolean verfiyPassword(String password)
	{
		//If given password is Admin authority.
		if(isAdmin(password))
			return true;
		//Hashing user password.
		String hashedPassword = hashPassword(password);
		//Indexer to locate the index number of the correct password within the list.
		int indexer = listHashedPasswords.size();
		//Maping over the list of hashed passwords along to the remaining allowed attempts as integer.
		for(Map.Entry<String, Integer> passwordFile : listHashedPasswords.entrySet())
		{
			if(passwordFile.getKey().equals(hashedPassword))
			{
				if(passwordFile.getValue() < 1)
				{
					showToastMessage("Expired Credtional", "You have used enough of the granted previllage, seek the developer for extention.",
							new ToastButton[]{ToastButton.OK});
					return false;
				}		
				//Decrease the number of attempts
				decreaseNumberOfAttempts(indexer, (passwordFile.getValue() - 1));
				return true;
			}
			indexer--;
			System.out.println("---Key " + passwordFile.getKey() + " " + passwordFile.getValue());
		}
		return false;
	}

		
	 * <h1> Creating Local Passwords Storing File </h1>
	 * <p> 
	 * 		On the initial of the program, this method is triggered to create a plain text file.
	 * 		The file would be filled with hashed passwords followed by the number of remaining
	 * 		attempts to be valid.
	 * </p>
	private void createPasswordFiles()
	{
		File file = new File("binary.txt");
	    try {
	      if (!file.exists()) 
	      {
	    	  FileWriter writer = new FileWriter(file);
	    	  for(String hashedPassword: getPasswords())
	    		  writer.write(hashedPassword + "," + tnumberOfAttempts + "\n");
	    	  writer.flush();
	    	  writer.close();	    	  
	      } 
	    } catch (IOException e) {
	    	showToastMessage("Error", "Credtionals seems to be faild at the building stage, seek the developer.",
					new ToastButton[]{ToastButton.OK});
	      e.printStackTrace();
	    }
	}
	
	 * <h1> Generating Timed Passwords </h1>
	 * <p> 
	 * 		On call to this method, a list of strong hard coded passwords will be hashed with SHA-512 algorithm.
	 * 		This method is only called internally during the creation of passwords file process normally called
	 * 		once only unless the file is removed and the program had to regenerate it.
	 * </p>
	 * @return String[] The list of hashed passwords.
	private String[] getPasswords()
	{
		String[] passwords = new String[]{"Password231!", "Test231!"};
		
		String plaintext = "";
		
		for(int i = 0; i < passwords.length; i++)
		{
			//Hashing the password at given index. 
			byte[] cipher = hasher.get_hash(passwords[i]);
			//Converting the hashed value into hex format.
			plaintext = BinarySystemConverter.convert_bytes_array_to_hex(cipher);
			//Replacing the plain text password with the hashed version.
			passwords[i] = plaintext;
		}		
		return passwords;
	}
	
	 * <h1> Loading Hashed Password </h1>
	 * <p> 
	 * 		On every program launch, the method is triggered to read and load hashed passwords from the local file.
	 * 		The loaded list is stored in the private HashMap property.
	 * </p>
	 * @return HashMap<String, Integer> String value represents the hash and integer the number of remaining attempts.
	private Map<String, Integer> getHashesFromFile()
	{
		File file = new File("binary.txt");
		if (file.exists()) 
		{
			BufferedReader reader;
			String line = "";
			try 
			{
				reader = new BufferedReader(new FileReader(file));
				Map<String, Integer> passwords = new HashMap<String, Integer>();
				while((line = reader.readLine()) != null)
				{
					int attempts = fetchNumberOfAttempts(line);
					//Remove the attempts from the line
					line = line.substring(0,line.lastIndexOf(","));
					passwords.put(line, attempts);
				}
				reader.close();
				return passwords;
			} catch (IOException e) {
				showToastMessage("Credtional Error", "Configuration files have been curropted, how about asking for some help?",
						new ToastButton[]{ToastButton.OK});
				e.printStackTrace();
			}
		}
		return null;
	}
	
	 * <h1> Extracting Remaining Attempts</h1>
	 * <p> 
	 * 		This function is called during the hash reading process of the local file. It passed with a single line, this
	 * 		line is filtered to extract and return the last integer value by the end of the line.
	 * </p>
	 * @param String The hash line.
	 * @return int The remaining attempts of given hash.
	private int fetchNumberOfAttempts(String passowrd)
	{
		try {
		    int lastCommaIndex = passowrd.lastIndexOf(",");
		    if (lastCommaIndex == -1) 
		    	return 0;
		    else 
		    {
		      String attempts = passowrd.substring(lastCommaIndex + 1);
		      return Integer.parseInt(attempts);
		    }
		}catch (Exception e) {
			showToastMessage("Counter Missing", "Configuration files have been curropted, try loading the program from scratch.",
					new ToastButton[]{ToastButton.OK});
		}
		return 0;
	}
	

	 * <h1> Decrease Remaining Attempts</h1>
	 * <p> 
	 * 		On every valid password entry taking place this method is called to reduce the total remaining attempts by 1.
	 * 		It takes the index value representing the line of which the password is used and the new reduced attempts.
	 * </p>
	 * @param int The index of target line within the file.
	 * @param int The new number of allowed password usage.
	 * @return int The remaining attempts of given hash.
	private void decreaseNumberOfAttempts(int index, int attempts)
	{
		System.out.println("Index " + index + " Attemopts: " + attempts);
		File file = new File("binary.txt");
	    try (BufferedReader reader = new BufferedReader(new FileReader(file))) 
	    {
	      String line;
	      int currentLine = 1;
	      StringBuilder fileContent = new StringBuilder();
	      while ((line = reader.readLine()) != null) 
	      {
		        if (currentLine == index) 
		        {
		        	//Update the number of attempts
		        	int attemptsIndex = line.lastIndexOf(",");
		        	line = line.substring(0, attemptsIndex + 1) + attempts;
		        }
		        //Add the line into string builder with a new line.
		        fileContent.append(line).append(System.lineSeparator());
		        currentLine++;
	      }
	      try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
	    	  writer.write(fileContent.toString());
	      }
	    } catch (IOException e) {
	    	showToastMessage("Counter Reduction", "Configuration files are missing core data, report this error.",
					new ToastButton[]{ToastButton.OK});
	      e.printStackTrace();
	    }
	}

 ***/

