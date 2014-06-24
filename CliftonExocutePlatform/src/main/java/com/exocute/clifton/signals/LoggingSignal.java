package com.exocute.clifton.signals;

import java.io.Serializable;


@SuppressWarnings("serial")


public class LoggingSignal implements Serializable 
{
	
	private int logLevel;
	
	private String logMessage;
	
	/**
	 * Set the logging level for signals to the Clifton Node
	 */
	public int getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}

	
	/** 
	 * Set and get the message for error monitoring and reporting signals
	 */
	public String getLogMessage() {
		return logMessage;
	}

	public void setLogMessage(String logMessage) {
		this.logMessage = logMessage;
	}

}
