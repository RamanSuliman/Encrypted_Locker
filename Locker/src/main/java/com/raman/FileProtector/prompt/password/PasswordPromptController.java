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
	private static final String wrong = "89, 19, 32";
	private static final String correct = "2, 150, 10";
	
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
			view.updateColor(0, wrong);
		}else
			view.updateColor(0, correct);
		
		if(!password.matches(".*[!@#\\$£%^\\-\\.\\,&\\*]+.*")){
			correctAnswers--;
			view.updateColor(3, wrong);
		}else
			view.updateColor(3, correct);
		
		if(!password.matches(".*[A-Z]+.*")){
			correctAnswers--;
			view.updateColor(1, wrong);
		}else
			view.updateColor(1, correct);
			
		if(!password.matches(".*[0-9]+.*")){
			correctAnswers--;
			view.updateColor(2, wrong);
		}else
			view.updateColor(2, correct);
			
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
