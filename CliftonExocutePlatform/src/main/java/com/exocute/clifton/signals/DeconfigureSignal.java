package com.exocute.clifton.signals;

import java.io.Serializable;

@SuppressWarnings("serial")

public class DeconfigureSignal implements Serializable {
	
	private String pipelineInstance;

	public String getPipelineInstance() {
		return pipelineInstance;
	}

	public void setPipelineInstance(String pipelineInstance) {
		this.pipelineInstance = pipelineInstance;
	}
	
	public String toString() {
		return "Deconfigure " + pipelineInstance;
	}

}
