package com.exocute.classdistributer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarInputStream;
import java.util.jar.JarEntry;

public class JarFileHandler {

	private static final String CLASS_EXTENSION = ".class";
	private static final String PIPELINE_EXTENSION = ".pln";
	
	/**
	 * Get as a list all of the classes that appear in a JarFile
	 * @param file - the jarFile to open
	 * @return
	 */
	public List <String> getClassNames(File file) {
		
			List<String> classNames = new ArrayList<String>(); 
			try {
				JarInputStream jis = new JarInputStream( new FileInputStream(file) );				
				JarEntry je;
				while ( (je = jis.getNextJarEntry()) != null) {
					String entryName = je.getName();
					if (entryName.endsWith(CLASS_EXTENSION))  {
						entryName = entryName.replace(CLASS_EXTENSION,"");
						entryName = entryName.replace('/', '.');
						classNames.add(entryName);
					}
					if (entryName.endsWith(PIPELINE_EXTENSION)) {
						classNames.add(entryName);
					}
					jis.closeEntry();
				}
				jis.close();
			} catch (IOException e) {
					e.printStackTrace();
			}
		return classNames;
		
	}
	
	
	/**
	 * Use a spot of  NIO to quickly load the jar from the given File into 
	 * a byte array.
	 * @param filePath
	 * @return the whole file as an array of bytes
	 */
	public byte [] getJarAsBytes(File file)  {
		FileChannel roChannel;
		byte [] jarAsBytes = null;
    
		try {
			roChannel = new RandomAccessFile(file, "r").getChannel();
			ByteBuffer roBuf = roChannel.map(FileChannel.MapMode.READ_ONLY, 0, (int)roChannel.size());
			roBuf.clear();
			jarAsBytes = new byte[roBuf.capacity()];
			roBuf.get(jarAsBytes, 0, jarAsBytes.length);
			roChannel.close();
			} 
		catch (Exception e) {
			e.printStackTrace();
			}
		return jarAsBytes;
		}
	
    
	
	
}
