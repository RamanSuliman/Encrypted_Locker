package com.raman.main;

import com.raman.FileProtector.prompt.password.PasswordPromptController;
import com.raman.gui.toast.Toast;
import hashing.Hashing;
import hashing.Hashing.Algorithms;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AccessController implements EventHandler<ActionEvent>
{
	private PasswordPromptController passwordController;
	private Hashing hasher;
	private Toast toast;
	
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


