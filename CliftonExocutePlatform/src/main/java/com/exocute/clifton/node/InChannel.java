package com.exocute.clifton.node;



import java.io.Serializable;

import com.zink.fly.FlyPrime;

public abstract class InChannel {

	// many things can happen in a millisecond and we need to be fair to all of
	// the other clients
	private static long TAKE_TIME = 0;

	// make all of these configurable
	private FlyPrime space = null;
	private ExoEntry tmpl = null;
	
	public abstract FlyPrime getSpace();
	
	// Set up this InputChannel for a particular marker
	// Set up this channel for a particular marker 
	public InChannel(String marker) {
		// go to the space chance and get a random Space
		space = getSpace();
		
		// now setup up the template  
		tmpl = new ExoEntry();
		tmpl.marker = marker;
		tmpl.payload = null;
	}
	

	
	public Serializable getObject() {
		// TODO guard the lack of space pointer both here and in the constructor
		if (space != null) {
			ExoEntry inputEntry = null;
			try {
				inputEntry = space.take(tmpl, TAKE_TIME);
			} catch (Exception e) {
				e.printStackTrace();
				// **********************************
				// looks like the channel is busted
				// in a better world try to fix it
				// **********************************
			}

			if (inputEntry != null) {
				TAKE_TIME=0;
				try {
					return inputEntry.payload;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else { TAKE_TIME=1000; }
		}
		return null;
	}

}
