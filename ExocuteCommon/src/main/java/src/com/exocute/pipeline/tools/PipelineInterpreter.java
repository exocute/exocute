
package com.exocute.pipeline.tools;


import java.io.IOException;
import java.io.LineNumberReader;




/*
 * The Pipeleine Interpreter takes a LineNumberReader that wraps a .pln file.
 * and generates a object structure represent the pipeline as java objects.
 * 
 */


public class PipelineInterpreter {
	
	private LineNumberReader lnr = null;
	
	// other stuff like severity etc.
 
	
	private PipelineRep pipeline = null;
	private ActivityRep activity = null;
	
		

	public PipelineInterpreter(LineNumberReader lnr)
		{
		setSource(lnr);
		}
	
	
	// and perhaps one that takes a string and wraps a line number reader that way
	
	public void setSource(LineNumberReader lnr)
		{
		this.lnr = lnr;
		}
	
	
	
	public PipelineRep createPipeline()
		{
		// if lnr != null
		//TODO Log.log(Log.LOGGING, lnr.getLineNumber(), "Parsing incoming pln definition");
		
		
		// read each line 
		String currentLine = null;
		try {
			while ( ( currentLine=lnr.readLine() )  != null )
				{
				// remove the comments
				String [] tokens = currentLine.split("//");
				// remove leading and trailing white space
				if (tokens.length != 0)
					{
					String trimedLine = tokens[0].trim();
				
					// there is definitly a token or two so parse the line
					
					if ( !(trimedLine.equals("")) )
						{
						// Now tokenize the real instructions
						String [] commands = trimedLine.split(" ", 2);
				
						if (commands.length > 1) parseLine(commands[0], commands[1]);
						else parseLine(commands[0],"");
					
						}

					}
				
				}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (state == PRE_PIPELINE)
			{
			String txt = "Pipeline not defined at end of file";
//			TODO Log.log(Log.ERROR, lnr.getLineNumber(), txt);
			}
		
		if (state == IN_PIPELINE)
			{
			String txt = "Activity not defined at end of file";
			//TODO Log.log(Log.ERROR, lnr.getLineNumber(), txt);
			}
		
		int acts = pipeline.getActivities().size();
		String txt = "Created Pipeline with "+acts; 
		if (acts == 1) txt += " Activity";
		else txt += " Activities";
		//TODO Log.log(Log.LOGGING, lnr.getLineNumber(),txt);
		
		// now check the inputs and ouputs
	
		return pipeline;
		
		}
	
	
	private static final int PRE_PIPELINE = 1;
	private static final int IN_PIPELINE = 2;
	private static final int IN_ACTIVITY = 3;

	private int state = PRE_PIPELINE;
	
	
	private void parseLine( String key, String value )
		{
		// tidy the inputs 
		key.trim();
		value.trim();
		
		// use the state machine to parse the inputs 
		// First pipeline has not been defined.
		if (state == PRE_PIPELINE)
			{
			if (key.equalsIgnoreCase("Pipeline"))
				{
				pipeline = new PipelineRep();
				pipeline.setName(value);
//				TODO Log.log(Log.LOGGING, lnr.getLineNumber(), "Created Pipeline named :" + value);
				state = IN_PIPELINE;
				}
			else 
				{
				String txt = "Expected token Pipeline - Got " + key;
//				TODO Log.log(Log.ERROR, lnr.getLineNumber(), txt);
				}
			}
		
		// Pipeline is created so looking for Import, Export or Activity
		else if (state == IN_PIPELINE)
			{
			boolean goodKey = false;
			// check for the Import token
			if (key.equalsIgnoreCase("Import"))
				{
				pipeline.setImportName(value);
//				TODO Log.log(Log.LOGGING,lnr.getLineNumber(),"Set Pipeline import to :" + value);
				goodKey = true;
				}
			// check for the Export token
			if (key.equalsIgnoreCase("Export"))
				{
				pipeline.setExportName(value);
//				TODO Log.log(Log.LOGGING,lnr.getLineNumber(),"Set Pipeline export to :" + value);
				goodKey = true;
				}
			// check for Activity token
			if (key.equalsIgnoreCase("Activity"))
				{
				activity = new ActivityRep();
				// now split the name and the parameters
				String [] tokens = value.split(":",2);
				activity.setName(tokens[0]);
				
//				TODO Log.log(Log.LOGGING,lnr.getLineNumber(),"Set Activity name to :"+tokens[0]);
				
				// if there are any parameters set them or the empty string
				String params = "";
				if (tokens.length > 1) params = tokens[1];
//				TODO Log.log(Log.LOGGING,lnr.getLineNumber(),"Set Activity parameters to :"+params);
				activity.setParameters(params);	
				pipeline.addActivity(activity);
				state = IN_ACTIVITY;
				goodKey = true;
				}
			
			if (!goodKey)
				{
				String txt = "Expected : Activity:Import:Export - Got " + key;
//				TODO Log.log(Log.ERROR, lnr.getLineNumber(), txt);
				}
			}
		
		// Activity is created so looking for Import, Export or another Activity
		else if (state == IN_ACTIVITY)
			{
			boolean goodKey = false;
			// check for the Import token
			if (key.equalsIgnoreCase("Import"))
				{
				activity.setImportName(value);
//				TODO Log.log(Log.LOGGING, lnr.getLineNumber(), "Set Activity import to :" + value);
				goodKey = true;
				}
			// check for the Export token
			if (key.equalsIgnoreCase("Export"))
				{
				pipeline.setExportName(value);
//				TODO Log.log(Log.LOGGING, lnr.getLineNumber(), "Set Activity export to :" + value);
				goodKey = true;
				}
			// check for Activity token
			if (key.equalsIgnoreCase("Activity"))
				{
				activity = new ActivityRep();
				// now split the name and the parameters
				String [] tokens = value.split(":",2);
				activity.setName(tokens[0]);
				
//				TODO Log.log(Log.LOGGING,lnr.getLineNumber(),"Set Activity Name to :"+tokens[0]);
				
				// if there are any parameters set them or the empty string
				String params = "";
				if (tokens.length > 1)
					{
					params = tokens[1];
//					TODO Log.log(Log.LOGGING,lnr.getLineNumber(),"Set Activity parameters to :"+tokens[1]);
					}
				activity.setParameters(params);	
				pipeline.addActivity(activity);
				goodKey = true;
				}
			if (!goodKey)
				{
				String txt = "Expected : Activity:Import:Export - Got " + key;
//				TODO Log.log(Log.ERROR, lnr.getLineNumber(), txt);
				}
			}
		
		// and youre back in the room.	
	}

}
