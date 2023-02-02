package com.raman.FileProtector.prompt.password;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PasswordPromptController implements ChangeListener<String>
{
	private PasswordPromptView view;
	public static String password = "";
	
	public PasswordPromptController(Stage ownerWindow, EventHandler<ActionEvent> eventhander)
	{
		view = new PasswordPromptView(ownerWindow, eventhander, this);
	}

	public void show()
	{
		//Update the toast message parent stage and event handling
		view.showToast();
	}

	public void close()
	{
		view.close();
	}
	
	private boolean verfiyPassword()
	{
		int correctAnswers = 4;
		password = view.passwordField.getText();
		if(password.isEmpty())
		{
			cleanUp();
			return false;
		}
		
		if(password.length() < 8){
			correctAnswers--;
			view.updateColor(0, "red");
		}else
			view.updateColor(0, "green");
		
		if(!password.matches(".*[!@#\\$£%^\\-\\.\\,&\\*]+.*")){
			correctAnswers--;
			view.updateColor(3, "red");
		}else
			view.updateColor(3, "green");
		
		if(!password.matches(".*[A-Z]+.*")){
			correctAnswers--;
			view.updateColor(1, "red");
		}else
			view.updateColor(1, "green");
			
		if(!password.matches(".*[0-9]+.*")){
			correctAnswers--;
			view.updateColor(2, "red");
		}else
			view.updateColor(2, "green");
			
		return (correctAnswers == 4);
	}
	
	public String getPassword() 
	{
		return password;
	}
	
	@Override
	public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) 
	{
		if(verfiyPassword())
			view.btn_password_confirm.setDisable(false);
		else
			view.btn_password_confirm.setDisable(true);
	}	
	public Button getConfirmButton()
	{
		return view.btn_password_confirm;
	}
	public Button getCancelButton()
	{
		return view.btn__password_close;
	}	
	public void cleanUp()
	{
		view.reset();
	}	
	public Scene getScene()
	{
		return view.getScene();
	}
}
