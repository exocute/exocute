package com.exocute.pipeline;

import java.io.Serializable;

/** I provide the means to submit something to a Pipeline */
public interface Injector {

	/** I inject a new thing into this Injector's Pipeline.  The thing injected must be of type java.io.Serializeable */
	void inject(Serializable individual) throws InjectException;
	
	void inject(int occurences, Serializable input) throws InjectException;
	void inject(Serializable [] inputs) throws InjectException;
	
}
