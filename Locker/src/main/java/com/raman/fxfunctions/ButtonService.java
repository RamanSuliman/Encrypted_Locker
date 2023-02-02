package com.raman.fxfunctions;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class ButtonService 
{
	public static Button getButtonWithLabel(String labelText, String labelCss, String btnCss)
	{
		Button button = new Button();
		setButtonCSS(button, btnCss);
		
		Label label = new Label(labelText);
		//Define CSS class for this button.
		if(labelCss != null && !labelCss.isEmpty())
			label.getStyleClass().add(labelCss);
		if(btnCss != null && !btnCss.isEmpty())
			button.getStyleClass().add(btnCss);
		
		//Attach the label to the text.
		button.setGraphic(label);
		
		return button;
	}
	
	public static Button getButtonWithIcon(Image image, String iconCss, String btnCss, String optionalText)
	{
		Button button = new Button();
		setButtonCSS(button, btnCss);
		
		Label label = new Label((optionalText == null)? "":optionalText);
		try {
			if(image != null)
			{
				ImageView imageView = new ImageView(image);
				label.setGraphic(imageView);
				button.setGraphic(label);
			}else
				throw new IllegalAccessException();
		}catch (IllegalAccessException e) {
			System.err.println("Image argument can't be null!" + e.getMessage());
			e.printStackTrace();
		}
		return button;
	}
	
	/***
	 * Create an instance of button with icon image.
	 * 
	 * @param String iconCss is the CSS class name to edit the icon.
	 * @param String btnCss is the CSS class name to edit the button.
	 * @return Button
	 */
	public static Button getButtonWithIcon(String btnCss, int width, int height, EventHandler<ActionEvent> eventHandler)
	{
		Button button = getButton(width, height, null, btnCss, eventHandler);
		//Set the imageview as icon image for the button.
		ImageView imageView = new ImageView();
		
		//Assign the size of icon according to button size to fit in.
		imageView.setPickOnBounds(false);
		imageView.fitWidthProperty().bind(button.widthProperty());
		imageView.fitHeightProperty().bind(button.heightProperty());
		button.setPadding(new Insets(-1, -1, -1, -1));
		
		//Set the button icon.
		button.setGraphic(imageView);
		return button;
	}
	
	/***
	 * This method is used to create and prepare an instance of an image button.
	 * 
	 * @param width Takes an integer value to define the width property.
	 * @param height Takes an integer value to define the height property.
	 * @return ImageView Repressing image view to be used as a button.
	 */
	public static Button getButton(int width, int height, String text, String css, EventHandler<ActionEvent> eventHandler)
	{
		//If the text value is null then make the label empty.
		Button  button = new Button((text == null)? "":text);
		setButtonCSS(button, css);
		
		//Define button size.
		button.setPrefSize(width, height);
		//Add event handler
		if(eventHandler != null)
			button.setOnAction(eventHandler);
		
		return button;
	}
	
	public static void setButtonVisibilty(Node button, boolean isOn)
	{
		button.setVisible(isOn);
		button.setManaged(isOn);
	}
	
	private static void setButtonCSS(Button button, String css)
	{
		//Define CSS class for this button.
		if(css != null && !css.isEmpty())
			button.getStyleClass().add(css);
	}
}


/**

	public static Button getButtonWithIcon(String iconCss, String btnCss, int width, int height)
	{
		Button button = getButton(width, height, null, btnCss, null);
		//Set the imageview as icon image for the button.
		ImageView imageView = new ImageView();
		//Define CSS class for this imageview.
		try {
			if(iconCss != null && !iconCss.isEmpty())
			{
				imageView.getStyleClass().add(iconCss);
				button.setGraphic(imageView);
			}else
				throw new IllegalAccessException();
		}catch (IllegalAccessException e) {
			System.err.println("Css name must be defined otherwise you can't attach an icon!" + e.getMessage());
			e.printStackTrace();
		}
		return button;
	}



*/