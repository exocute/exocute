
package com.exocute.clifton.pipeline;



import java.util.List;
import java.util.UUID;

import com.exocute.clifton.node.OutChannel;
import com.exocute.clifton.node.SignalOutChannel;
import com.exocute.clifton.signals.ActivitySignal;
import com.exocute.clifton.signals.Markers;
import com.exocute.pipeline.tools.ActivityRep;
import com.exocute.pipeline.tools.PipelineRep;


/**
 * 
 * Pipeline Creator takes a representation of a Pipeline (a PipelineRep)
 * and uses the SpaceCache to inject a set of Signals into Clifton to 
 * create and manage the pipeline as it runs.
 * 
 * @author nigel
 *
 */

public class PipelineCreator 
{
	
	private String injectMarker;
	private String collectMarker;
	
	
	public void injectPipeline(PipelineRep graph)
		{
		// prepare an output channel	
		OutChannel outChannel = new SignalOutChannel(Markers.inoSignal);

		// cache some of the core pipeline info
		String pipeName = graph.getName();
		int stages = graph.getActivities().size();
		System.out.println("Pipeline has " + stages + " stages");
	    List acts = graph.getActivities();
	    
		// make a unique instance for this pipeline.
		String pipelineInstance = pipeName + ":" + generateUID() + ":";
		injectMarker = pipelineInstance+">";
		collectMarker = pipelineInstance+"<";
	    
        // work through each activity in the pipeline
		for (int i = 0; i < stages; i++)
			{
			ActivitySignal signal = new ActivitySignal();
			ActivityRep actRep = (ActivityRep)acts.get(i);
			signal.setActivityName( actRep.getName() + ":" + actRep.getParameters() );
					
			// if it is the first stage then mark it for the pipeline injector
			// or mark with the stage number
			if (i == 0) signal.setInMarker(injectMarker);
			else signal.setInMarker(pipelineInstance+i);
			
			// if it is the last stage then mark it for the collector or 
			// mark with the stage number
			if (i == (stages-1)) signal.setOutMarker(collectMarker);
			else signal.setOutMarker(pipelineInstance+(i+1));
			
			// inject the resulting signal into the platform
			outChannel.putObject(signal);
			System.out.print("Sending signal : ");
			System.out.println(signal.toString());
			}
      
		}
	
	
	public String getInjectMarker() {
		return injectMarker;
	}


	public String getCollectMarker() {
		return collectMarker;
	}
	
	
	private String generateUID() {
			return UUID.randomUUID().toString();
	}






		
}
