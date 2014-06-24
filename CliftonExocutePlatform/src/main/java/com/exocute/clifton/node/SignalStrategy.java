package com.exocute.clifton.node;


import com.exocute.clifton.signals.ActivitySignal;
import com.exocute.clifton.signals.Markers;


public class SignalStrategy {
	
	static public enum NodeEvent {   	
							BOOT,  
							ACTIVITY_SIGNAL,
							LOG_SIGNAL,
							KILL_SIGNAL,
								FIRST_JOB,
								CONSECUTIVE_JOB,
								BUSY_PROCESSING,
								ACTIVITY_TIMEOUT,
							NODE_TIMEOUT,
							NODE_DEAD
	};
	
	
	private static SignalStrategy me;
	
	// any signals from here are internal
	private OutChannel outChannel  = new SignalOutChannel(Markers.inoSignal);
	
	private ActivitySignal activitySignal;
	
	
	private SignalStrategy() {
		super();
	}
	
	public synchronized static SignalStrategy instance() {
		if (me == null) {
			me = new SignalStrategy();
		}
		return me;
	}

	
	public void setOutChannel(OutChannel sigOutChannel) {
		outChannel = sigOutChannel;	
	}

	
	public void event(NodeEvent nodeEvent) {
		event(nodeEvent, null);
	}
	

	public void event(NodeEvent nodeEvent, Object extra) {
		
		// the node will call back to here for all major state changes etc
		String logMsg = nodeEvent.toString();
		if (extra != null) logMsg = logMsg + ":" + extra.toString();
		Log.info(logMsg);
		
		// now do the signal specific behavior
		
		// if it an activity signal then store it because we may want to send it
		// below.
		if (nodeEvent == NodeEvent.ACTIVITY_SIGNAL) {
			activitySignal = (ActivitySignal)extra;
		}
		
		
		// in this strategy we rebroadcast the signal when there are 
		// consecutive jobs.
		if (nodeEvent == NodeEvent.CONSECUTIVE_JOB || nodeEvent == NodeEvent.BUSY_PROCESSING) {
			// if this is a Consecutive Job event then the extra parameter
			// is an ActivitySignal. If not at least force runtime error.
			outChannel.putObject(activitySignal);	
		}
				
	}


}
