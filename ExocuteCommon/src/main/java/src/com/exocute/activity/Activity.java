package com.exocute.activity;



import java.io.Serializable;


public interface Activity 
{
	
	
	/**
	 * This is where you do anything you want to do to transform an object (i.e. change its state and return it)
	 * or to create new object and send it on its way.
	 * 
	 * The atk is there just in case you want to do groovey things like supply paramters to your processor, 
	 * or send objects out of band on a seperate channel. 
	 *
	 * @param  input The Serializable input object
	 * @param  ptk The Serializable input object
	 * 
	 * @return output The Serializbale output object
	 */
	public abstract Serializable process(Serializable input, ActivityToolKit atk);
	
	
}

