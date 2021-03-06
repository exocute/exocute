package com.exocute.clifton.node;

import com.zink.fly.FlyPrime;

public class SignalOutChannel extends OutChannel {

	public SignalOutChannel(String marker) {
		super(marker);
	}

	@Override
	public FlyPrime getSpace() {
		return SpaceCache.getSignalSpace();
	}

}
