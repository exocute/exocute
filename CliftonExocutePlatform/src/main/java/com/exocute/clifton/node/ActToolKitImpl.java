package com.exocute.clifton.node;

import com.exocute.activity.ActivityToolKit;



public class ActToolKitImpl implements ActivityToolKit {

	
	private String [] _params = { "" };

	
	public void setParams(String [] parameters)
	{
		_params = parameters;	
	}
	
	public void clearParams()
	{
		_params= new String[] { "" };
	}
	
	
	public String[] getParameters() 
	{
		return _params;
	}

	
	
}
