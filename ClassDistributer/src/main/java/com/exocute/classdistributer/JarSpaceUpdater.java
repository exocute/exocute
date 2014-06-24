package com.exocute.classdistributer;


import java.io.File;
import java.util.List;

import com.zink.fly.Fly;
import com.zink.fly.kit.FlyFactory;
import com.zink.fly.kit.FlyFinder;


public class JarSpaceUpdater extends JarUpdater {
  
	private static final String TAG = "JarSpace";
	private static final long ENTRY_LEASE = 365 * 24 * 60 * 60 * 1000;

	private Fly space;
	private JarFileHandler fileHandler = new JarFileHandler();
	
	public JarSpaceUpdater(String flyHost) {
		if (flyHost==null) {
			FlyFinder finder = new FlyFinder();
			space = finder.find(new String[] { TAG });
		} else {
			space = FlyFactory.makeFly(flyHost);
		}
		if (space == null) {
			throw new RuntimeException("Cant find space with [" + TAG + "]");
		}
	}
	
	public void update(String directory, String jarFile) {
		
		File file = new File(directory, jarFile);
		updateJarEntry(file);
		updateClassEntries(file);
	}
	
	private void updateJarEntry(File jarFile) {
		FlyJarEntry je = new FlyJarEntry();
		je.fileName = jarFile.getName();
		System.out.println("Updating Jar Entry " + je);
		space.take(je, 0L);
		je.bytes = fileHandler.getJarAsBytes(jarFile);
		space.write(je, ENTRY_LEASE);	
	}

	
	private void updateClassEntries(File jarFile) {
		List<String> classNames = fileHandler.getClassNames(jarFile);
		for (String className : classNames) {
			updateClassEntry(jarFile, className);
		}

	}
	
	//	 ensure both the class and the source jar are matched
	private void updateClassEntry(File jarFile, String className) {
		FlyClassEntry ce = new FlyClassEntry();
		ce.className = className;
		ce.jarName = jarFile.getName();	
		
		System.out.println("Updating class entry " + ce);
		space.take(ce, 0L);
		space.write(ce, ENTRY_LEASE);		
	}
	
}
