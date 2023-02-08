package com.raman.FileProtector.prompt.initial;

import com.raman.FileProtector.MainController;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Prompt implements EventHandler<ActionEvent>
{
	private Inital_Window initialWindow;
	private MainController root;
	
	public Scene getInitialWindow()
	{
		//Create and prepare the welcome initial window.
		initialWindow = new Inital_Window(this);

		//Return the initial window to be displayed on the screen.
		return initialWindow.getScene();
	}

	@Override
	public void handle(ActionEvent event) 
	{
		Button button = (Button) event.getSource();
		Stage primairyStage = (Stage) initialWindow.getScene().getWindow();
		String taskChosen = "Encrypt";
		
		//Prepare the main application window.
		root = new MainController();
		if(button == initialWindow.btn_encrypt)
			root.isEncrption(true);
		if(button == initialWindow.btn_decrypt)
		{
			taskChosen = "Decrypt";
			root.isEncrption(false);
		}
		primairyStage.setScene(root.getScene());
		//Initiate the properties of the main application controller
		root.init(taskChosen);
	}
}
