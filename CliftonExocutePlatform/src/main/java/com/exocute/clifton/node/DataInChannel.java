package com.exocute.clifton.node;

import com.zink.fly.FlyPrime;

public class DataInChannel extends InChannel {

	public DataInChannel(String marker) {
		super(marker);
	}

	@Override
	public FlyPrime getSpace() {
		return SpaceCache.getDataSpace();
	}

}
