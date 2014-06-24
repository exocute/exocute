
package com.exocute.clifton.node;



import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

import com.exocute.activity.Activity;
import com.exocute.classdistributer.FlyClassEntry;
import com.exocute.classdistributer.FlyJarEntry;
import com.zink.fly.Fly;
import com.zink.fly.kit.FlyFinder;



public class ActivityCache {


	
	private static Map _cache = null;
	

	// load up the cache with the default activities
	private static final void init()
	{
		// set up the map implementing the cache.
		_cache = new TreeMap();

	}
	
	
	public static Activity getActivity(String name)
		{
		if (_cache == null) {
			init();
		}
		
		// first check if we have it in the cache
		Activity activity = (Activity)_cache.get(name);
		
		// if not then try to get the code from the JarSpace
		if (activity == null)
			{
			try {
	        	// go get the required jar
	        	byte [] jar = CliftonClassLoader.getJarFromSpace(name); 
	        	if (jar != null)
	        		{
	        		Object acl = ActivityCache.class.getClassLoader();
	        		
	        		Method initMethod = acl.getClass().getMethod("init", new Class[] {(new byte[1]).getClass()});
	        		initMethod.invoke(acl, new Object[] {jar});
	        		// make a class loader for the jar
	        		
	        		Class clazz = Class.forName(name); // load class
	        		activity = (Activity) clazz.newInstance(); 
	        		_cache.put(activity.getClass().getName(),activity);
	        		}
	        	}
			
			catch (Exception e) { e.printStackTrace(); }
			}
		
		return activity;
		}
	
}
