
package com.exocute.clifton.node;

import java.io.Serializable;




/**
 * This FlyPrimes entry is the only one we use is simple has 
 * Marker - the marker for the currnet pipeline and state
 * Serialiazble - Whatever people want to put in it to carry around.
 * 
 * It also as an implied location for example a specific exocute client
 * , Enode, Exocute router or FlyPrime that it is currently living in.
 * 
 * @author nigel
 *
 */
public class ExoEntry 
	{
		private static final long serialVersionUID = 5590977814215863628L;
		
		public String marker;
		
		public Serializable payload;
		
	}

