package com.raman.FileProtector.prompt.initial;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import com.raman.fxfunctions.ButtonService;
import com.raman.fxfunctions.WindowService;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HelpMessageView extends Stage
{
	private final String encryptionMessage = "com.raman.gui/icons/encrypt.png";
	private final String decryptionMessage = "com.raman.gui/icons/decrypt.png";
	private VBox root;
	private ImageView imageview;
	protected Button btn_close;
	
	public HelpMessageView(Stage ownerWidnow, EventHandler<ActionEvent> eventHandler) 
	{
		//Create the root pane
		root = new VBox();
		root.getStyleClass().add("root_help_message");
		root.setAlignment(Pos.CENTER);
		root.setSpacing(6);
		//Setting minimum and maximum height for the root, works with the Region... define in the scene.
		root.setMinHeight(300);
		root.setMaxHeight(500);	
		
		//Create the Scene
		//Set maximum width of the scene and empty height with expandable size.
		Scene scene = new Scene(root, 500, Region.USE_COMPUTED_SIZE);
		scene.getStylesheets().add("com.raman.gui/prompts.css");			
		scene.setFill(Color.TRANSPARENT);
	
		setAlwaysOnTop(true);
		initModality(Modality.APPLICATION_MODAL);
		initOwner(ownerWidnow);
		initStyle(StageStyle.TRANSPARENT);		
		WindowService.centreStage(ownerWidnow, this);
		
		setViewNodes(eventHandler);
		
		setScene(scene);	
	}
	
	private void setViewNodes(EventHandler<ActionEvent> eventHandler)
	{
		/*############### Contact Information ###############*/
		VBox contactInfo = new VBox();
		contactInfo.setAlignment(Pos.CENTER);
		contactInfo.setSpacing(2);
		
		Label txt_title = new Label("Contact Developer");
		//Set CSS class name.
		txt_title.getStyleClass().add("txt_contact_developer");
		//Position the text in the middle.
		txt_title.setAlignment(Pos.CENTER);
		
		Label emailAddress = new Label("raman.suliman@outlook.com");
		//Set CSS class name.
		emailAddress.getStyleClass().add("txt_email");
		//Position the text in the middle.
		emailAddress.setAlignment(Pos.CENTER);
		//Add the email to clipboard on click
		emailAddress.setOnMouseClicked(event -> {
			//Open the default email application and pass the clicked email to it.
			if (Desktop.isDesktopSupported()) {
		        try {
		        	 String email = emailAddress.getText();
		             String subject = "(Privacy Locker) Enquiry";
		             String body = "Hey Raman,\n\nI have a concern about the application.\n\n[Your message]\n\nBest Wishes,\nSoftware Admirer.";
		             Desktop.getDesktop().mail(new URI("mailto:" + email + "?subject=" + 
		                                               URLEncoder.encode(subject, "UTF-8") + "&body=" + 
		                                               URLEncoder.encode(body, "UTF-8")));
		        } catch (URISyntaxException | IOException ex) {
		            ex.printStackTrace();
		        }
		    }
			//Add the email into the clipboard.
		    final Clipboard clipboard = Clipboard.getSystemClipboard();
		    final ClipboardContent content = new ClipboardContent();
		    content.putString(emailAddress.getText());
		    clipboard.setContent(content);
		});
		
		contactInfo.getChildren().addAll(txt_title, emailAddress);
		
		/*############### Info Image ###############*/
		imageview = new ImageView();
		imageview.getStyleClass().add("help_image");
		//Set width and height, if not define using setPreserveRatio() display image in original size.
		imageview.setFitHeight(480);
		imageview.setFitWidth(500);
		imageview.setPreserveRatio(true);
		
		/*############### Close Button ###############*/
		btn_close = ButtonService.getButton(100, 25, "Close", "btn_help_close", eventHandler);
		
		root.getChildren().addAll(contactInfo, imageview, btn_close);
	}
	
	protected void setHelpImage(String imageType)
	{
		String imageName = (imageType.equalsIgnoreCase("Encrypt"))? encryptionMessage : decryptionMessage;
		imageview.setImage(new Image(imageName));
	}
}
