package com.raman.security;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.raman.FileProtector.ProgressMeasure;
import com.raman.FileProtector.prompt.password.PasswordPromptController;

import encryption.symmetric.PasswordEncryption;


public class EncryptionController  
{
	public final static String encryptedFileName = "raman.encrypted";
	private int passwordAttempts = 5;
	private ProgressMeasure callback;
	
	public EncryptionController(ProgressMeasure callback)
	{
		this.callback = callback;
	}
	
	public boolean encryptFiles(ArrayList<File> files)
	{
		int numberOfFilesEncrypted = 0;
		FileInputStream inputStream = null;
		byte[] inputBytes;
		Map<String, byte[]> cipherFiles = new HashMap<>();
		// Create a task to simulate file decryption
		for(File file : files)
		{
			try {
				inputStream = new FileInputStream(file);
				//Declare an array of the file size.
				inputBytes = new byte[(int) file.length()];
				//Load file bytes into the array.
			    inputStream.read(inputBytes);
			    //Fed the bytes of cipher and file name and  into the map.
			    byte[] decrypted = feedBytesToEncryption(inputBytes);
			    //Close the file.
			    inputStream.close();
			    //If decrypted is null then user password is wrong.
			    if(decrypted == null)
			    	return false;
			    cipherFiles.put(file.getName(), decrypted);
			    callback.onProgressUpdate(++numberOfFilesEncrypted, "encrypt");
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} 
		
		System.out.println(cipherFiles.size());
		
		try {
			FileOutputStream sourceStream = new FileOutputStream(encryptedFileName);
			DataOutputStream writer = new DataOutputStream(sourceStream);
			// write the file name and the ciphertext to the single output file
			// Iterating HashMap through for loop
			for(Map.Entry<String, byte[]> ciphers: cipherFiles.entrySet())
			{
				//Write file name
				writer.writeUTF(ciphers.getKey());
				//Write file size
				writer.writeInt(ciphers.getValue().length);
				//Write cipher bytes.
				writer.write(ciphers.getValue());
			}
			writer.close();
			sourceStream.close();
		}catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private byte[] feedBytesToEncryption(byte[] bytes)
	{
		return PasswordEncryption.encryptFile(bytes, PasswordPromptController.password);
	}
	
	public boolean isPasswordCorrect(File file)
	{
		try{
		    // the ciphertext file
			FileInputStream sourceStream = new FileInputStream(file);
			DataInputStream reader = new DataInputStream(sourceStream);
		    while (reader.available() > 1) 
		    {
		        // read the file name
		        String fileName = reader.readUTF();
		        if (fileName == null) {
		            break;
		        }

		        // read the ciphertext size
		        int ciphertextSize = reader.readInt();

		        // read the ciphertext
		        byte[] inputBytes = new byte[ciphertextSize];
		        reader.read(inputBytes);

		        //decrypt the file
				byte[] outputBytes = PasswordEncryption.decryptFile(inputBytes, PasswordPromptController.password);
				//Null means password is wrong
				if(outputBytes == null)
					return false;;
		    }
		    reader.close();
		    sourceStream.close();
		}catch(Exception e){
		    e.printStackTrace();
		    return false;
		}
		return true;
	}
	
	public boolean removeProtection(File file, String folderSavePath)
	{
		try{
		    // the ciphertext file
			FileInputStream sourceStream = new FileInputStream(file);
			DataInputStream reader = new DataInputStream(sourceStream);
		    while (reader.available() > 1) 
		    {
		        // read the file name
		        String fileName = reader.readUTF();
		        if (fileName == null) {
		            break;
		        }

		        // read the ciphertext size
		        int ciphertextSize = reader.readInt();

		        // read the ciphertext
		        byte[] inputBytes = new byte[ciphertextSize];
		        reader.read(inputBytes);

		        //decrypt the file
				byte[] outputBytes = PasswordEncryption.decryptFile(inputBytes, PasswordPromptController.password);
				//Null means password is wrong
				if(outputBytes == null)
					return false;
		        //write the decrypted bytes to the file
		        FileOutputStream outputStream = new FileOutputStream(folderSavePath + "\\" + fileName);
		        outputStream.write(outputBytes);
		        outputStream.close();
		        callback.onProgressUpdate(outputBytes.length, "decrypt");        
		    }
		    reader.close();
		    sourceStream.close();
		}catch(Exception e){
		    e.printStackTrace();
		    return false;
		}
		return true;
	}
	
	public boolean hasUserReachedMaxNumberOfPasswordAttempts()
	{
		passwordAttempts--;
		if(passwordAttempts == 0)
			return true;
		return false;
	}
	
	public int getNumberOfPasswordAttempts()
	{
		return passwordAttempts;
	}
}
