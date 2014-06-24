package com.exocute.clifton.pipeline;


import java.io.Serializable;

import com.exocute.clifton.node.ExoEntry;

import com.exocute.clifton.node.SpaceCache;

import com.exocute.clifton.signals.Markers;
import com.exocute.pipeline.CollectException;
import com.exocute.pipeline.Collector;
import com.zink.fly.FlyPrime;




public class CliftonCollector implements Collector {
	
	// and cache the template
	ExoEntry tmpl = new ExoEntry();
	

	public CliftonCollector(String marker)
		{
		// set up the template.
		tmpl.marker = marker;
		tmpl.payload = null;
		}


	public final  Serializable collect() throws CollectException 
		{
		return collect(0L);
		}
	
	
	public final Serializable collect(long waitTime) throws CollectException 
		{
		// TODO - This should be all spaces not just random one.
		FlyPrime space = SpaceCache.getDataSpace();
		
		ExoEntry ent = null;
		try {
			ent =  (ExoEntry)space.take(tmpl,  waitTime);
			} 
		catch (Exception exp) 
			{
			throw new CollectException("Collector error", exp);
			}	 
		
		// copy out the returned object if there is a reply object
		Serializable ret = null;
		if (ent != null) ret = ent.payload;
		return ret;
		}

	
	public Serializable[] collect(int numObjects, long waitTime) throws CollectException
		{
		//**** TODO 
		Serializable [] ret = new Serializable[numObjects];
		long start = System.currentTimeMillis();
		long remainingTime = waitTime;
		int totalObjects = 0;
		
		//
		while (totalObjects < numObjects && remainingTime > 1l)
			{
			Serializable serializable = collect();
			if (serializable!=null) {
				ret[totalObjects] = serializable;
				totalObjects++;
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			remainingTime =  waitTime - (System.currentTimeMillis() - start);
			}
		
		// If we got less object than we wanted  but the time ran out 
		// then we copy the objects into an array of the right size
		// so that there arnt any gaps at the end when we send it 
		// back to the client.
		if (totalObjects != numObjects)
			{
			Serializable [] temp = new Serializable[totalObjects];
			// copy into the array of the correct size
			System.arraycopy(ret, 0, temp, 0, totalObjects);
			ret = temp;
			}
		return ret;
		}

}
