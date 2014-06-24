package com.exocute.pipeline;

import java.io.Serializable;

/** I provide the means of retrieving something from a Pipeline */
public interface Collector {

	/** I collect a single result from the Pipeline
	 * @return a Serializable result or null if no result was available */ 
	public Serializable collect() throws CollectException;

	/** I behave like {@link #collect() collect()} except that I wait for a maximum of millis milliseconds before returning.
	 *  In effect, collect(0) is the same as {@link #collect() collect()}.
	 * @param millis The maximum number of milliseconds to wait
	 * @return a Serializable result or null if no result was available
	 */
	public Serializable collect(long millis) throws CollectException;
	
	Serializable [] collect(int numObjects, long waitTime) throws CollectException;
}
