package com.raman.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.raman.FileProtector.prompt.password.PasswordPromptController;
import com.raman.gui.toast.Toast;
import com.raman.gui.toast.Toast.ToastButton;

import hashing.Hashing;
import hashing.Hashing.Algorithms;
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
	private final int tnumberOfAttempts = 2;
	
	public AccessController(Stage primaryStage)
	{	
		passwordController = new PasswordPromptController(primaryStage, this);
		hasher = new Hashing(Algorithms.SHA_512);
		//Initiate the toast message
		toast = Toast.getInstance();
		toast.setParentSatge(primaryStage, new ToastActionHandler());
		//Create list of allowed passwords
		createPasswordFiles();
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
			System.out.println("Close");
		if(button == passwordController.getConfirmButton())
			System.out.println("Confirm");
	}
	
	private boolean isAdmin(String password)
	{
		String admin = "Code.me231!";
		return password.equals(admin);
	}
	
	/**
	 * <h1> Analizing and Responding to User Password </h1>
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
		if(verfiyPassword(getUserPassword))
		{
			//Password is correct start the application
			Application_Entry.getInstance().initaiteApplication();
			return;
		}
		//Clean the current user inputs
		passwordController.cleanUp();
		//Define the details of the toast message.
		showToastMessage("Incorrect Password", "The password entered doesn't seem to be valid, seek the developer.",
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
		//Hashing user password.
		String hashedPassword = hashPassword(password);
		//Indexer to locate the index number of the correct password within the list.
		int indexer = 0;
		//Maping over the list of hashed passwords along to the remaining allowed attempts as integer.
		for(Map.Entry<String, Integer> passwordFile : getHashesFromFile().entrySet())
		{
			if(passwordFile.getKey().equals(hashedPassword))
			{
				if(passwordFile.getValue() <= 0)
				{
					showToastMessage("Expired Credtional", "You have used enough of the granted previllage, seek the developer for extention.",
							new ToastButton[]{ToastButton.OK});
					return false;
				}					
				indexer++;
				//Decrease the number of attempts
				decreaseNumberOfAttempts(indexer, (passwordFile.getValue() - 1));
				return true;
			}
		}
		return false;
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
		return BinarySystemConverter.convert_bytes_array_to_hex(hasher.get_hash(userPassword));
	}
	
	/**
	 * <h1> Creating Local Passwords Storing File </h1>
	 * <p> 
	 * 		On the initial of the program, this method is triggered to create a plain text file.
	 * 		The file would be filled with hashed passwords followed by the number of remaining
	 * 		attempts to be valid.
	 * </p>
	 */
	private void createPasswordFiles()
	{
		File file = new File("binary.txt");
	    try {
	      if (!file.exists()) 
	      {
	    	  FileWriter writer = new FileWriter(file);
	    	  for(String hashedPassword: getPasswords())
	    	  {
	    		  writer.write(hashedPassword + "," + tnumberOfAttempts + "\n");
	    	  }
	    	  writer.flush();
	    	  writer.close();	    	  
	      } 
	    } catch (IOException e) {
	    	showToastMessage("Error", "Credtionals seems to be faild at the building stage, seek the developer.",
					new ToastButton[]{ToastButton.OK});
	      e.printStackTrace();
	    }
	}
	
	/**
	 * <h1> Generating Timed Passwords </h1>
	 * <p> 
	 * 		On call to this method, a list of strong hard coded passwords will be hashed with SHA-512 algorithm.
	 * 		This method is only called internally during the creation of passwords file process normally called
	 * 		once only unless the file is removed and the program had to regenerate it.
	 * </p>
	 * @return String[] The list of hashed passwords.
	 */
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
	
	/**
	 * <h1> Loading Hashed Password </h1>
	 * <p> 
	 * 		On every program launch, the method is triggered to read and load hashed passwords from the local file.
	 * 		The loaded list is stored in the private HashMap property.
	 * </p>
	 * @return HashMap<String, Integer> String value represents the hash and integer the number of remaining attempts.
	 */
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
	
	/**
	 * <h1> Extracting Remaining Attempts</h1>
	 * <p> 
	 * 		This function is called during the hash reading process of the local file. It passed with a single line, this
	 * 		line is filtered to extract and return the last integer value by the end of the line.
	 * </p>
	 * @param String The hash line.
	 * @return int The remaining attempts of given hash.
	 */
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
	
	/**
	 * <h1> Decrease Remaining Attempts</h1>
	 * <p> 
	 * 		On every valid password entry taking place this method is called to reduce the total remaining attempts by 1.
	 * 		It takes the index value representing the line of which the password is used and the new reduced attempts.
	 * </p>
	 * @param int The index of target line within the file.
	 * @param int The new number of allowed password usage.
	 * @return int The remaining attempts of given hash.
	 */
	private void decreaseNumberOfAttempts(int index, int attempts)
	{
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
	
	private void showToastMessage(String title, String message, ToastButton[] buttons)
	{
		//Define the details of the toast message.
		toast.loadToast("HiIIII", message, buttons);
		toast.show();
	}
	
	// ############################# Toast Handler #############################
	class ToastActionHandler implements EventHandler<ActionEvent>
	{
		@Override
		public void handle(ActionEvent event) 
		{
			Button button = (Button) event.getSource();
			if(toast.getExitButton() == button || toast.getRetryButton() == button || toast.getOKButton() == button){
				toast.hideToast();
			}	
		}
	}
}


