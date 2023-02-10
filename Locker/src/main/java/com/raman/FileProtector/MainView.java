package com.raman.FileProtector;

import java.io.File;
import com.raman.fxfunctions.ButtonService;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MainView
{
	private Scene scene;
	private BorderPane root;
	private Label txt_title, txt_file_loaded;
	private ImageView icon;
	private EventHandler<ActionEvent> eventHandler;
	private VBox box_encrypt, box_decypt, panel_footer;
	private HBox box_decrypt_panel;
	private ProgressBar progressBar;
	protected Button btn_minimise, btn_close, btn_encrypt, btn_decrypt, btn_save_enc;
	protected Button btn_removeAll, btn_removeSelected, btn_loadFiles, btn_undo, btn_attach;
	protected ListView<Label> fileContainer;
	
	public MainView(EventHandler<ActionEvent> eventHandler)
	{
		this.eventHandler = eventHandler;
		
		//Create the root pane
		root = new BorderPane();
		root.setTop(panel_header());
		root.setCenter(panel_body());
		root.setBottom(panel_footer());
		root.getStyleClass().add("rootPane");
		//Setting minimum and maximum height for the root, works with the Region... define in the scene.
		root.setMinHeight(75);
		root.setMaxHeight(300);	
		
		scene = new Scene(root, 400, Region.USE_COMPUTED_SIZE);
		scene.getStylesheets().add("com.raman.gui/main.css");
		scene.setFill(Color.TRANSPARENT);
	}
	
	private Node panel_header() 
	{
		BorderPane panel_header = new BorderPane();
		panel_header.getStyleClass().add("panel_header");
		
		/*############### Application Controllers Container ###############*/
		HBox container = new HBox();
		container.setSpacing(5);
		panel_header.setRight(container);
		
		/*############### Application Close Button ###############*/
		btn_close = ButtonService.getButtonWithIcon("btn_close", 25, 25, eventHandler);
		
		/*############### Application Minimise Button ###############*/
		btn_minimise = ButtonService.getButtonWithIcon("btn_minimise", 25, 25, eventHandler);
		
		container.getChildren().addAll(btn_minimise, btn_close);
		
		/*############### Application Title ###############*/
		txt_title = new Label("Privacy Locker");
		//Set CSS class name.
		txt_title.getStyleClass().add("txt_title");
		//Make the text to be wrapped meaning extra text is pushed into new line.
		txt_title.setWrapText(true);
		//Set max width for the text.
		txt_title.setMaxWidth(180);
		//Position the text in the middle.
		txt_title.setAlignment(Pos.CENTER);
		panel_header.setCenter(txt_title);
	
		/*############### Default Message Type Icon ###############*/
		icon = new ImageView();
		icon.getStyleClass().add("icon_application");
		//Set width and height, if not define using setPreserveRatio() display image in original size.
		icon.setFitHeight(35);
		icon.setFitWidth(35);
		icon.setPreserveRatio(true);
		//Centre the icon vertically.
		BorderPane.setAlignment(icon, Pos.CENTER);
		panel_header.setLeft(icon);
		
		return panel_header;
	}

	private Node panel_body() 
	{
		HBox panel_body= new HBox();
		panel_body.getStyleClass().add("panel_body");
		
				/*############### Left Content Editing Tools ###############*/
		VBox left_container = new VBox();
		left_container.setAlignment(Pos.CENTER);
		left_container.setSpacing(4);
		left_container.setPadding(new Insets(0,0,0,10));
		left_container.getStyleClass().add("left_container");
		
		// Remove All Button
		btn_removeAll = ButtonService.getButtonWithIcon("btn_removeAll", 30, 30, eventHandler);
		// Remove Selected File Button
		btn_removeSelected = ButtonService.getButtonWithIcon("btn_removeSelected", 30, 30, eventHandler);
		left_container.getChildren().addAll(btn_removeAll, btn_removeSelected);
		
				/*############### File Container ###############*/
		fileContainer= new ListView<Label>();		
		fileContainer.setPrefSize(300, 100);
		fileContainer.getStyleClass().add("fileContainer");
		
		ScrollPane scrollPane = new ScrollPane();
		panel_body.getStyleClass().add("scrollPane");
		scrollPane.setContent(fileContainer);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		//Make the bar invisible
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		HBox.setMargin(scrollPane, new Insets(0,10,0,10));
			
				/*############### Left Content Editing Tools ###############*/
		VBox right_container = new VBox();
		right_container.setAlignment(Pos.CENTER);
		right_container.setSpacing(4);
		right_container.setPadding(new Insets(0,10,0,0));
		right_container.getStyleClass().add("right_container");
		
		// Remove All Button
		btn_loadFiles = ButtonService.getButtonWithIcon("btn_loadFiles", 30, 30, eventHandler);
		// Remove Selected File Button
		btn_undo = ButtonService.getButtonWithIcon("btn_undo", 30, 30, eventHandler);
		right_container.getChildren().addAll(btn_loadFiles, btn_undo);
				
		panel_body.getChildren().addAll(left_container, scrollPane, right_container);
		
		return panel_body;
	}
	
	/***
	 * This method sets the footer section of the main window GUI.
	 * @return Node Repressing layout of type HBox.
	 */
	private Node panel_footer() 
	{
		panel_footer = new VBox();
		panel_footer.getStyleClass().add("panel_footer");
		panel_footer.setSpacing(5);
		panel_footer.setAlignment(Pos.CENTER);
		
		//Prepare the progress bar
		progressBar = new ProgressBar();
		progressBar.getStyleClass().add("progressBar");
		//Filling width
		progressBar.prefWidthProperty().bind(panel_footer.widthProperty());
		//VBox.setMargin(progressBar, new Insets(0, 2, 0, 2));
		//Make the bar invisible on creation.
		ButtonService.setButtonVisibilty(progressBar, false);
		
		panel_footer.getChildren().add(progressBar);
		
		return panel_footer;
	}

	private VBox setEncryptBox()
	{
		box_encrypt = new VBox();
		box_encrypt.setAlignment(Pos.CENTER);
		box_encrypt.getStyleClass().add("box_encrypt");

		btn_encrypt = ButtonService.getButton(80, 20, "Encrypt", "btn_encrypt", eventHandler);

		btn_save_enc = ButtonService.getButton(80, 20, "Save", "btn_save_enc", eventHandler);
		isEncryptionComplete(false);
		
		box_encrypt.getChildren().addAll(btn_encrypt, btn_save_enc);
		
		return box_encrypt;
	}
	
	private VBox setDecryptBox()
	{
		box_decypt = new VBox();
		box_decypt.getStyleClass().add("box_decypt");
		box_decypt.setSpacing(4);
		box_decypt.setAlignment(Pos.CENTER);
		
		//Prepare the panel container that will hold attach and "decrypt" buttons.
		box_decrypt_panel = new HBox();
		box_decrypt_panel.setSpacing(10);
		box_decrypt_panel.getStyleClass().add("box_decrypt_panel");
		box_decrypt_panel.setAlignment(Pos.CENTER);
		
		//Preparing buttons
		btn_attach = ButtonService.getButton(80, 20, "Attach", "btn_attach", eventHandler);
		btn_decrypt = ButtonService.getButton(80, 20, "Decrypt", "btn_decrypt", eventHandler);
		btn_decrypt.setDisable(true);
		
		//Add buttons to the horizontal panel box
		box_decrypt_panel.getChildren().addAll(btn_attach, btn_decrypt);
		
		txt_file_loaded = new Label("No cipher is loaded yet...");
		txt_file_loaded.getStyleClass().add("txt_file_loaded");
		
		box_decypt.getChildren().addAll(txt_file_loaded, box_decrypt_panel);
		
		return box_decypt;
	}
	
	protected void isEncryptionComplete(boolean isDone)
	{
		//Turns off or on the save button of the encryption task.
		ButtonService.setButtonVisibilty(btn_save_enc, isDone);
		//Turns off or on the encrypt button of the encryption task.
		ButtonService.setButtonVisibilty(btn_encrypt, !isDone);
	}
	
	/**
	 * Sets the mode of encryption or decryption, this method would
	 * hide one section and make other visible according to the boolean value.
	 * @param isEncryption
	 */
	protected void isEncrption(boolean isEncryption)
	{
		if(isEncryption)
		{
			panel_footer.getChildren().add(setEncryptBox());
			ButtonService.setButtonVisibilty(box_encrypt, isEncryption);
		}
		else
		{
			panel_footer.getChildren().add(setDecryptBox());
			ButtonService.setButtonVisibilty(box_decypt, !isEncryption);
			//In decryption mode there is no need for the body controllers.
			root.getChildren().remove(1);
		}		
	}
	
	protected void addFileToContianer(String filename)
	{
		Label label = new Label(filename);
		fileContainer.getItems().add(label);
	}
	
	protected Scene getScene()
	{
		return scene;
	}
	
	protected void removeFileAt(int index)
	{
		if(fileContainer.getItems().isEmpty() || index > fileContainer.getItems().size() - 1 || index < 0)
			return;
		fileContainer.getItems().remove(index);
	}
	
	protected void undo(int index, File file)
	{
		if(file == null || index > fileContainer.getItems().size() - 1 || index < 0)
			return;
		//This will add the file at given position and shift the elements up without replacing.
		System.out.println("File list name:" + file.getName());
		fileContainer.getItems().add(index, new Label(file.getName()));
	}
	
	protected void setLoadedFileText(String name)
	{
		txt_file_loaded.setText(name);;
	}
	
	protected ProgressBar getProgressBar()
	{
		return progressBar;
	}
	
	protected void showProgressBar()
	{
		//Set the progress bar to initial state.
		progressBar.setProgress(0.0);
		ButtonService.setButtonVisibilty(progressBar, true);
	}
	
	protected void decryptionEnded()
	{
		btn_attach.setDisable(true);
		btn_decrypt.setDisable(true);
	}
	
	protected void encryptionEnded()
	{
		btn_save_enc.setDisable(false);
		btn_encrypt.setDisable(true);
	}
	
	protected boolean isFilesContainerEmpty()
	{
		return fileContainer.getItems().isEmpty();
	}	
	
	protected void hideEncryptionSaveButton()
	{
		ButtonService.setButtonVisibilty(btn_save_enc, false);
	}
	
	protected void hideEncryptionBody()
	{
		Node bodyPanel = root.getChildren().get(1);
		ButtonService.setButtonVisibilty(bodyPanel, false);
	}
}
