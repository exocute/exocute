package com.exocute.classdistributer;

import java.io.File;
import java.io.FilenameFilter;

public class ClassDistributer {

	
	/**
	 * Start the ClassDistributer with the directory given as a parameter.
	 * @param args - the path the exocute jars directory
	 * @throws InterruptedException 
	 */
	public static void main(String [] args) throws InterruptedException {
		
		// look to the jar directory to cache all of the jars
		final String directoryName = args[0];
		File directory = new File(directoryName);
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException(directoryName + " is not valid directory");
		}
		FilenameFilter filter = (FilenameFilter) new JarFileFilter();
		JarUpdater writer;
		if (args.length>1) {
			 writer = new JarSpaceUpdater(args[1]);
		} else {
			 writer = new JarSpaceUpdater(null);
		}
		
		while ( !Thread.interrupted() ) {
			writer.update(directoryName, directory.list(filter));
			Thread.sleep(100);
		}
	}
	
	
}
