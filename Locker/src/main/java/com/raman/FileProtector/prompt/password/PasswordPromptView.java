package com.raman.FileProtector.prompt.password;

import com.raman.fxfunctions.ButtonService;
import com.raman.fxfunctions.WindowService;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PasswordPromptView extends Stage
{
	protected PasswordField passwordField;
	private Label[] messages;
	private Label errorMessage;
	protected Button btn__password_close, btn_password_confirm;
	private EventHandler<ActionEvent> eventHandler;
	private ChangeListener<String> passwordHandler;
	private VBox root, messages_pane;
	private Scene scene;
	
	public PasswordPromptView(Stage ownerWidnow, EventHandler<ActionEvent> eventHandler, ChangeListener<String> passwordHandler) 
	{
		this.eventHandler = eventHandler;
		this.passwordHandler = passwordHandler;
		
		//Create the root pane
		root = new VBox();
		root.getStyleClass().add("rootPane_password");
		root.setAlignment(Pos.CENTER);
		root.setSpacing(4);
		//Setting minimum and maximum height for the root, works with the Region... define in the scene.
		root.setMinHeight(60);
		root.setMaxHeight(150);	
		
		//Create the Scene
		//Set maximum width of the scene and empty height with expandable size.
		scene = new Scene(root, 250, Region.USE_COMPUTED_SIZE);
		scene.getStylesheets().add("com.raman.gui/prompts.css");			
		scene.setFill(Color.TRANSPARENT);
	
		setAlwaysOnTop(true);
		initModality(Modality.APPLICATION_MODAL);
		initOwner(ownerWidnow);
		initStyle(StageStyle.TRANSPARENT);		
		WindowService.centreStage(ownerWidnow, this);
		
		setupView();
		
		setScene(scene);	
	}
	
	private void setupView()
	{	
		passwordField = new PasswordField();
		passwordField.setAlignment(Pos.CENTER);
		passwordField.getStyleClass().add("passwordField");
		passwordField.setPromptText("Enter your password");
		passwordField.textProperty().addListener(passwordHandler);
		
		
		
		messages_pane = new VBox();
		messages_pane.setStyle("-fx-padding: 0 0 0 50;");
		setErrorMessages(messages_pane);
		
		HBox button_container = new HBox();
		button_container.setSpacing(10);
		button_container.setAlignment(Pos.CENTER);
		
		btn__password_close = ButtonService.getButton(100, 25, "Close", "btn__password_close", eventHandler);
		btn_password_confirm = ButtonService.getButton(100, 25, "Confirm", "btn_password_confirm", eventHandler);
		btn_password_confirm.setDisable(true);
		
		button_container.getChildren().addAll(btn__password_close, btn_password_confirm);
		
		root.getChildren().addAll(passwordField, messages_pane, button_container);
	}
	
	protected void showToast()
	{
		//Reset view
		reset();
        //This shows the current window, show() could'nt be overridden
        showAndWait();
	}
	
	private void setErrorMessages(VBox container)
	{
		String[] rules = {
				"-Password must be at least 8 characters",
				"-Password minimum of 1 capital letter",
				"-Password must contain numbers",
				"-Password require 1 symbol"};
		messages = new Label[4];
		for(int i = 0; i < messages.length; i++)
		{
			//Load each message into a label object.
			Label txt_error_message = new Label(rules[i]);
			txt_error_message.getStyleClass().add("error_messages");
			container.getChildren().add(txt_error_message);
			//Add label into the list of messages.
			messages[i] = txt_error_message;
		}
	}
	
	public void updateColor(int index, String color) 
	{
		messages[index].setStyle("-fx-text-fill:" + color + ";");
	}
	
	protected void reset()
	{
		passwordField.setText("");
		btn_password_confirm.setDisable(true);
		for(Label lbl : messages)
			lbl.setStyle("-fx-text-fill:black;");
	}
}
