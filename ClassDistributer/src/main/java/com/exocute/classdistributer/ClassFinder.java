/*
*
*/

package com.exocute.classdistributer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;

import com.zink.fly.Fly;
import com.zink.fly.kit.FlyFinder;



public class ClassFinder {

	/**
	 * Start the ClassDistributer with the directory given as a parameter.
	 * @param args - the path the exocute jars directory
	 * @throws InterruptedException 
	 */
	public static void main(String [] args) throws InterruptedException {
		
		 final String TAG = "Exocute";
		 Fly space;
		
	
		FlyFinder finder = new FlyFinder();
		space = finder.find(new String[] { TAG });
		if (space == null) {
			throw new RuntimeException("Cant find space with [" + TAG + "]");
		}
	
	
		System.out.print("Please enter class to find on space :");
		String className = "NoSet";
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			className = br.readLine();
		} catch (Exception exp) {
			System.err.println(exp.getMessage());
			System.exit(1); 
		}

		
		System.out.println("Looking for ["+ className +"]");
		FlyJarEntry je = new FlyJarEntry();
		
		FlyClassEntry ce = new FlyClassEntry();
		ce.className = null;
		ce.jarName = null;
		Collection<FlyClassEntry> entries = space.readMany(ce,10000000);
		System.out.println("Found " + entries.size() + " classes in jar space");
		for (FlyClassEntry e : entries) {
			if (e.className.contains(className)) {
				System.out.println("Found [" + e.className + "] in jar [" + e.jarName + "]" );
			}
		}
		System.out.println("Done");	
	}
	
	
}
