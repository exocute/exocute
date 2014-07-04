
package com.exocute.pipeline;

/** 
 * I am a PipelineToolkit that allows a user to interact with a Pipeline.
 * Primarily, this is done by using an {@link com.exocute.pipeline.Injector Injector} which provides a means of 
 * submitting things into the pipeline, or a {@link com.exocute.pipeline.Collector Collector} that allows things to be
 * removed from a Pipeline.
 */
public interface Pipeline {

	/** I return an {@link com.exocute.pipeline.Injector Injector} for this Pipeline */
	public Injector injector();
	
	/** I return a {@link com.exocute.pipeline.Collector Collector} for this Pipeline */
	public Collector collector();

}
