package com.exocute.clifton.pipeline;

import java.io.Serializable;

import com.exocute.clifton.node.ExoEntry;
import com.exocute.clifton.node.SpaceCache;
import com.exocute.pipeline.InjectException;
import com.exocute.pipeline.Injector;
import com.zink.fly.FlyPrime;



public class CliftonInjector implements Injector {

	private static long INJECTION_LEASE  = 2 * 60 * 1000;
	
	// pre-bake the Entry to send
	ExoEntry ent = new ExoEntry();

	public CliftonInjector(String marker)
		{
		ent.marker = marker;
		}
	

	// need to also make one entry that is copied rather than creating 
	// one each time.
	public void inject(Serializable input) throws InjectException {
		
		// put the input object into the pre-baked entry
		ent.payload = input;
		
		FlyPrime space = SpaceCache.getDataSpace();
		
//System.out.println("injecting " + ent.toString());
		
	    try {   space.write(ent, INJECTION_LEASE);  } 
	    catch (Exception exp)  
	    	{  throw new InjectException("Internal inject error" , exp);  }
		}
		
	
	public void inject(int occurences, Serializable input)  throws InjectException
		{
		if (occurences < 1) throw new InjectException("Too few occurences");
		for (int i = 0; i < occurences; i++) inject(input);
		}

	
	public void inject(Serializable[] inputs) throws InjectException
		{
		for (int i = 0; i < inputs.length; i++) inject(inputs[i]);
		}


}
