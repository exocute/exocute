
package com.exocute.clifton.node;

import com.exocute.clifton.signals.LoggingSignal;
import com.exocute.clifton.signals.Markers;



public class Log {
	
	//
	// Logging can have one of two values
	// In Info mode a whole load of information is propagated 
	// for debugging and monitoring purposes.
	// In Error mode only errors are propagated.
	//
	public static final int INFO = 1;
	public static final int ERROR = 0;
	
	private static int logLevel = INFO;  
	
	private static OutChannel outChannel =  new SignalOutChannel(Markers.exoSignal);
	
	private static boolean ALSO_LOCAL = true;
	
	/*
	 * Only write this message if the Info level is set
	 */
	public static final void info(String msg){
		if (logLevel == INFO) sendMessage(msg); 
		return;
	}
	
	
	/** 
	 * Write this message no matter what
	 */
	public static final void error(String msg) {
		sendMessage(msg);
		return;
	}
	
	
	/**
	 * Set or reset the logging level
	 * @param level
	 */
	public static final void setLogLevel(int level) {
		if ( level == INFO ||  level == ERROR ) logLevel = level;
		else {
			error("Attempt to set logging level set to wierd value");
		}
		info("Set logging level to " + logLevel);
	}

	
	
	private static LoggingSignal logSig = new LoggingSignal(); 
	
	
	private static void sendMessage(String msg) {

		logSig.setLogLevel(logLevel);
		logSig.setLogMessage(formatMessage(msg));
		outChannel.putObject(logSig);

		if (ALSO_LOCAL)  {
			System.out.println(formatMessage(msg));
		}	
	}

	
	// create these object only once
	private static StringBuilder sb = new StringBuilder();
	
	private static final String formatMessage(String msg) {
		// check an exception and catch the resulting stuff
		StackTraceElement [] stack = new Throwable().getStackTrace();
		sb.setLength(0);
		sb.append(stack[4].getClassName());
		sb.append(":");
		sb.append(stack[4].getMethodName());
		sb.append(":");
		sb.append(stack[4].getLineNumber());
		sb.append(":");
		return sb + msg ;
	}
	
	
	// very simple tests for stack traces etc.
	public static void main(String [] args) {
		Log.info("Info");
		Log.error("Error");
		
		Log.setLogLevel(Log.INFO);
		
		Log.info("Info");
		Log.error("Error");
	}
	
	
}




