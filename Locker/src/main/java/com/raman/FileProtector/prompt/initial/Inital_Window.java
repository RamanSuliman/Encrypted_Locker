package com.raman.FileProtector.prompt.initial;

import com.raman.fxfunctions.ButtonService;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class Inital_Window
{
	private Label txt_welcome;
	private VBox root;
	protected Button btn_encrypt, btn_decrypt;
	private EventHandler<ActionEvent> eventHandler;
	private Scene scene;
	
	public Inital_Window(EventHandler<ActionEvent> eventHandler) 
	{
		this.eventHandler = eventHandler;
		
		//Create the root pane
		root = new VBox();
		root.getStyleClass().add("initial_window_rootPane");
		root.setSpacing(5);
		root.setAlignment(Pos.CENTER);
		//Setting minimum and maximum height for the root, works with the Region... define in the scene.
		root.setMinHeight(200);
		root.setMaxHeight(400);	

		addChildren();
		
		//Create the Scene
		//Set maximum width of the scene and empty height with expandable size.
		scene = new Scene(root, 300, Region.USE_COMPUTED_SIZE);
		scene.getStylesheets().add("com.raman.gui/prompts.css");			
		scene.setFill(Color.TRANSPARENT);
	}
	
	private void addChildren()
	{
		//##### Head ######
		StackPane logo_container_pane = new StackPane();
		logo_container_pane.getStyleClass().add("logo_conatiner");
		
		ImageView logo = new ImageView();
		logo.getStyleClass().add("logo");
		//Set width and height, if not define using setPreserveRatio() display image in original size.
		logo.setFitHeight(150);
		logo.setFitWidth(150);
		logo.setPreserveRatio(true);
		logo_container_pane.getChildren().add(logo);
		
		//##### Body ######
		VBox body = new VBox();
		body.setAlignment(Pos.CENTER);
		body.setSpacing(6);
		body.getStyleClass().add("body_conatiner");
		
		txt_welcome = new Label("Choose a Task");
		txt_welcome.getStyleClass().add("txt_welcome");
		//Position the text in the middle.
		txt_welcome.setAlignment(Pos.CENTER);
		
		HBox button_container = new HBox();
		button_container.setSpacing(4);
		button_container.setAlignment(Pos.CENTER);
		btn_encrypt = ButtonService.getButton(0, 40, "Encrypt", "btn_encrypt", eventHandler);
		btn_decrypt = ButtonService.getButton(0, 40, "Decrypt", "btn_decrypt", eventHandler);
		//Filling width
		btn_encrypt.prefWidthProperty().bind(button_container.widthProperty().divide(2));
		btn_decrypt.prefWidthProperty().bind(button_container.widthProperty().divide(2));
		button_container.getChildren().addAll(btn_encrypt, btn_decrypt);
		
		body.getChildren().addAll(txt_welcome, button_container);
		
		root.getChildren().addAll(logo_container_pane, body);		
	}
	
	protected Scene getScene()
	{
		return scene;
	}
}
