package com.exocute.clifton.signals;

import java.io.Serializable;



@SuppressWarnings("serial")

public class KillSignal implements Serializable 
{
	
	private boolean _killNow;

	
	public void setKillNow() { _killNow = true; }
		
	public void setKillGracefull() { _killNow = false; }

	
	public boolean isKillNow() { return _killNow; }
	
	public boolean isKillGracefull() { return !_killNow; }
	
}
