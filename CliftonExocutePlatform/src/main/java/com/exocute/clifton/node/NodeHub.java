package com.exocute.clifton.node;


import java.io.Serializable;

import com.exocute.activity.Activity;



public class NodeHub 
{

	private Activity _activity = null;
	private ActToolKitImpl _atk  = new ActToolKitImpl();

	
	void setActivity(String activity)
		{
		// get the activity string and parse it to make the activity fly.
		String [] activityTokens = activity.split(":");
		String activityName = activityTokens[0];
		
		int paramCount = activityTokens.length-1;
		
		if (paramCount < 1) _atk.clearParams();
		else 
			{
			String[] params = new String[paramCount];
			System.arraycopy(activityTokens,1,params,0,paramCount);
			_atk.setParams(params);
			}

		// The cache will also try to load the class
		_activity = ActivityCache.getActivity(activityName);
		}
	
	
	public String getActivityName()
		{
		return _activity.getClass().getName();
		}
	
	public Activity getActivity() {
		return _activity;
	}
	
	
	public Serializable process(Serializable in)	{
		return _activity.process(in,_atk);
		}
	
	
}
