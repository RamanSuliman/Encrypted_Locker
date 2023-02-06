package com.raman.main;

import com.raman.FileProtector.prompt.initial.Prompt;
import com.raman.fxfunctions.WindowService;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Application_Entry extends Application 
{
	private Stage stage;
	
	private static Application_Entry instance;

	public static Application_Entry getInstance() 
	{
	    return instance;
	}
	
	@Override
	public void start(Stage primaryStage) 
	{
		instance = this;
		stage = primaryStage;
		try 
		{					
			AccessController access = new AccessController(primaryStage);
			
			//Set up Stage
			primaryStage.setScene(access.getScene());
			primaryStage.setResizable(false);
			primaryStage.setAlwaysOnTop(true);
			primaryStage.getIcons().add(new Image("com.raman.gui/icons/logo.png"));
	        primaryStage.initStyle(StageStyle.TRANSPARENT);
	        primaryStage.show();
	        
		/*	
			Prompt prompt = new Prompt();
			
			//Set up Stage
			primaryStage.setScene(prompt.getInitialWindow());
			primaryStage.setResizable(false);
			primaryStage.setAlwaysOnTop(true);
			primaryStage.getIcons().add(new Image("com.raman.gui/icons/logo.png"));
	        primaryStage.initStyle(StageStyle.TRANSPARENT);
	        primaryStage.show();
	     
	     */          
	               
	        /******  To centre screen must be called after .show() ******/
	        WindowService.centreWindow(primaryStage);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}	
	
	public static void main(String[] args) 
	{
		launch(args);
	}
		
	public void initaiteApplication()
	{
		Prompt prompt = new Prompt();		
		//Set up Stage
		stage.setScene(prompt.getInitialWindow());
	}
}
