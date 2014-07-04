
package com.exocute.clifton.node;

import java.io.Serializable;

import com.exocute.clifton.signals.ActivitySignal;
import com.exocute.clifton.signals.KillSignal;
import com.exocute.clifton.signals.LoggingSignal;
import com.exocute.clifton.signals.Markers;


/**
 * An CliftonNode is used by the second version of the Exocute platform as the 'worker' processor
 * node. 
 *  
 *  CliftonNode - Is a vanilla java (J2SE) 'main' application that is simply a client of Fly
 *  and simply uses channels to read and write objects to Fly which is the 'gue' that holds
 *  the cellular processing instructions together.
 *  
 *  The worker process connects to the Spaces via channels which mask both the dynamic space lookups
 *  performed by the SpaceCache and the marker matching processes.
 *  
 * 
 * @author nigel
 *
 */


public class CliftonNode extends Thread {
	
	private static final long MIN_INTER_SIGNAL_SLEEP_TIME = 10;
	private static final long MAX_INTER_SIGNAL_SLEEP_TIME = 1000;
	
	private String id;
	
	public CliftonNode(String signalHost, String dataHost, String jarHost) {
		this("UNKNOWN", signalHost, dataHost, jarHost);
	}
	
	public CliftonNode(String id, String signalHost, String dataHost, String jarHost) {
		this.id = id;
		SpaceCache.setSignalHost(signalHost);
		SpaceCache.setDataHost(dataHost);
		SpaceCache.setJarHost(jarHost);
	}

	
	/**
	 * @param args
	 */
	public void run() {
		
		final long lifeTime = 1 * 60 * 60 * 1000; // One hour to live
		final long bootTime = System.currentTimeMillis();
		final long dieTime = bootTime + lifeTime;
		
		long interSignalSleepTime = MIN_INTER_SIGNAL_SLEEP_TIME; 		// millis
			
		// set up  channels to look for global input and send output signals
		InChannel sigInChannel = new SignalInChannel(Markers.inoSignal);
		
	
		// set the signal strategy
		SignalStrategy strat = SignalStrategy.instance();
		
		
		// the worker thread associated with this CliftonNode
		Worker worker = null;

		strat.event(SignalStrategy.NodeEvent.BOOT, id );
		
		
		// only live for a given time
		
		while (System.currentTimeMillis() < dieTime )
			{
			
			// see if there is an incoming signal
			Serializable signal = sigInChannel.getObject();
			
			// if so try to act on it.
			if (signal != null)
				{
				//reset interSignalSleepTime
				interSignalSleepTime = MIN_INTER_SIGNAL_SLEEP_TIME;
				// if it is an activity signal and the worker is busy then ignore the 
				// signal
				if ((worker == null || !worker.isAlive()) && signal instanceof com.exocute.clifton.signals.ActivitySignal)
					{
					strat.event(SignalStrategy.NodeEvent.ACTIVITY_SIGNAL, signal);
					
					// now program the worker to act on the signal
					ActivitySignal config = (ActivitySignal) signal;
					
					// check for existence of a current worker
					if (worker != null && worker.isAlive())
						{
						// ask worker to stop running when ready 
						worker.setRunning(false);
						while ( worker.isAlive() );
						}
					
					// worker is either non exisitent or dead at this point
					// so we can simply make a new one and wait for the GC 
					// to collect the old one.
					worker  = new Worker(config);
					worker.start();
					}
				
				
				if (signal instanceof com.exocute.clifton.signals.KillSignal)
					{
					strat.event(SignalStrategy.NodeEvent.KILL_SIGNAL, signal);
					KillSignal kill = (KillSignal)signal;
					
					
					// check for existence of a current worker
					if (worker != null && worker.isAlive() && kill.isKillGracefull())
						{
						// ask worker to stop running when ready 
						worker.setRunning(false);
						// and to finish
						while ( worker.isAlive() );
						}
					strat.event(SignalStrategy.NodeEvent.NODE_DEAD, id);
					System.exit(1);
					}
		
				
				if (signal instanceof com.exocute.clifton.signals.LoggingSignal)
					{
					strat.event(SignalStrategy.NodeEvent.LOG_SIGNAL, signal);
					LoggingSignal logSig = (LoggingSignal)signal;
					Log.setLogLevel(logSig.getLogLevel());
					}
			
				// now sleep a while before consuming the next signal.
				try { Thread.sleep(interSignalSleepTime); }
				catch(Exception exp) { Log.error(exp.toString()); }
				} else {
					// ramp up the interSignalSleepTime to avoid CPU spinning
					interSignalSleepTime = Math.min(interSignalSleepTime+10, MAX_INTER_SIGNAL_SLEEP_TIME);
				}
			}
		
		strat.event(SignalStrategy.NodeEvent.NODE_TIMEOUT, id);
		
		// do graceful shutdown so
		// ask worker to stop running when ready 
		worker.setRunning(false);
		// and to finish
		while ( worker.isAlive() );
		
		// finally signal it out of here.
		strat.event(SignalStrategy.NodeEvent.NODE_DEAD, id);	
		}
	
}


