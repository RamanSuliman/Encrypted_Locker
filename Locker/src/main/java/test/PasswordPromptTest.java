package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit5.ApplicationTest;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.*;
import com.raman.FileProtector.prompt.initial.Prompt;
import com.raman.FileProtector.prompt.password.PasswordPromptController;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.stage.Stage;

public class PasswordPromptTest extends ApplicationTest implements EventHandler<ActionEvent>
{
	private PasswordPromptController controller;
	private Stage stage;
	@Override 
	public void start(Stage stage)
	{
		this.stage = stage;
	}
	
	//To run/initiate objects before executing text methods.
	@Before
	public void init()
	{
		controller = new PasswordPromptController(stage, this);
	}
	
	@Test
	private void checkNodeIdExistAndValid(String nodeName)
	{
		// Get a reference to the button by its ID
		Node node = lookup(nodeName).query();
		// Check that the button exists
		verifyThat(node, isNotNull());
	}
	
	@Test
	public void testLoginSuccess()
	{
		
	}

	
	@Override
	public void handle(ActionEvent buttons) 
	{
		// TODO Auto-generated method stub
		
	}
}

/*
	WaitForAsyncUtils.waitForFxEvents() is a utility method provided by TestFX, a library for testing JavaFX applications.
	 This method waits for all currently scheduled JavaFX events to be processed before returning. It can be used to ensure 
	 that the JavaFX thread has finished processing events and has returned to an idle state before executing further test steps.
 */
