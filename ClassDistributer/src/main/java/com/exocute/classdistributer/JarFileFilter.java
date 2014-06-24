
package com.exocute.classdistributer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

/** 
 * The jar scanner looks at the jars directory replies with any new or updated jar files.
 *  
 * @author nigel
 *
 */
public class JarFileFilter implements FilenameFilter {
	
	/**
	 * Maps the name of the jar file to its creation date
	 */
	private Map<String, Long>  jarMap = new HashMap<String, Long>();	
	
	/** 
	 * Select only the new or updated jars
	 */
	public boolean accept(File dir, String name)
		{  
		boolean isNewOrModified = false;
		if (name.endsWith(".jar")) {
			File file = new File(dir, name);
			long lastModified = file.lastModified();
			Long date = jarMap.get(name);
			if (date == null || date.longValue() != lastModified) {
				// its a new jar file, add it to the timestamp map.
				jarMap.put(name, new Long(lastModified) );
				isNewOrModified = Boolean.TRUE;
			} 
		}
		return isNewOrModified; 
	}

	
}
