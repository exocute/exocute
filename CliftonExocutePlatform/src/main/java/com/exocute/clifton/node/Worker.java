package com.exocute.clifton.node;

import java.io.Serializable;

import com.exocute.clifton.signals.ActivitySignal;



public class Worker extends Thread {
	
	/**
	 * Set up the basic components of the Worker
	 */
	private NodeHub _hub;
	private InChannel _dataIn;
	private OutChannel _dataOut;
	
	private InChannel _signalIn;
	private OutChannel _signalOut;
	
	
	// status variables 
	private boolean _running = true;
	private boolean _inProcess = false;
	private long _time = System.currentTimeMillis();

	private boolean firstJob = true;
	
	// set up this worker
	public Worker(ActivitySignal actDes)
		{
		// set up the hub to sit the Activity in
		_hub = new NodeHub();
		_hub.setActivity(actDes.getActivityName());
		
		// set the markers on the channels		
		try {
			_dataIn = new DataInChannel(actDes.getInMarker()); 
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			_dataOut = new DataOutChannel(actDes.getOutMarker());
		}

		
	// status 
	public synchronized void setRunning(boolean running) { _running = running; }
	
	public synchronized boolean isInProcess() { return _inProcess; }
	
	public synchronized long lastOperation() 
		{ 
		if (_inProcess) return 0;
		return System.currentTimeMillis() - _time;
		}
	
	public synchronized String activityName() { return _hub.getActivityName(); }
	
	
	// This worker will sleep for a max of 1 minute
	// clearing the decks and stopping if there is nothing to do
	private final long sleepMax = 1 * 60 * 1000;
	
	
	public void run()
		{	
		SignalStrategy strat = SignalStrategy.instance();
		
		// the sleep time gets backed off if there is no input to the activities
		long sleepTime = 1;
		
		while (_running)
			{
			Serializable in =_dataIn.getObject();		
			
			if (in != null)
				{
				// Signal if this is the first Job
				if (firstJob) {
					strat.event(SignalStrategy.NodeEvent.FIRST_JOB);
				}
				
				// is this a consecutive job
				if (sleepTime == 1 && !firstJob ) {
				    strat.event(SignalStrategy.NodeEvent.CONSECUTIVE_JOB);
				}
			
				// lock this worker from most intruptions
				_inProcess = true;
				
				BusySignalThread busySignalThread = new BusySignalThread(strat);
				busySignalThread.start();
				
				Serializable out = _hub.process(in);
				busySignalThread.interrupt();
				
				_dataOut.putObject(out);
				_inProcess = false;
				_time = System.currentTimeMillis();
				
				// clear the decks 
				sleepTime = 1;
				firstJob = false;
				}
			else
				{
				// there wasnt anything at the input so sleep with a 
				// binary back off pattern. 
				// It is also very legitimate to inturrpt any sleep on this 
				// thread  need some more clever sleep mangement here using the _time 
				// varbiable and some maths and stuff
				try { Thread.sleep(sleepTime); } 
				catch (InterruptedException e) {  }
				sleepTime = sleepTime * 2;
				if (sleepTime > sleepMax) _running = false;
				}	
			}
		strat.event(SignalStrategy.NodeEvent.ACTIVITY_TIMEOUT);
		// end of thread run loop.
		}
	
	private class BusySignalThread extends Thread {
		private boolean stop = false;
		private SignalStrategy strat;
		
		private BusySignalThread(SignalStrategy strat) {
			super();
			this.strat = strat;
		}
		
		@Override 
		
		public void run() {
			while (!stop) {
				try {
					Thread.sleep(1000);
					strat.event(SignalStrategy.NodeEvent.BUSY_PROCESSING);
				} catch (InterruptedException e) {
					// If I'm interupted I can stop
					finish();
				}
			}
		}

		public void finish() {
			stop = true;
		}
		
	}
		
	
}
