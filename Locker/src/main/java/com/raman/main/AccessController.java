package com.raman.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.raman.FileProtector.prompt.password.PasswordPromptController;
import com.raman.gui.toast.Toast;
import com.raman.gui.toast.Toast.ToastButton;

import hashing.Hashing;
import hashing.Hashing.Algorithms;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
		//Get an instance for the toast message.
		toast = Toast.getInstance();
		toast.setParentSatge(primaryStage, new ToastActionHandler());
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
	    	System.out.println("Error in password file creation.");
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


