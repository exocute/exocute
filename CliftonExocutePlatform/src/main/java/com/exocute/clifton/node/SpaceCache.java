package com.exocute.clifton.node;

import java.util.HashMap;
import java.util.Map;

import com.zink.fly.Fly;
import com.zink.fly.FlyPrime;
import com.zink.fly.kit.FlyFactory;
import com.zink.fly.kit.FlyFinder;

public class SpaceCache {

	private static Map<String, Fly> spaceMap = new HashMap<String, Fly>();

	private static String signalHost = null;
	private static String dataHost = null;
	private static String jarHost = null;

	private static final String SIGNALTAG = "SignalSpace";
	private static final String DATATAG = "DataSpace";
	private static final String JARTAG = "JarSpace";

	public static FlyPrime getSignalSpace() {
		return getSpace(SIGNALTAG, signalHost);
	}

	public static FlyPrime getDataSpace() {
		return getSpace(DATATAG, dataHost);
	}

	public static FlyPrime getJarSpace() {
		return getSpace(JARTAG, jarHost);
	}

	private static FlyPrime getSpace(String tag, String flyHost) {
		// go find one
		// if (space == null) {
		Fly space = null;
		if (!spaceMap.containsKey(tag)) {
			try {
				if (flyHost == null) {
					FlyFinder finder = new FlyFinder();
					space = finder.find(new String[] { tag });
				} else {
					space = FlyFactory.makeFly(flyHost);
				}
			} catch (Exception e) {
				Log.error(e.toString());
			}
			if (space == null) {
				Log.error("Failed to locate space");
			}
		} else {
			space = spaceMap.get(tag);
		}
		spaceMap.put(tag, space);
		return space;
	}

	public static void setDataHost(String host) {
		dataHost = host;
	}

	public static void setSignalHost(String host) {
		signalHost = host;
	}

	public static void setJarHost(String host) {
		jarHost = host;
	}

}