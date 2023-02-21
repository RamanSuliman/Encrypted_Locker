package com.raman.fxfunctions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DataCompressor 
{
	public static byte[] compress_file(File file)
	{
		if(file == null)
			return null;
		
		try {
			FileInputStream reader = new FileInputStream(file);
			//Storing the output stream insead of writing it.
			ByteArrayOutputStream compressedFileData = new ByteArrayOutputStream();
			//
			GZIPOutputStream compressor = new GZIPOutputStream(compressedFileData);
			byte[] buffer = new byte[4096];
			int length;
			while((length = reader.read(buffer)) != -1)
				compressor.write(buffer, 0, length);
				
			reader.close();
			compressor.finish();
			compressor.close();
			return compressedFileData.toByteArray();
		} catch(SecurityException sec) {
			// TODO Auto-generated catch block
			sec.printStackTrace();
		}
		catch (IOException e) {
			System.out.println("Issue " + file.getName());
			e.printStackTrace();
		}
		return null;
	}

}
