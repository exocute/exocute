package com.exocute.clifton.node;

import com.zink.fly.FlyPrime;

public class SignalInChannel extends InChannel {

	public SignalInChannel(String marker) {
		super(marker);
		// TODO Auto-generated constructor stub
	}

	@Override
	public FlyPrime getSpace() {
		
		return SpaceCache.getSignalSpace();
	}

}
