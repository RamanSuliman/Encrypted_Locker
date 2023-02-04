package com.raman.fxfunctions;

import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class WindowService 
{
	private static double xOffset = 0;
	private static double yOffset = 0;
	// Get the screen size
	private static Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
	/***
	 * Centre the program in the middle of the screen (MUST BE CALLED AFTER) .show() method.
	 */
	public static void centreWindow(Stage primaryStage)
	{
        // Center the stage horizontally
        primaryStage.setX((screenBounds.getWidth() - primaryStage.getWidth()) / 2);

        // Center the stage vertically
        primaryStage.setY((screenBounds.getHeight() - primaryStage.getHeight()) / 2);
	}
	
	/***
	 * Centring the toast stage in the middle of the screen.
	 */
	public static void centreStage(Stage parent, Stage child)
	{
		// Calculate the center position of the parent Stage
		double parentX = (Double.isNaN(parent.getX())? 0 : parent.getX());
		double parentY = (Double.isNaN(parent.getY())? 0 : parent.getY());
		//If parent stage width and height are 0 then use the screen mreasurments to center the child.
		double parentWidth = (Double.isNaN(parent.getWidth())? screenBounds.getWidth() : parent.getWidth());
		double parentHeight = (Double.isNaN(parent.getHeight())? screenBounds.getHeight() : parent.getHeight());
		
        double centerXPosition = parentX + parentWidth / 2d;
        double centerYPosition = parentY + parentHeight / 2d;

        // Relocate the pop-up Stage
        child.setOnShown(ev -> {
        	child.setX(centerXPosition - child.getWidth()/2d);
        	child.setY(centerYPosition - child.getHeight()/2d);
        });
	}
	
	/**
     * The stage is set to undecorated style, therefore this method
     * allow user to drag the window if wanted using mouse click-drag.
     */
	public static void dragableWindow(Stage stage, Node node)
	{
		node.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
		node.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	stage.setX(event.getScreenX() - xOffset);
            	stage.setY(event.getScreenY() - yOffset);
            }
        });
	}
}
