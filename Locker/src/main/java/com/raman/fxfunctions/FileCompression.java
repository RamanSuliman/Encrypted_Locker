package com.raman.fxfunctions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileCompression 
{
	public static boolean compress(File file, String path)
	{
		System.out.println("File: " + file.getName() + " path: " + path);
		 // File to be compressed
		try {
			FileInputStream encryptedFile = new FileInputStream(file);
	        // Compressed file
	        FileOutputStream compressedFile = new FileOutputStream(path + ".zip");
	        // Compression method
	        ZipOutputStream writer = new ZipOutputStream(compressedFile);
	        // Compressing
	        ZipEntry ze = new ZipEntry(file.getName());
	        writer.putNextEntry(ze);
	        byte[] buffer = new byte[1024];
	        int len;
	        while ((len = encryptedFile.read(buffer)) > 0) {
	        	writer.write(buffer, 0, len);
	        }
	        writer.closeEntry();
	        // Closing streams
	        writer.close();
	        encryptedFile.close();
	        compressedFile.close();
	        return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public static void decompress()
	{
		try {
			FileInputStream fis = new FileInputStream("original.txt");
	        // Compressed file
	        FileOutputStream fos = new FileOutputStream("compressed.zip");
	        // Compression method
	        ZipOutputStream zos = new ZipOutputStream(fos);
	        // Compressing
	        ZipEntry ze = new ZipEntry("original.txt");
	        zos.putNextEntry(ze);
	        byte[] buffer = new byte[1024];
	        int len;
	        while ((len = fis.read(buffer)) > 0) {
	            zos.write(buffer, 0, len);
	        }
	        zos.closeEntry();
	        // Closing streams
	        zos.close();
	        fis.close();
	        fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
