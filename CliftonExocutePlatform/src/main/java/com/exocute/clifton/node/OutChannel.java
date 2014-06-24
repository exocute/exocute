package com.exocute.clifton.node;

import java.io.Serializable;


import com.zink.fly.FlyPrime;



public abstract class OutChannel 
{

	// make all of these configurable 
	private long entryLifetime = 60*1000;  // by default let the entry live for a minute
	private FlyPrime space = null;
	private ExoEntry outEntry = null;
	
	public abstract FlyPrime getSpace();

	// Set up this channel for a particular marker 
	public OutChannel(String marker)
		{
		// go to the space chance and get a random Space
		space = getSpace();
		
		// now setup up the template  
		outEntry = new ExoEntry();
		outEntry.marker = marker;
		}
	
	
	// Set up this channel for a particular marker and entry lifetime.
	public OutChannel(String marker, long entryLifetime)
		{
		this(marker);
		this.entryLifetime = entryLifetime;
		}


	public boolean putObject(Serializable object) 
		{		
		// TODO Way more error checking and output required
		if (space != null && outEntry.marker != null && object!=null)
			{
			outEntry.payload = object;
			try { space.write(outEntry, entryLifetime);	} 
			catch (Exception e) 
				{ System.out.println("ouput channel broken"); }

			}
		return true;
		}	

	
}
