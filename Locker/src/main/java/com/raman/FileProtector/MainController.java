package com.raman.FileProtector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.raman.FileProtector.prompt.initial.Prompt;
import com.raman.FileProtector.prompt.password.PasswordPromptController;
import com.raman.gui.toast.Toast;
import com.raman.gui.toast.Toast.ToastButton;
import com.raman.security.EncryptionController;
import com.raman.security.FilesController;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainController implements EventHandler<ActionEvent>
{
	private PasswordPromptController passwordPromptController;
	private int indexOfSelectedItem = -1;
	private int loadedNumberOfBytes = 0;
	private MainView view;
	private FilesController fileController;
	private EncryptionController encryptionController;
	private ArrayList<File> files;
	private File decryptedLoadedFile;
	private Toast toast;
	private String taskType;
	
	public MainController()
	{
		view = new MainView(this);
	}

	public void init(String taskType)
	{
		//Define a task type.
		this.taskType = taskType;
		//This method prepares the ListView on GUI to listen for select changes
		listSelectedEvent();
		//Initiate the file controller object.
		fileController = new FilesController((Stage) view.getScene().getWindow());
		//
		encryptionController = new EncryptionController(new ProgressBarActionHandler());
		//Setup password prompt
		passwordPromptController = new PasswordPromptController((Stage) view.getScene().getWindow(), new PasswordActionHandler());
		//Get an instance for the toast message.
		toast = Toast.getInstance();
		toast.setParentSatge((Stage)view.getScene().getWindow(), new ToastActionHandler());
	}
	
	public Scene getScene()
	{
		return view.getScene();
	}
	
	private Stage getStage()
	{
		return (Stage) view.getScene().getWindow();
	}
	
	public void isEncrption(boolean isEncryption)
	{
		view.isEncrption(isEncryption);
	}
	
	private void listSelectedEvent()
	{
		view.fileContainer.getSelectionModel().selectedIndexProperty()
		.addListener((observable, oldValue, newValue) -> 
		{
			if(indexOfSelectedItem == -1 || oldValue != newValue)
				if((int)newValue != -1)
					indexOfSelectedItem = (int) newValue;
		});
	}

	@Override
	public void handle(ActionEvent event) 
	{
		Object source = event.getSource();
		if(source instanceof Button)
			guibuttonEvent((Button) source);
		else
			System.out.println("Not button event been triggered");
		
	}	
	
	private void guibuttonEvent(Button button)
	{
		if(view.btn_attach == button)
		{
			if(attachEncryptedFile())
			{
				view.btn_attach.setDisable(true);
				view.btn_decrypt.setDisable(false);
			}
		}
		else if(view.btn_decrypt == button){			
			passwordPromptController.show();
		}else if(view.btn_encrypt == button){
			//If user has files loaded into the program.
			if(!view.isFilesContainerEmpty())
			{
				//Prompt the user to enter a password.
				passwordPromptController.show();
			}else
			{
				//Define the details of the toast message.
				toast.showToastMessage("Missing Files", "Make sure files are loaded first prior proceeding with encryption.",
						new ToastButton[]{ToastButton.OK});
			}
		}else if(view.btn_save_enc == button)
		{
			if(fileController.saveEncryptedFile())
				view.hideEncryptionSaveButton();

		}else if(view.btn_loadFiles == button)
		{
			fileController.browseFiles();
			addSelectedFilesToGUI();
		}else if(view.btn_removeAll == button)
		{
			view.fileContainer.getItems().clear();
			fileController.removeAll();
		}else if(view.btn_removeSelected == button){
			//If the ListView has an item selected then perform removal.
			if(!view.fileContainer.getSelectionModel().isEmpty())
			{
				//Remove selection after removing. 
				view.fileContainer.getSelectionModel().clearSelection();
				view.removeFileAt(indexOfSelectedItem);
				fileController.removeFileAt(indexOfSelectedItem);
			}
		}else if(view.btn_undo == button)
		{
			//If there is a file in the removed stack list.
			if(!fileController.isStackEmpty())
			{
				view.undo(fileController.getLastDeletedFile());
				fileController.undo();
				//To prevent removing another item again after undoing last action.
				indexOfSelectedItem = -1;
			}

		}else if(view.btn_minimise == button)
			((Stage) view.getScene().getWindow()).setIconified(true);
		else if(view.btn_close == button){
			Platform.exit();
	        System.exit(0);
		}else if(view.btn_help == button){
			//Determine which help message to show
			String taskType = view.getTaskType();
			new Prompt().showHelpWindow(getStage(), taskType);
		}
		else{
			//Define the details of the toast message.
			toast.showToastMessage("Unsupported Event", "Something unusual occured, refer back to the developer...",
					new ToastButton[]{ToastButton.OK});
			System.exit(0);
		}		
	}	
	
	private void addSelectedFilesToGUI()
	{
		List<File> files = fileController.getFiles();
		if(files != null && !files.isEmpty())
		{
			for(File file : files)
				view.addFileToContianer(file.getName());
		}
	}
	
	private void startEncryption()
	{
		files = fileController.getFiles();
		if(files != null && !files.isEmpty())
		{
			//Show the progress bar.
			view.showProgressBar();
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() 
				{
					//If encryption is done.
					if(encryptionController.encryptFiles(files))
						view.isEncryptionComplete(true);
					else
					{
						toast.getOKButton().setOnAction(e -> System.exit(0));
						toast.showToastMessage("Task Failed", "The encryption task faced an issue prior completion, please try again later.",
								new ToastButton[]{ToastButton.OK});
					}
				}
			});		
			thread.start();
		}		
	}
	
	private boolean attachEncryptedFile()
	{
		decryptedLoadedFile = fileController.openEncryptedFile();
		if(decryptedLoadedFile != null)
		{
			view.setLoadedFileText(decryptedLoadedFile.getName());
			return true;
		}
		return false;
	}

	private void startdDecrypting()
	{
		if(decryptedLoadedFile != null)
		{
			String path = fileController.getSavingFolderPath();
			if(!path.isEmpty())
			{
				//Show the progress bar.
				view.showProgressBar();
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() 
					{
						String message = "";
						String title = "";
						if(encryptionController.removeProtection(decryptedLoadedFile, path))
						{
							title = "Decryption Completed";
							message = "Privacy is now lifted and files can be found in the chosen folder.";
						}
						else
						{
							title = "Process Interrupted!";
							message = "An error occured during the decryption process, please try again.";
						}
						toast.getOKButton().setOnAction(e -> System.exit(0));
	                    toast.loadToast(title, message, new ToastButton[]{ToastButton.OK});
						Platform.runLater(new Runnable() {
							public void run() {
								toast.show(); 
							}
						});
					}
				});	
				thread.start();
				return;
			}
			else{
				toast.showToastMessage("Missing Folder!", "Seems like you have canceled the process.",
						new ToastButton[]{ToastButton.OK});
				return;
			}
		}
	}
	
	// ############################# Password Handler #############################
	class PasswordActionHandler implements EventHandler<ActionEvent>
	{
		private void passwordEventHanderDecryption(Button button)
		{
			if(button == passwordPromptController.getCancelButton())
			{
				passwordPromptController.close();
			}
			else if(button == passwordPromptController.getConfirmButton())
			{
				System.out.println(PasswordPromptController.password);
				if(!encryptionController.isPasswordCorrect(decryptedLoadedFile))
				{
					String message = "";
					String title = "";
					ToastButton buttonType;
					if(!encryptionController.hasUserReachedMaxNumberOfPasswordAttempts())
					{
						title = "Password Incorrect";
						message = "An invalid credtional is given, please try again and remember " + 
                                encryptionController.getNumberOfPasswordAttempts()+ " attempts remained."; 
						buttonType = ToastButton.RERTRY;
					}else{
						title = "Unreachable Solution!";
						message = "Password attempst ended, this session will be ended after pressing OK.";
						buttonType = ToastButton.OK;
						toast.getOKButton().setOnAction(e -> System.exit(0));
					}
					//Remove given password value
					PasswordPromptController.password = "";
					//Clean up the password GUI.
					passwordPromptController.cleanUp();
					//Define the details of the toast message.
                    toast.showToastMessage(title, message, new ToastButton[]{buttonType}); 
				}
				else
				{
					if(!PasswordPromptController.password.isEmpty())
					{
						view.decryptionEnded();
						startdDecrypting();
					}
				}
			}
			else
				toast.showToastMessage("Unknown Input", "Not defined action has been triggered, notify the developer.",
						new ToastButton[]{ToastButton.CANCEL});
		}

		private void passwordEventHanderEncryption(Button button)
		{
			if(button == passwordPromptController.getCancelButton()){
				passwordPromptController.close();
			}
			else if(button == passwordPromptController.getConfirmButton()){
				if(!PasswordPromptController.password.isEmpty())
				{
					passwordPromptController.close();
					//Hide the body panel to hide controllers and prevent user adding or removing loaded files.
					view.hideEncryptionBody();
					//Swap the buttons by hiding the "Encrypt" and showing "Save" button.
					view.encryptionEnded();
					//Begin decrypting the encrypted file.
					startEncryption();
				}
			}else
				toast.showToastMessage("Unknown Input", "Not defined action has been triggered, notify the developer.",
						new ToastButton[]{ToastButton.CANCEL});
		}
		
		@Override
		public void handle(ActionEvent event) 
		{
			Object source = event.getSource();
			if(source instanceof Button)
			{
				if(taskType.equalsIgnoreCase("Encrypt"))
					passwordEventHanderEncryption((Button)source);
				else
					passwordEventHanderDecryption((Button)source);
			}else
				toast.showToastMessage("Unknown Input", "Not defined action has been triggered, notify the developer.",
						new ToastButton[]{ToastButton.CANCEL});			
		}			
	}
	
	// ############################# Progressbar Handler #############################
	class ProgressBarActionHandler implements ProgressMeasure
	{
		@Override
		public void onProgressUpdate(int progress, String type) 
		{
			if(type.equalsIgnoreCase("encrypt"))
			{
				System.out.println("Encryption Progress: " + progress / (double)files.size());
				view.getProgressBar().setProgress(progress / (double)files.size());
			}
			else
			{
				//Add the number of bytes into the counter.
				loadedNumberOfBytes += progress;
				System.out.println("Decryption Progress: " + progress + " ---> " + loadedNumberOfBytes / (double)decryptedLoadedFile.length());
				view.getProgressBar().setProgress(loadedNumberOfBytes / (double)decryptedLoadedFile.length());
			}		
		}

		@Override
		public void onComplete(int type) 
		{
			if(type == 0)
				view.decryptionEnded();
			else
				view.encryptionEnded();
		}
	}
	// ############################# Toast Handler #############################
	class ToastActionHandler implements EventHandler<ActionEvent>
	{
		@Override
		public void handle(ActionEvent event) 
		{
			Object source = event.getSource();
			if(source instanceof Button)
				implementToastButtons((Button) source);
			else
				toast.showToastMessage("Unknown Input", "Not defined action has been triggered, notify the developer.",
						new ToastButton[]{ToastButton.CANCEL});	
		}
		
		private void implementToastButtons(Button button)
		{
			if(toast.getExitButton() == button){
				toast.hideToast();
			}
			else if(toast.getCancelButton() == button || toast.getRetryButton() == button || toast.getOKButton() == button ){			
				toast.hideToast();
			}	
		}
	}
}
