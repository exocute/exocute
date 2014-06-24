package com.exocute.clifton.node;

import com.zink.fly.FlyPrime;

public class DataOutChannel extends OutChannel {

	public DataOutChannel(String marker) {
		super(marker);
	}

	@Override
	public FlyPrime getSpace() {
		return SpaceCache.getDataSpace();
	}

}
