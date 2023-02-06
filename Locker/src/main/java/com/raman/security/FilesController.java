package com.raman.security;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FilesController 
{
	private ArrayList<File> files;
	private File lastDeletedFile;
	private Stage stage;
	private FileChooser fileChooser;
	
	public FilesController(Stage stage)
	{
		this.stage = stage;
		files = new ArrayList<File>();
		fileChooser = new FileChooser();
		//In case the 
		removeFile();
	}
	
	public void browseFiles()
	{
		fileChooser.setTitle("Fetch Desired Files & Folders");
		//Used to clear the default text in the file name field.
		fileChooser.setInitialFileName(null);
		//Used to clear all the predefined extension filters.
		fileChooser.getExtensionFilters().clear();
		//Used to allow any type of files.
		fileChooser.setSelectedExtensionFilter(null);
		List<File> selected = fileChooser.showOpenMultipleDialog(stage);
		if(selected != null)
			files = new ArrayList<File>(selected);
	}
	
	public void browseCipherFile()
	{
		fileChooser.setTitle("Fetch Desired Files & Folders");
		//Used to clear the default text in the file name field.
		fileChooser.setInitialFileName(null);
		//Used to clear all the predefined extension filters.
		fileChooser.getExtensionFilters().clear();
		//Only allow .encryption file to be selected.
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Encryption File", "*.encryption");
		fileChooser.getExtensionFilters().add(extFilter);
		List<File> selected = fileChooser.showOpenMultipleDialog(stage);
		if(selected != null)
			files = new ArrayList<File>(selected);
	}
	
	public ArrayList<File> getFiles()
	{
		return files;
	}
	
	public void removeFileAt(int index)
	{
		if(files.isEmpty() || index > files.size() - 1 || index < 0)
			return;
		//Assign last deleted file before removing it from the list.
		lastDeletedFile = files.get(index);
		System.out.println("File to be removed:" + lastDeletedFile.getName());
		files.remove(index);
	}
	
	public void removeAll()
	{
		if(!files.isEmpty())
			files.clear();
	}
	
	public void undo(int index)
	{
		if(index > files.size() - 1 || index < 0)
			return;
		//This will add the file at given position and shift the elements up without replacing.
		System.out.println("File:" + getLastDeletedFile().getName());
		files.add(index, getLastDeletedFile());
	}
	
	public File getLastDeletedFile()
	{
		return lastDeletedFile;
	}
	
	public boolean saveEncryptedFile()
	{
		//Setup file chooser
		saveOptionLoader("Save Encryption File 👨‍💻");
		//Get the cipher file to prepare for saving.
		File localFile = getEncryptedFile();
		if(localFile != null)
		{
			// Show save file dialog
			File saveFile = fileChooser.showSaveDialog(stage);
			if (saveFile != null) 
			{

				try {
					//Rename the local file name and path to the user file details.
					localFile.renameTo(saveFile);
					//Save the local raman.encryption file in the user defined path.
					FileOutputStream writer = new FileOutputStream(localFile);
				    //Remove the file from current folder.
				    removeFile();
				    writer.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			}else
				System.out.println("File not saved :(");
		}	
		return false;
	}
	
	public File openEncryptedFile()
	{
		//Setup file chooser
		saveOptionLoader("Load Encryption File 👨‍💻");
		File fileSelected;
		// re-show the save dialog until user has selected a valid file or choose to cancel.
		while(true)
		{
			//Check if user has selected the correct file format.
			fileSelected = fileChooser.showOpenDialog(stage);
			if(fileSelected == null)
				return null;
			System.out.println(fileSelected.getName());
			//This checks if file isn't the encryption one and is not null mean user has selected something.
		    if (fileSelected.getName().endsWith(".encrypted")) 
		    	return fileSelected;
		}
	}
	
	private void saveOptionLoader(String title)
	{
		fileChooser.setTitle(title);
		//Used to clear the default text in the file name field.
		fileChooser.setInitialFileName("Privacy");
		//Used to clear all the predefined extension filters.
		fileChooser.getExtensionFilters().clear();
		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Privacy...", "*.encrypted");
		fileChooser.getExtensionFilters().add(extFilter);
	}
	
	//Used get the path to save the decrypted files into.
	public String getSavingFolderPath()
	{
		DirectoryChooser directoryChooser = new DirectoryChooser();
		//This line can be used to define the initial start up folder.
		//directoryChooser.setInitialDirectory(new File("path"));
	    File selectedDirectory = directoryChooser.showDialog(stage);
	    if(selectedDirectory != null)
	    {
	    	System.out.println("Folder path is found " + selectedDirectory.getAbsolutePath());
			//Get the path of chosen directory
			return selectedDirectory.getAbsolutePath();
	    }
	    return "";
	}
	
	private File getEncryptedFile()
	{
		File file = new File(EncryptionController.encryptedFileName);
		try {
			if(file.exists())
				return file;
			throw new FileNotFoundException("Encrypted file not found!");
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	private void removeFile()
	{
		File file = new File(EncryptionController.encryptedFileName);
		if (file.delete())
		    System.out.println(file.getName() + " is deleted!");
		else
		    System.out.println("Delete operation is failed.");
	}
}
