package com.exocute.clifton.node;

import java.lang.reflect.Method;
import java.util.Iterator;

public class NodeStarter {

	/**
	 * @param args
	 */
	// NodeStarter -signalhost 192.168.1.1 -datahost 192.168.1.1 -jarhost 192.168.1.1 
	public static void main(String[] args) {
		Object node = null;
		//String flyHost = null;
		
		String signalHost=null;
		String dataHost=null;
		String jarHost=null;
		
		// ITERATE THROUGH ARGS	
		// two states . . operator, operand . . if not, then fail
	
		
//		if (args.length==1) {
//			flyHost=args[0];
//			CliftonClassLoader.setFlyHost(flyHost);
//		}
		
		if (args.length>=0) {
			// two states: 0=operator, 1=operand
			int state=0;
			String op=null;
			
			// iterate over args
			for (String arg : args) {
				if (state==0) {
					if (!arg.startsWith("--")) {
						failWithInvalidOptions();
					}
					if (arg.equals("--signalhost")) {
						op = "signal";
					}
					if (arg.equals("--datahost")) {
						op = "data";
					}
					if (arg.equals("--jarhost")) {
						op = "jar";
					}
					state=1; // waiting for operand
				} else if (state==1) {
					if (arg.startsWith("--")) {
						failWithInvalidOptions();
					}
					if(op.equals("signal")) {
						signalHost=arg;
					}
					if(op.equals("data")) {
						dataHost=arg;
					}
					if(op.equals("jar")) {
						jarHost=arg;
					}
					state=0;
				}
			} 
		}
		
		System.out.print("Finding Signal Space");
		if (signalHost==null) {
			System.out.println(" using broadcast");
		} else {
			System.out.println(" at " + signalHost);
		}
		System.out.print("Finding Jar Space");
		if (jarHost==null) {
			System.out.println(" using broadcast");
		} else {
			System.out.println(" at " + jarHost);
		}
		System.out.print("Finding Data Space");
		if (dataHost==null) {
			System.out.println(" using broadcast");
		} else {
			System.out.println(" at " + dataHost);
		}
		
		SpaceCache.setJarHost(jarHost);
		
		ClassLoader classLoader = new CliftonClassLoader();
		

		// first make sure the space stub is loaded by the ACL
		try {
			Class clazz = Class.forName("com.exocute.clifton.node.CliftonNode" , false, classLoader);
			
			java.lang.reflect.Constructor ctor = clazz.getConstructor(new Class[] {String.class, String.class, String.class});
			node = ctor.newInstance(new Object[] {signalHost, dataHost, jarHost});
			
//			Simon commented out this stuff when he added the ability to specify a hostname for the space
//			To be honest, he wasn't that sure of reason for it as we never pass any command line parameters to
//			the NodeStarter.  It is but a simple job to reinstate it if necessary.
//			if (args.length==0) {
//				node = clazz.newInstance();
//			} else {
//				java.lang.reflect.Constructor ctor = clazz.getConstructor(new Class[] {String.class});
//				node = ctor.newInstance(new Object[] {args[0]});
//			}
		
			// Set the ContextClassLoader for the Thread
			Method method = node.getClass().getMethod("setContextClassLoader", new Class[] {ClassLoader.class});
			method.invoke(node, new Object[] {classLoader});
			
			// . . and start it.
		    method = node.getClass().getMethod("start", new Class[] {});
			method.invoke(node, new Object[] {});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // load class
	


	}
	
	private interface SetVal {
		public void set(String val);
	}

	private static void failWithInvalidOptions() {
		System.out.println("Usage: NodeStarter [--signalhost <addr>] [--datahost <addr>] [--jarhost <addr>]");
		System.exit(-1);
	}

}
