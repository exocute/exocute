package com.exocute.clifton.pipeline;

import java.io.File;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Queue;

import scala.actors.threadpool.LinkedBlockingQueue;
import scala.collection.mutable.LinkedList;

import com.exocute.pipeline.CollectException;
import com.exocute.pipeline.Collector;
import com.exocute.pipeline.InjectException;
import com.exocute.pipeline.Injector;
import com.exocute.pipeline.tools.PipelineRep;

public class AdaptivePipeline extends CliftonPipeline implements Injector, Collector {
	private QueuingInjector injector;
	private long iCount = 0;
	private long cCount = 0;
	private long monitorInterval = 100;
	private long throttleThreshold = 100; // should be automated as a function of throughput or payload size

	private class Monitor extends Thread {
		private Monitor() {
			super("Adaptive Injector Collector Monitor");
		}
		public void run() {
			long current = 0;
			long previous = 0;
			int unchanged = 0;
			double rate = 10.0;

			while (true) {
				try {
					Thread.sleep(monitorInterval);
					if (injector.getQueuingEnabled()) {
						System.out.println("#Q="+injector.size());
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// Outstanding Things
				previous = current;
				current = outstanding();
				
				System.out.println("Prev " + previous + "Current " + current);

				// if number outstanding is increasing we need to introduce a Queue, and introduce more activity signals
				// to encourage more workers. 
				if (current > throttleThreshold) {
					injector.startQueuing();

					// TODO Introduce Extra Signals
				}

				// if number outstanding is reducing, then increase the injection rate because framework has capacity
				if (current < previous || unchanged > 1) {
					rate++;
					unchanged = 0;
				}

				if (current > previous) {
					rate--;
				}
				
				// mechanism to increase the rate if the collection rate is unchanged, but we don't want it to just keep increasing if everything has stalled
				// TODO: Might need to change the condition so that the rate does not increase if the size of the queue is increasing.
				if (current == previous) {
					unchanged++;
				}

				injector.setRate(rate);

			}
		}
	}

	private static class QueuingInjector implements Injector {
		private static final Queue<Serializable> throttleQueue = new LinkedBlockingQueue<Serializable>();
		private Injector directInjector;
		private Injector indirectInjector;
		private Injector activeInjector;
		private long nowInjected = 0;
		private long lastInjected = 0;
		private long nowTime = 0;
		private long lastTime = 0;
		private double rate = 1.0;
		private double actualRate = 0.0;
		private boolean queuingEnabled = false;
		
		/*
		 * This injector offers payloads to a local queue, which is drained by another thread at
		 * an appropriaye rate.
		 */
		private class IndirectInjector implements Injector {

			public void inject(Serializable individual) throws InjectException {	
				throttleQueue.offer(individual);
			}

			public void inject(int occurences, Serializable input)
					throws InjectException {
				for (int i=1;i<=occurences;i++) {
					throttleQueue.offer(input);
				}
			}

			public void inject(Serializable[] inputs) throws InjectException {
				for (Serializable input: inputs) {
					throttleQueue.offer(input);
				}
			}
		}
		
		/* 
		 * Constructor initialises queue, and configures injector to inject payloads into queue
		 */
		private QueuingInjector(final Injector injector) {
			directInjector = injector;
			indirectInjector = new IndirectInjector();
			startQueuing();
		}

		public boolean getQueuingEnabled() {
			return queuingEnabled ;
		}

		private synchronized void startQueuing() {
			synchronized (throttleQueue) {
				if (!getQueuingEnabled()) {
					System.out.println("Starting Queuing");
					queuingEnabled=true;
					activeInjector = indirectInjector;
					Thread rateCalculator = new Thread("Rate Calculator") {
						public void run() {
							while (!throttleQueue.isEmpty() || getQueuingEnabled()) {
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								nowTime = System.currentTimeMillis();
								//System.out.println("Time: Now" + nowTime + " Last," + lastTime + " Injected Now" + nowInjected + " Last" + lastInjected);
								actualRate = (nowInjected - lastInjected) / ((nowTime - lastTime)/1000.0);
								lastTime = nowTime;
								lastInjected = nowInjected;
							}
						}
					};
					
					// create a thread which drains the queue at the appropriate rate until
					// the queue is empty.
					Thread throttler = new Thread("Throttler") {
						public void run() {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							while (!throttleQueue.isEmpty() || getQueuingEnabled()) {
								System.out.println("Actual " + actualRate + "Desired " + rate);
								Serializable payload;
								if (actualRate<rate) {
									try {
										payload = throttleQueue.poll();
										if (payload!=null) {
											directInjector.inject(payload);
											++nowInjected;
										}
									} catch (InjectException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
									System.out.println("Throttling: " + "Actual " + actualRate + "Desired " + rate);
								}
							}
							// Once Queue is drained, switch back to direct injector again
							synchronized (throttleQueue) {
								queuingEnabled=false;
								activeInjector = directInjector;
								System.out.println("Queue Disabled");
							}
						}
					};
					rateCalculator.start();
					throttler.start();
				}
			}
		}
		
		public int size() {
			return throttleQueue.size();
		}

		public void setRate(double rate) {
			this.rate  = Math.max(rate,1);
		}

		public void inject(Serializable individual) throws InjectException {
			activeInjector.inject(individual);
		}

		public void inject(int occurences, Serializable input)
				throws InjectException {
			activeInjector.inject(occurences, input);

		}

		public void inject(Serializable[] inputs) throws InjectException {
			activeInjector.inject(inputs);

		}
	}

	public AdaptivePipeline(File file) {
		super(file);
		initialiseAdaptiveInjectors();
	}
	
	public AdaptivePipeline(File file, String signalHost, String dataHost) {
		super(file, signalHost, dataHost);
		initialiseAdaptiveInjectors();
	}

	public AdaptivePipeline(String pipelineAsText, String signalHost, String dataHost) 
	{
		super(pipelineAsText, signalHost, dataHost);
		initialiseAdaptiveInjectors();
	}

	public AdaptivePipeline(String pipelineAsText) 
	{	
		super(pipelineAsText);
		initialiseAdaptiveInjectors();
	}

	/** This Ctor takes a pipelineRep which may be been 'built'
	 * by the calling client program. If you are using .pln files 
	 * or text then please use the other consturctors which take 
	 * a java Native file or java Strings.
	 * @param A valid PipelineRep
	 */
	public AdaptivePipeline(PipelineRep pipelineRep, String signalHost, String dataHost)
	{
		super(pipelineRep, signalHost, dataHost);
		initialiseAdaptiveInjectors();
	}

	public AdaptivePipeline(PipelineRep pipelineRep)
	{
		super(pipelineRep);
		initialiseAdaptiveInjectors();
	}

	private void initialiseAdaptiveInjectors() {
		injector = new QueuingInjector(super.injector());
		Thread monitor = new Monitor();
		monitor.setDaemon(true);
		monitor.start();
	}


	public Serializable collect() throws CollectException {
		Serializable ret = super.collector().collect();
		if (ret!=null) {
			cCount++;
		}
		return ret;
	}

	public Serializable collect(long millis) throws CollectException {
		Serializable ret = super.collector().collect(millis);
		if (ret!=null) {
			cCount++;
		}
		return ret;
	}

	public Serializable[] collect(int numObjects, long waitTime)
			throws CollectException {
		Serializable ret[] = super.collector().collect(numObjects, waitTime);
		cCount+=ret.length;
		return ret;
	}

	public void inject(Serializable individual) throws InjectException {
		injector.inject(individual);
		iCount+=1;
	}

	public void inject(int occurences, Serializable input)
			throws InjectException {	

		injector.inject(occurences, input);
		iCount+=occurences;
	}

	public void inject(Serializable[] inputs) throws InjectException {
		injector.inject(inputs);
		iCount+=inputs.length;
	}

	public long injected() {
		return iCount;
	}

	public long collected() {
		return cCount;
	}

	public long outstanding() {
		return iCount-cCount;
	}

	@Override
	public Injector injector() {
		return this;
	}

	@Override 
	public Collector collector() {
		return this;
	}
}
