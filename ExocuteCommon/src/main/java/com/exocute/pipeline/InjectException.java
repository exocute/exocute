package com.exocute.pipeline;

@SuppressWarnings("serial")

public class InjectException extends Exception {

	public InjectException(String message) 
		{ super(message); }

	public InjectException(String message, Throwable cause) 
		{ super (message, cause); }
	
}
