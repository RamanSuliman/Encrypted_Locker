package com.raman.gui.toast;

import java.util.ArrayList;
import java.util.List;

import com.raman.fxfunctions.ButtonService;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Toast
{
	private ToastView view;
	private static Toast instance = null;
	public enum ToastButton {OK,CANCEL,RERTRY};
	
	private Toast()
	{
		view = new ToastView();	
	}
	
	public static Toast getInstance()
	{	
		//If the instance of the toast is never made and current toast stage parent isn't similar to the new parent Stage, create a toast instance.
		if(instance == null)
		{
			instance = new Toast();
		}
		return instance;
	}
	
	public void setParentSatge(Stage ownerWindow, EventHandler<ActionEvent> eventHandler)
	{
		view.setParentStage(ownerWindow);
		setButtonHandlers(eventHandler);
	}
	
	public void loadToast(String title, String message, ToastButton[] buttonsToDisplay)
	{
		//Add the buttons user has defined to be displayed.
		chooseButtonDisabilities(buttonsToDisplay);
		//Validate message and title of the toast.
		if(title.length() > 21 || message.length() > 500)
			throw new IllegalArgumentException("Outbound: The title max is 50 charachters"
					+ " with message max 500 and min 5.");
		if(title.length() < 4 || message.length() < 5)
			throw new IllegalArgumentException("Less: The title min is 4 charachters"
					+ " with message max and min 5.");
		view.loadToast(title, message);
	}
	
	private void chooseButtonDisabilities(ToastButton[] buttonsToDisplay)
	{
		if(buttonsToDisplay.length > 0)
		{
			//Convert list of user-defined buttons types into toast buttons.
			List<Button> visibleButtons = convertButtonTypesIntoButtons(buttonsToDisplay);
			for(Button button : view.getButtons())
			{	
				if(button == view.btn_exit)
					continue;
				if(visibleButtons.contains(button))
					ButtonService.setButtonVisibilty(button, true);
				else
					ButtonService.setButtonVisibilty(button, false);
			}
		}
	}
	
	private List<Button> convertButtonTypesIntoButtons(ToastButton[] buttonsToDisplay)
	{
		List<Button> convertedButtons = new ArrayList<Button>();
		for(ToastButton btnType : buttonsToDisplay)
		{
			if(btnType == ToastButton.CANCEL)
				convertedButtons.add(view.btn_cancel);
			if(btnType == ToastButton.OK)
				convertedButtons.add(view.btn_ok);
			if(btnType == ToastButton.RERTRY)
				convertedButtons.add(view.btn_retry);
		}
		return convertedButtons;
	}
	
	public Scene getScene()
	{
		return view.getScene();
	}
	
	public void show()
	{
		if(view != null)
			view.showToast();
	}
	
	public void setButtonHandlers(EventHandler<ActionEvent> eventHandler)
	{
		view.setButtonHandlers(eventHandler);
	}
	
	public Button getOKButton()
	{
		return view.btn_ok;
	}
	
	public Button getCancelButton()
	{
		return view.btn_cancel;
	}
	
	public Button getExitButton()
	{
		return view.btn_exit;
	}
	
	public Button getRetryButton()
	{
		return view.btn_retry;
	}
	
	public void hideToast()
	{
        //This shows the current window, show() could'nt be overridden
        view.hideToast();
	}
	
	public void showToastMessage(String title, String message, ToastButton[] buttons)
	{
		//Define the details of the toast message.
		loadToast(title, message, buttons);
		show();
	}
}
