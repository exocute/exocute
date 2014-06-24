package com.exocute.clifton.signals;

import java.io.Serializable;


@SuppressWarnings("serial")


public class ActivitySignal implements Serializable 
{
	
	private String activity;

	private String inMarker;
	
	private String outMarker;


	
	public String getActivityName() {
		return activity;
	}
	public void setActivityName(String _activity) {
		this.activity = _activity;
	}
	
	public String getInMarker() {
		return inMarker;
	}
	public void setInMarker(String inMarker) {
		this.inMarker = inMarker;
	}

	public String getOutMarker() {
		return outMarker;
	}
	public void setOutMarker(String outMarker) {
		this.outMarker = outMarker;
	}

	
	public String toString()
	{
		StringBuilder ret = new StringBuilder(64);
		ret.append("Activity :" + activity + "\n"
				+ "In  " +  inMarker + "\n"
				+ "Out " + outMarker + "\n");
		
		return ret.toString();
	}
	
}
