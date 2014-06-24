package com.exocute.classdistributer;

public abstract class JarUpdater {

	public void update(String directory, String[] jarFiles) {
		for (String jarFile : jarFiles) {
			update(directory,jarFile);
		}
	}
	
	public abstract void update(String directory, String jarFile);
	
}
