package com.exocute.clifton.pipeline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;

import com.exocute.clifton.node.SpaceCache;
import com.exocute.pipeline.Collector;
import com.exocute.pipeline.Injector;
import com.exocute.pipeline.Pipeline;
import com.exocute.pipeline.tools.PipelineInterpreter;
import com.exocute.pipeline.tools.PipelineRep;



public class CliftonPipeline implements Pipeline
{
	private PipelineRep pipelineRep;
	
	private CliftonInjector injector;
	
	private CliftonCollector collector;
	
	/** 
	 * This Ctor takes a valid pipeline file (.pln file)
	 * @param A valid java.io.File
	 */
	public CliftonPipeline(File file, String signalHost, String dataHost) {
		// put the now valid file in a Pipeline interpreter to get a Valid 
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
			init(fileReader, signalHost, dataHost);
			} 
		catch (FileNotFoundException e) 
			{
			e.printStackTrace();
			System.exit(1);
			}
	}
	
	/** 
	 * This Ctor takes a valid pipeline file (.pln file)
	 * @param A valid java.io.File
	 */
	public CliftonPipeline(File file) {
		this(file, null, null);
	}
	

	/** This Ctor takes some text that represents the 
	 * Pipleline in the same form as you would expect to find in
	 * a pipeline file (.pln file)
	 * @param A valid java String
	 */
	public CliftonPipeline(String pipelineAsText, String signalHost, String dataHost) 
		{
		Reader stringReader = new StringReader(pipelineAsText);
		init(stringReader, signalHost, dataHost);
	}
	
	public CliftonPipeline(String pipelineAsText) 
	{
		Reader stringReader = new StringReader(pipelineAsText);
		init(stringReader, null, null);
	}
	
	/** This Ctor takes a pipelineRep which may be been 'built'
	 * by the calling client program. If you are using .pln files 
	 * or text then please use the other consturctors which take 
	 * a java Native file or java Strings.
	 * @param A valid PipelineRep
	 */
	public CliftonPipeline(PipelineRep pipelineRep, String signalHost, String dataHost)
		{
		init(pipelineRep, signalHost, dataHost);
	}

	public CliftonPipeline(PipelineRep pipelineRep)
	{
		init(pipelineRep, null, null);
	}
	
	
	/** 
	 * 'super' init for the ctors that take a pipeline in file
	 * or text form
	 */
	private void init(Reader reader, String signalHost, String dataHost)
		{
		// get the line number reader working to give a reader thing
		LineNumberReader lnr = new LineNumberReader(reader);
		PipelineInterpreter itprt = new PipelineInterpreter(lnr);
		
		// and init the pipeline with the the PipleineRep 
		// created from the incoming text
		init(itprt.createPipeline(), signalHost, dataHost);
		}
	
	
	
	/**
	 * Private init to start the pipeline
	 * @param pipelineRep
	 */
	private void init(PipelineRep pipelineRep, String signalHost, String dataHost) {
		if (signalHost!=null) {
			SpaceCache.setSignalHost(signalHost);
		}
		if (dataHost!=null) {
			SpaceCache.setDataHost(dataHost);
		}
		
		// store the pipeline rep for later reference.
		this.pipelineRep = pipelineRep;
		
		// user the pipeline builder to build the pipeline
		PipelineCreator pipeCreator = new PipelineCreator();
		
		// TODO - this should start a thread and do all of the pipeline 
		// Management as we go forward.
		pipeCreator.injectPipeline(pipelineRep);
		
		injector = new CliftonInjector(pipeCreator.getInjectMarker());
		collector = new CliftonCollector(pipeCreator.getCollectMarker());		
	}
		
		
	public Injector injector() {
		return injector; 
	}

	
	public Collector collector() {
		return collector;
	}
	
	
}
